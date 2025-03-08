package com.echoriff.echoriff.favorite.domain.usecase

import com.echoriff.echoriff.radio.domain.model.Song

interface DeleteSongUseCase {
    suspend fun deleteSong(removeSong: Song): Boolean
}