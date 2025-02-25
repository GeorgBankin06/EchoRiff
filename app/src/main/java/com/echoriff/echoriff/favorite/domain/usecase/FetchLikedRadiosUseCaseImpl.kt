package com.echoriff.echoriff.favorite.domain.usecase

import com.echoriff.echoriff.favorite.data.FavoriteRepository
import com.echoriff.echoriff.radio.data.RadioRepository
import com.echoriff.echoriff.favorite.domain.LikedRadiosState

class FetchLikedRadiosUseCaseImpl(private val repository: FavoriteRepository):
    FetchLikedRadiosUseCase {
    override suspend fun fetchLikedRadios(): LikedRadiosState {
        return repository.fetchLikedRadios()
    }
}