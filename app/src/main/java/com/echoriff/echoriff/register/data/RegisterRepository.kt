package com.echoriff.echoriff.register.data

import com.echoriff.echoriff.register.domain.RegisterState

interface RegisterRepository {
    suspend fun registerUser(firstName: String, lastName: String, email: String, password: String): RegisterState
}
