package com.echoriff.echoriff.radio.domain.usecase

import com.echoriff.echoriff.radio.domain.model.Radio
import com.echoriff.echoriff.radio.domain.RadioState

interface LikeRadioUseCase {
    suspend fun likeRadio(radio: Radio): RadioState
}