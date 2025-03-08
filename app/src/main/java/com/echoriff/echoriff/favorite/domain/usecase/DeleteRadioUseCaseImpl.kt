package com.echoriff.echoriff.favorite.domain.usecase

import com.echoriff.echoriff.favorite.data.FavoriteRepository
import com.echoriff.echoriff.radio.domain.model.Radio

class DeleteRadioUseCaseImpl(private val repository: FavoriteRepository) : DeleteRadioUseCase {
    override suspend fun deleteRadio(radio: Radio): Boolean {
        return repository.deleteRadio(radio)
    }
}