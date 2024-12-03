package com.echoriff.echoriff.register.domain.usecase

import com.echoriff.echoriff.register.domain.RegisterState

interface RegisterUseCase {
    suspend fun registerUser(firstName: String, lastName: String, email: String, password: String): RegisterState
}