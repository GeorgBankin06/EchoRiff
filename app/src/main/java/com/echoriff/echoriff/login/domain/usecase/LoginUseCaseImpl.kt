package com.echoriff.echoriff.login.domain.usecase

import com.echoriff.echoriff.login.data.LoginRepository
import com.echoriff.echoriff.login.domain.LoginState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginUseCaseImpl(
    private val repository: LoginRepository
) : LoginUseCase {

    override suspend fun login(email: String, password: String): LoginState {
        return withContext(Dispatchers.IO) {
            try {
                repository.login(email, password)
            } catch (e: Exception) {
                LoginState.Failure(e.message ?: "Unknown error")
            }
        }
    }
}