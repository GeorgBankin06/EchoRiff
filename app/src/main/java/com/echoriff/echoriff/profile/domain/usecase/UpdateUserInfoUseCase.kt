package com.echoriff.echoriff.profile.domain.usecase

import android.net.Uri
import com.echoriff.echoriff.common.domain.User
import com.echoriff.echoriff.profile.domain.EditState

interface UpdateUserInfoUseCase {
    suspend fun updateUserInfo(
        user: User,
        newProfileImageUri: Uri?,
    ): EditState
}