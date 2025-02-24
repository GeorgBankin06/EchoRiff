package com.echoriff.echoriff.radio.domain.usecase

import com.echoriff.echoriff.radio.data.RadioRepository
import com.echoriff.echoriff.radio.domain.LikedRadiosState

class FetchLikedRadiosUseCaseImpl(private val repository: RadioRepository): FetchLikedRadiosUseCase {
    override suspend fun fetchLikedRadios(): LikedRadiosState {
        return repository.fetchLikedRadios()
    }
}