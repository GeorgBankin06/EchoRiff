package com.echoriff.echoriff.radio.domain.usecase

import com.echoriff.echoriff.radio.data.RadioRepository
import com.echoriff.echoriff.radio.domain.Radio
import com.echoriff.echoriff.radio.domain.RadioState

class LikeRadioUseCaseImpl(
    private val repository: RadioRepository
) : LikeRadioUseCase {
    override suspend fun likeRadio(radio: Radio): RadioState {
        return repository.likeRadio(radio)
    }
}