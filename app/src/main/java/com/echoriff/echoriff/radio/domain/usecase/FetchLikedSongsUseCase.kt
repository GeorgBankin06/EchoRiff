package com.echoriff.echoriff.radio.domain.usecase

import com.echoriff.echoriff.radio.domain.LikedSongsState

interface FetchLikedSongsUseCase {
    suspend fun fetchLikedSongs(): LikedSongsState

}