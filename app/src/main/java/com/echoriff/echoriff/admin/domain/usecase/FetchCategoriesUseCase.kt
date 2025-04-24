package com.echoriff.echoriff.admin.domain.usecase

import com.echoriff.echoriff.radio.domain.model.Category

interface FetchCategoriesUseCase {
    suspend fun fetchCategories(): List<Category>
}