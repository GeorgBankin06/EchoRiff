package com.echoriff.echoriff.login.domain

sealed class LoginState {
    data object Idle : LoginState()
    data object Loading : LoginState()
    data object Success : LoginState()
    data class Failure(val message: String) : LoginState()
}