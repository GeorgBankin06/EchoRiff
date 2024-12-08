package com.echoriff.echoriff.register.domain

import com.echoriff.echoriff.common.domain.User

sealed class RegisterState {
    data class Success(val user: User) : RegisterState()
    data class Failure(val errorMessage: String) : RegisterState()
    data object Loading : RegisterState()
}