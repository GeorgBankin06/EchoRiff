package com.echoriff.echoriff.profile.domain.usecase

import android.net.Uri
import com.echoriff.echoriff.common.domain.User
import com.echoriff.echoriff.profile.data.ProfileRepository
import com.echoriff.echoriff.profile.domain.EditState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class UpdateUserInfoUseCaseImpl(private val repository: ProfileRepository) : UpdateUserInfoUseCase {
    override suspend fun updateUserInfo(
        user: User,
        newProfileImageUri: Uri?,
    ): EditState {
        return withContext(Dispatchers.IO) {
            try {
                repository.updateUserInfo(user, newProfileImageUri)
            } catch (e: Exception) {
                EditState.Failure(e.message ?: "Unknown error")
            }
        }
    }
}