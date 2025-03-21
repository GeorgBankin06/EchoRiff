package com.echoriff.echoriff.profile.data

import android.net.Uri
import com.echoriff.echoriff.common.domain.User
import com.echoriff.echoriff.profile.domain.EditState
import com.echoriff.echoriff.profile.domain.ProfileState

interface ProfileRepository {
    suspend fun fetchUserInfo(): ProfileState
    suspend fun updateUserInfo(
        user: User,
        newProfileImageUri: Uri?,
    ): EditState
}