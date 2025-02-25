package com.echoriff.echoriff.favorite.data

import com.echoriff.echoriff.favorite.domain.LikedRadiosState
import com.echoriff.echoriff.favorite.domain.LikedSongsState

interface FavoriteRepository {
    suspend fun fetchLikedRadios(): LikedRadiosState
    suspend fun fetchLikedSongs(): LikedSongsState
}