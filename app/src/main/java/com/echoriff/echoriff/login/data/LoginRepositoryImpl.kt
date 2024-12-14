package com.echoriff.echoriff.login.data

import com.echoriff.echoriff.common.Constants
import com.echoriff.echoriff.login.domain.LoginState
import com.google.firebase.auth.FirebaseAuth
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
            LoginState.Failure(e.localizedMessage ?: "Error occurred")
        }
    }
}
