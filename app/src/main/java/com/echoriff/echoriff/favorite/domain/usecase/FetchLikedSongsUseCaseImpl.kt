package com.echoriff.echoriff.favorite.domain.usecase

import com.echoriff.echoriff.favorite.data.FavoriteRepository
import com.echoriff.echoriff.radio.data.RadioRepository
import com.echoriff.echoriff.favorite.domain.LikedSongsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FetchLikedSongsUseCaseImpl(private val repository: FavoriteRepository) : FetchLikedSongsUseCase {
    override suspend fun fetchLikedSongs(): LikedSongsState {
        return withContext(Dispatchers.IO){
            try {
                repository.fetchLikedSongs()
            }catch (e: Exception){
                LikedSongsState.Failure(e.message ?: "Unknown error")
            }
        }
    }
}