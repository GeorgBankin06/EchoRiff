package com.echoriff.echoriff.favorite.domain.usecase

import com.echoriff.echoriff.favorite.data.FavoriteRepository
import com.echoriff.echoriff.radio.data.RadioRepository
import com.echoriff.echoriff.favorite.domain.LikedSongsState

class FetchLikedSongsUseCaseImpl(private val repository: FavoriteRepository) : FetchLikedSongsUseCase {
    override suspend fun fetchLikedSongs(): LikedSongsState {
        return repository.fetchLikedSongs()
    }
}