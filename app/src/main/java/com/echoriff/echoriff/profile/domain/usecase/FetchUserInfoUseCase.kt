package com.echoriff.echoriff.profile.domain.usecase

import com.echoriff.echoriff.profile.domain.ProfileState

interface FetchUserInfoUseCase {
    suspend fun fetchUserInfo(): ProfileState
}