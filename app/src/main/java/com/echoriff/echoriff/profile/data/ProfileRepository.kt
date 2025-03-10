package com.echoriff.echoriff.profile.data

import com.echoriff.echoriff.profile.domain.ProfileState

interface ProfileRepository {
    suspend fun fetchUserInfo(): ProfileState
}