package com.echoriff.echoriff.favorite.domain.usecase

import com.echoriff.echoriff.favorite.domain.LikedSongsState

interface FetchLikedSongsUseCase {
    suspend fun fetchLikedSongs(): LikedSongsState

}