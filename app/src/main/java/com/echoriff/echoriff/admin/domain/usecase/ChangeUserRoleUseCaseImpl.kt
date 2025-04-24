package com.echoriff.echoriff.admin.domain.usecase

import com.echoriff.echoriff.admin.data.AdminRepository
import com.echoriff.echoriff.admin.domain.ChangeUserRoleState

class ChangeUserRoleUseCaseImpl(private val repository: AdminRepository) : ChangeUserRoleUseCase {
    override suspend fun changeUserRole(email: String, role: String): ChangeUserRoleState {
        return repository.changeUserRole(email, role)
    }
}