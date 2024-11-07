package com.echoriff.echoriff.radio.domain.usecase

import com.echoriff.echoriff.radio.domain.CategoriesState

interface FetchCategoriesUseCase {
    suspend fun fetchCategories(): CategoriesState
}