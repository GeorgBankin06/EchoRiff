package com.echoriff.echoriff.login.domain.usecase

import com.echoriff.echoriff.login.data.LoginRepository
import com.echoriff.echoriff.login.domain.LoginState

class LoginUseCaseImpl(private val repository: LoginRepository): LoginUseCase {
    override suspend fun login(email: String, password: String): LoginState {
        return repository.login(email, password)
    }
}