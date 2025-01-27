package com.echoriff.echoriff.radio.domain

sealed class SongState {
    object Loading : SongState()
    data class Success(val message: String) : SongState()
    data class Failure(val error: String) : SongState()
}