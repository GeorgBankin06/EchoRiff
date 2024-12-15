package com.echoriff.echoriff.login.data

import com.echoriff.echoriff.common.Constants
import com.echoriff.echoriff.login.domain.LoginState
import com.echoriff.echoriff.register.domain.RegisterState
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class LoginRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : LoginRepository {
    override suspend fun login(email: String, password: String): LoginState {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: return LoginState.Failure("User not found")

            val userDoc = firestore.collection(Constants.USERS).document(userId).get().await()
            val role = userDoc.getString("role") ?: "User"

            LoginState.Success(role)
        } catch (e: Exception) {
            handleFirebaseError(e)
        }
    }

    private fun handleFirebaseError(e: Exception): LoginState {
        return when (e) {
            is FirebaseAuthInvalidCredentialsException -> {
                LoginState.Failure("Invalid email or password. Please check your input.")
            }

            is FirebaseAuthUserCollisionException -> {
                LoginState.Failure("This email is already in use. Please try logging in instead.")
            }

            is FirebaseNetworkException -> {
                LoginState.Failure("Please check your internet connection and try again.")
            }

            is FirebaseAuthException -> {
                when (e.errorCode) {
                    "ERROR_EMAIL_ALREADY_IN_USE" -> {
                        LoginState.Failure("This email is already registered. Please use a different email.")
                    }

                    "ERROR_WEAK_PASSWORD" -> {
                        LoginState.Failure("The password is too weak. Please use a stronger password.")
                    }

                    "ERROR_INVALID_EMAIL" -> {
                        LoginState.Failure("The email address is badly formatted. Please check and try again.")
                    }

                    else -> {
                        LoginState.Failure("Authentication error: ${e.localizedMessage}")
                    }
                }
            }

            else -> {
                LoginState.Failure("An unexpected error occurred: ${e.localizedMessage}")
            }
        }
    }
}
