package com.echoriff.echoriff.radio.domain

import com.echoriff.echoriff.radio.domain.model.Radio

sealed class LikedRadiosState {
    data object Loading : LikedRadiosState()
    data class Success(
        val likedRadios: List<Radio>
    ) : LikedRadiosState()

    data class Failure(
        val messageError: String
    ) : LikedRadiosState()
}