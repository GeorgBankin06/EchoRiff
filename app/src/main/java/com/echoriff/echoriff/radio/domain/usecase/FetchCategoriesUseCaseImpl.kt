package com.echoriff.echoriff.radio.domain.usecase

import com.echoriff.echoriff.radio.data.RadioRepository
import com.echoriff.echoriff.radio.domain.CategoriesState
import com.echoriff.echoriff.radio.domain.toCategories

class FetchCategoriesUseCaseImpl(
    private val repository: RadioRepository
) : FetchCategoriesUseCase {
    override suspend fun fetchCategories(): CategoriesState {
        val categories = repository.fetchCategories()
        return when {
            categories.isEmpty() -> CategoriesState.Failure
            else -> CategoriesState.Success(categories.toCategories())
        }
    }
}
