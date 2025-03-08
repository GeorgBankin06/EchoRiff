package com.echoriff.echoriff.favorite.domain.usecase

import com.echoriff.echoriff.favorite.data.FavoriteRepository
import com.echoriff.echoriff.radio.domain.model.Song

class UpdateLikedSongsUseCaseImpl(private val repository: FavoriteRepository): UpdateLikedSongsUseCase {
    override suspend fun saveUpdatedSongList(updatedSongList: List<Song>): Boolean {
        return repository.updateLikedSongs(updatedSongList)
    }
}