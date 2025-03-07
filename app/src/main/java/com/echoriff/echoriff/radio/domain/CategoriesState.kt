package com.echoriff.echoriff.radio.domain

import com.echoriff.echoriff.radio.domain.model.Category

sealed class CategoriesState {
    data object Loading : CategoriesState()
    data class Success(
        val categories: List<Category>
    ) : CategoriesState()

    data object Failure : CategoriesState()
}