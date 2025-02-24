package com.echoriff.echoriff.radio.domain

import com.echoriff.echoriff.radio.domain.model.Song

sealed class LikedSongsState {
    data object Loading : LikedSongsState()
    data class Success(
        val likedSongs: List<Song>
    ) : LikedSongsState()

    data class Failure(
        val messageError: String
    ) : LikedSongsState()
}