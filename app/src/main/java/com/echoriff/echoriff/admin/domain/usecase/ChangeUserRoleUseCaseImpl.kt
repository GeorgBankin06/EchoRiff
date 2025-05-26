package com.echoriff.echoriff.admin.domain.usecase

import com.echoriff.echoriff.admin.data.AdminRepository
import com.echoriff.echoriff.admin.domain.ChangeUserRoleState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChangeUserRoleUseCaseImpl(private val repository: AdminRepository) : ChangeUserRoleUseCase {
    override suspend fun changeUserRole(email: String, role: String): ChangeUserRoleState {
        return withContext(Dispatchers.IO) {
            try{
                repository.changeUserRole(email, role)
            } catch (e: Exception){
                ChangeUserRoleState.Failure(e.message ?: "Unknown error")
            }
        }
    }
}
