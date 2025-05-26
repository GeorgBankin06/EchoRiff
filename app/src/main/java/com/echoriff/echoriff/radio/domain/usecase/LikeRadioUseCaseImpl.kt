package com.echoriff.echoriff.radio.domain.usecase

import com.echoriff.echoriff.radio.data.RadioRepository
import com.echoriff.echoriff.radio.domain.model.Radio
import com.echoriff.echoriff.radio.domain.RadioState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LikeRadioUseCaseImpl(
    private val repository: RadioRepository
) : LikeRadioUseCase {
    override suspend fun likeRadio(radio: Radio): RadioState {
        return withContext(Dispatchers.IO) {
            try {
                repository.likeRadio(radio)
            } catch (e: Exception) {
                RadioState.Failure(e.message ?: "Unknown error")
            }
        }
    }
}