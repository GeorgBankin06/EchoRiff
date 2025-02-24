package com.echoriff.echoriff.radio.domain.usecase

import com.echoriff.echoriff.radio.data.RadioRepository
import com.echoriff.echoriff.radio.domain.model.Song
import com.echoriff.echoriff.radio.domain.SongState

class SaveLikedSongUseCaseImpl(
    private val radioRepository: RadioRepository
) : SaveLikeSongUseCase {
    override suspend fun saveSong(song: Song): SongState {
        return radioRepository.saveLikedSong(song)
    }
}