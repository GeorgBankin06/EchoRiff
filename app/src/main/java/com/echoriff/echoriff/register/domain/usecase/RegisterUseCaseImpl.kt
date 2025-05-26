package com.echoriff.echoriff.register.domain.usecase

import com.echoriff.echoriff.register.data.RegisterRepository
import com.echoriff.echoriff.register.domain.RegisterState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RegisterUseCaseImpl(private val repository: RegisterRepository): RegisterUseCase {
    override suspend fun registerUser(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): RegisterState {
        return withContext(Dispatchers.IO){
            try {
                repository.registerUser(firstName, lastName, email, password)
            }catch (e: Exception){
                RegisterState.Failure(e.message ?: "Unknown error")
            }
        }
    }
}
