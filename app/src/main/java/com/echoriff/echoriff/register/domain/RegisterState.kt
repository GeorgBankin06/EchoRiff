package com.echoriff.echoriff.register.domain

sealed class RegisterState {
    data class Success(val user: User) : RegisterState()
    data class Failure(val errorMessage: String) : RegisterState()
    data object Loading : RegisterState()
}