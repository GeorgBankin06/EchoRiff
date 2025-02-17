package com.echoriff.echoriff.radio.domain.usecase

import com.echoriff.echoriff.radio.domain.Radio
import com.echoriff.echoriff.radio.domain.RadioState

interface LikeRadioUseCase {
    suspend fun likeRadio(radio: Radio): RadioState
}