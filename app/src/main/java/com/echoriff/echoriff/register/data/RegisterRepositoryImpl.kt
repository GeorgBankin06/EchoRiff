package com.echoriff.echoriff.register.data

import com.echoriff.echoriff.common.Constants
import com.echoriff.echoriff.common.domain.UserPreferences
import com.echoriff.echoriff.register.domain.RegisterState
import com.echoriff.echoriff.common.domain.User
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class RegisterRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val userPreferences: UserPreferences
) : RegisterRepository {

    private val adminEmail = Constants.ADMIN_EMAIL

    override suspend fun registerUser(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): RegisterState {

        val existingUser = checkIfEmailExists(email)
        if (existingUser != null) {
            return RegisterState.Failure("The email is already registered. Please use a different email.")
        }

        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: return RegisterState.Failure("User creation failed.")

            val role = if (email == adminEmail) "admin" else "user"

            // Create a user object to save to Firestore
            val user = User(
                userId = userId,
                email = email,
                firstName = firstName,
                lastName = lastName,
                role = role
            )

            // Save user info to Firestore
            firestore
                .collection(Constants.USERS)
                .document(userId)
                .set(user)
                .await()

            // Save the user role in preferences
            userPreferences.saveUserRole(role)

            RegisterState.Success(user)
        } catch (e: Exception) {
            handleFirebaseError(e)
        }
    }

    private suspend fun checkIfEmailExists(email: String): User? {
        // Check Firebase Authentication for the email
        val userExistsInAuth = try {
            val signInMethods = auth.fetchSignInMethodsForEmail(email).await()
            signInMethods.signInMethods?.isNotEmpty() ?: false
        } catch (e: Exception) {
            false
        }

        // If user exists in Firebase Auth, check Firestore for more details
        return if (userExistsInAuth) {
            val snapshot = firestore.collection(Constants.USERS)
                .whereEqualTo("email", email)
                .get()
                .await()

            if (snapshot.documents.isNotEmpty()) {
                snapshot.documents[0].toObject(User::class.java)
            } else null
        } else null
    }

    private fun handleFirebaseError(e: Exception): RegisterState {
        return when (e) {
            is FirebaseAuthInvalidCredentialsException -> {
                RegisterState.Failure("Invalid email or password. Please check your input.")
            }
            is FirebaseAuthUserCollisionException -> {
                RegisterState.Failure("This email is already in use. Please try logging in instead.")
            }
            is FirebaseNetworkException -> {
                RegisterState.Failure("Please check your internet connection and try again.")
            }
            is FirebaseAuthException -> {
                when (e.errorCode) {
                    "ERROR_EMAIL_ALREADY_IN_USE" -> {
                        RegisterState.Failure("This email is already registered. Please use a different email.")
                    }
                    "ERROR_WEAK_PASSWORD" -> {
                        RegisterState.Failure("The password is too weak. Please use a stronger password.")
                    }
                    "ERROR_INVALID_EMAIL" -> {
                        RegisterState.Failure("The email address is badly formatted. Please check and try again.")
                    }
                    else -> {
                        RegisterState.Failure("Authentication error: ${e.localizedMessage}")
                    }
                }
            }
            else -> {
                RegisterState.Failure("An unexpected error occurred: ${e.localizedMessage}")
            }
        }
    }
}
