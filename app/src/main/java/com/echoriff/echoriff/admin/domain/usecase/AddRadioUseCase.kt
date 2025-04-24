package com.echoriff.echoriff.admin.domain.usecase

import com.echoriff.echoriff.radio.domain.model.Radio

interface AddRadioUseCase {
    suspend fun addRadio(categoryTitle: String, radio: Radio)
}