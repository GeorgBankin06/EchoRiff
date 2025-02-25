package com.echoriff.echoriff.favorite.domain.usecase

import com.echoriff.echoriff.favorite.domain.LikedRadiosState

interface FetchLikedRadiosUseCase {
    suspend fun fetchLikedRadios(): LikedRadiosState
}