package com.echoriff.echoriff.radio.data

import com.echoriff.echoriff.radio.domain.model.CategoryDto

interface RadioRepository {
    suspend fun fetchCategories(): List<CategoryDto>
}