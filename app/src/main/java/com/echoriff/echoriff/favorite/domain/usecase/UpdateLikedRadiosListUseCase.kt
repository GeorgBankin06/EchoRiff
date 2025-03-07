package com.echoriff.echoriff.favorite.domain.usecase

import com.echoriff.echoriff.radio.domain.model.Radio

interface UpdateLikedRadiosListUseCase {
    suspend fun saveUpdatedRadioList(updatedRadioList: List<Radio>) : Boolean
}