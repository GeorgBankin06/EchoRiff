package com.echoriff.echoriff.favorite.domain.usecase

import com.echoriff.echoriff.radio.domain.model.Radio

interface DeleteRadioUseCase {
    suspend fun deleteRadio(radio: Radio): Boolean
}