package com.echoriff.echoriff.login.data

interface LoginRepository {
    suspend fun loginUser(email: String, password: String): Result<Boolean>
}