package com.echoriff.echoriff.radio.domain

sealed class RadioState {
    data object Loading : RadioState()
    data class Success(
        val messageSuccess: String
    ) : RadioState()
    data class Failure(
        val messageError: String
    ) : RadioState()
}
