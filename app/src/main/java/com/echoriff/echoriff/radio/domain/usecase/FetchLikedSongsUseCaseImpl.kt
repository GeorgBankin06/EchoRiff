package com.echoriff.echoriff.radio.domain.usecase

import com.echoriff.echoriff.radio.data.RadioRepository
import com.echoriff.echoriff.radio.domain.LikedSongsState

class FetchLikedSongsUseCaseImpl(private val repository: RadioRepository) : FetchLikedSongsUseCase {
    override suspend fun fetchLikedSongs(): LikedSongsState {
        return repository.fetchLikedSongs()
    }
}