package com.echoriff.echoriff.register.data

import com.echoriff.echoriff.common.Constants
import com.echoriff.echoriff.common.UserPreferences
import com.echoriff.echoriff.register.domain.RegisterState
import com.echoriff.echoriff.register.domain.User
import com.google.firebase.auth.FirebaseAuth
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

        if(existingUser != null){
            return RegisterState.Failure("Email already exists.")
        }

        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: return RegisterState.Failure("User creation failed")

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

            userPreferences.saveUserRole(role)

            RegisterState.Success(user)
        } catch (e: Exception) {
            RegisterState.Failure(e.message ?: "Unknown error")
        }
    }

    private suspend fun checkIfEmailExists(email: String): User? {
        // Check Firebase Authentication for the email
        val user = try {
            auth.fetchSignInMethodsForEmail(email).await()
            // If user exists in auth, we return that user
            true
        } catch (e: Exception) {
            // If an error occurs, email does not exist in authentication
            false
        }

        // Return null if no user exists
        return if (user) {
            // Check Firestore if the user already exists
            val snapshot = firestore.collection("users")
                .whereEqualTo("email", email)
                .get()
                .await()

            if (snapshot.documents.isNotEmpty()) {
                snapshot.documents[0].toObject(User::class.java)
            } else {
                null
            }
        } else {
            null
        }
    }
}
