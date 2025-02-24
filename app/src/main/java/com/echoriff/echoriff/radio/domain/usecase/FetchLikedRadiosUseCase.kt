package com.echoriff.echoriff.radio.domain.usecase

import com.echoriff.echoriff.radio.domain.LikedRadiosState

interface FetchLikedRadiosUseCase {
    suspend fun fetchLikedRadios(): LikedRadiosState
}