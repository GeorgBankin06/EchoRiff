package com.echoriff.echoriff.favorite.domain.usecase

import com.echoriff.echoriff.radio.domain.model.Song

interface UpdateLikedSongsUseCase {
    suspend fun saveUpdatedSongList(updatedSongList: List<Song>) : Boolean

}