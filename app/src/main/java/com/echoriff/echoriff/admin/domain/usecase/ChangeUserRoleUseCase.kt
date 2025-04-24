package com.echoriff.echoriff.admin.domain.usecase

import com.echoriff.echoriff.admin.domain.ChangeUserRoleState

interface ChangeUserRoleUseCase {
    suspend fun changeUserRole(email: String, role: String): ChangeUserRoleState
}