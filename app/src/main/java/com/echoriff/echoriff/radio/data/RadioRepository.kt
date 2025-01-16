package com.echoriff.echoriff.radio.data

import com.echoriff.echoriff.radio.domain.Radio
import com.echoriff.echoriff.radio.domain.RadioState
import com.echoriff.echoriff.radio.domain.model.CategoryDto

interface RadioRepository {
    suspend fun fetchCategories(): List<CategoryDto>
    suspend fun likeRadio(radio: Radio): RadioState
}