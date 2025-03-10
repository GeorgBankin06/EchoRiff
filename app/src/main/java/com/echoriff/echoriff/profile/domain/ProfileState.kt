package com.echoriff.echoriff.profile.domain

import com.echoriff.echoriff.common.domain.User

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val user: User) : ProfileState()
    data class Failure(val error: String) : ProfileState()
}