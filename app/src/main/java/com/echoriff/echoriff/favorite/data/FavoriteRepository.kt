package com.echoriff.echoriff.favorite.data

import com.echoriff.echoriff.favorite.domain.LikedRadiosState
import com.echoriff.echoriff.favorite.domain.LikedSongsState
import com.echoriff.echoriff.radio.domain.model.Radio

interface FavoriteRepository {
    suspend fun fetchLikedRadios(): LikedRadiosState
    suspend fun fetchLikedSongs(): LikedSongsState
    suspend fun updateLikedRadios(updatedRadios: List<Radio>): Boolean
}