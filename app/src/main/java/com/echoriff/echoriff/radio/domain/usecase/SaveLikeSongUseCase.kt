package com.echoriff.echoriff.radio.domain.usecase

import com.echoriff.echoriff.radio.domain.model.Song
import com.echoriff.echoriff.radio.domain.SongState

interface SaveLikeSongUseCase {
    suspend fun saveSong(song: Song): SongState
}