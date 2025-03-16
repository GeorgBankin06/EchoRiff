package com.echoriff.echoriff.profile.domain.usecase

import android.net.Uri
import com.echoriff.echoriff.common.domain.User
import com.echoriff.echoriff.profile.data.ProfileRepository
import com.echoriff.echoriff.profile.domain.EditState

class UpdateUserInfoUseCaseImpl(private val repository: ProfileRepository) : UpdateUserInfoUseCase {
    override suspend fun updateUserInfo(
        user: User,
        newProfileImageUri: Uri?,
    ): EditState {
        return repository.updateUserInfo(user, newProfileImageUri)
    }
}