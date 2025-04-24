package com.echoriff.echoriff.admin.domain.usecase

import com.echoriff.echoriff.admin.data.AdminRepository
import com.echoriff.echoriff.radio.domain.model.Category

class FetchCategoriesUseCaseImpl(private val repository: AdminRepository) : FetchCategoriesUseCase {
    override suspend fun fetchCategories(): List<Category> {
        return repository.getAllCategories()
    }
}