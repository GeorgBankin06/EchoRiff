package com.echoriff.echoriff.favorite.domain.usecase

import com.echoriff.echoriff.favorite.data.FavoriteRepository
import com.echoriff.echoriff.radio.domain.model.Song

class DeleteSongUseCaseImpl(private val repository: FavoriteRepository): DeleteSongUseCase {
    override suspend fun deleteSong(removeSong: Song): Boolean {
        return repository.deleteSong(removeSong)
    }
}