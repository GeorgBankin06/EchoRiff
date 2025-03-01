package com.echoriff.echoriff.login.data

import com.echoriff.echoriff.login.domain.LoginState

interface LoginRepository {
    suspend fun login(email: String, password: String): LoginState
}