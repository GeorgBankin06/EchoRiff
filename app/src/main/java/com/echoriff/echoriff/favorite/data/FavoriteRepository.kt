package com.echoriff.echoriff.favorite.data

import com.echoriff.echoriff.favorite.domain.LikedRadiosState
import com.echoriff.echoriff.favorite.domain.LikedSongsState
import com.echoriff.echoriff.radio.domain.model.Radio
import com.echoriff.echoriff.radio.domain.model.Song

interface FavoriteRepository {
    suspend fun fetchLikedRadios(): LikedRadiosState
    suspend fun updateLikedRadios(updatedRadios: List<Radio>): Boolean
    suspend fun deleteRadio(removeRadio: Radio): Boolean

    suspend fun fetchLikedSongs(): LikedSongsState
    suspend fun updateLikedSongs(updatedSongs: List<Song>) : Boolean
}