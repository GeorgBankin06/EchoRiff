package com.echoriff.echoriff.login.domain.usecase

import com.echoriff.echoriff.login.domain.LoginState

interface LoginUseCase {
    suspend fun login(email: String, password: String): LoginState
}