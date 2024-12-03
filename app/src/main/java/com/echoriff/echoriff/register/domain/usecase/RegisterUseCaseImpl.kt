package com.echoriff.echoriff.register.domain.usecase

import com.echoriff.echoriff.register.data.RegisterRepository
import com.echoriff.echoriff.register.domain.RegisterState

class RegisterUseCaseImpl(private val repository: RegisterRepository): RegisterUseCase {
    override suspend fun registerUser(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): RegisterState {
        return repository.registerUser(firstName, lastName, email, password)
    }
}
