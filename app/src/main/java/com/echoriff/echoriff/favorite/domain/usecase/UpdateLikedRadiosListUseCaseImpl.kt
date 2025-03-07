package com.echoriff.echoriff.favorite.domain.usecase

import com.echoriff.echoriff.favorite.data.FavoriteRepository
import com.echoriff.echoriff.radio.domain.model.Radio

class UpdateLikedRadiosListUseCaseImpl(private val repository: FavoriteRepository) :
    UpdateLikedRadiosListUseCase {
    override suspend fun saveUpdatedRadioList(updatedRadioList: List<Radio>): Boolean {
        return repository.updateLikedRadios(updatedRadioList)
    }
}