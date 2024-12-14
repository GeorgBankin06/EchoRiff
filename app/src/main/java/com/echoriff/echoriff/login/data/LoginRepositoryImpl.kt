package com.echoriff.echoriff.login.data

import com.echoriff.echoriff.login.domain.LoginState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class LoginRepositoryImpl(private val auth: FirebaseAuth) : LoginRepository {
    override suspend fun login(email: String, password: String): LoginState {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            LoginState.Success
        }catch (e: Exception){
            LoginState.Failure(e.localizedMessage ?: "Error occurred")
        }
    }
}
