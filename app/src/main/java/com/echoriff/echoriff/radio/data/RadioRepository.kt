package com.echoriff.echoriff.radio.data

import com.echoriff.echoriff.radio.domain.LikedRadiosState
import com.echoriff.echoriff.radio.domain.LikedSongsState
import com.echoriff.echoriff.radio.domain.model.Radio
import com.echoriff.echoriff.radio.domain.RadioState
import com.echoriff.echoriff.radio.domain.model.Song
import com.echoriff.echoriff.radio.domain.SongState
import com.echoriff.echoriff.radio.domain.model.CategoryDto

interface RadioRepository {
    suspend fun likeRadio(radio: Radio): RadioState
    suspend fun saveLikedSong(song: Song) : SongState
    suspend fun fetchCategories(): List<CategoryDto>
    suspend fun fetchLikedRadios(): LikedRadiosState
    suspend fun fetchLikedSongs(): LikedSongsState
}