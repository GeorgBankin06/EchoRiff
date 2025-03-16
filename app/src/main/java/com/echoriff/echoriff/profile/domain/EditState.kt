package com.echoriff.echoriff.profile.domain

import com.echoriff.echoriff.common.domain.User

sealed class EditState {
    object Loading : EditState()
    data class Success(val message: String) : EditState()
    data class Failure(val error: String) : EditState()
}