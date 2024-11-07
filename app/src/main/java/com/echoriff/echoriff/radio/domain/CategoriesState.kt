package com.echoriff.echoriff.radio.domain

import com.echoriff.echoriff.radio.domain.model.CategoryDto

sealed class CategoriesState {
    data object Loading : CategoriesState()
    data class Success(
        val categories: List<CategoryDto>
    ) : CategoriesState()
    data object Failure : CategoriesState()
}