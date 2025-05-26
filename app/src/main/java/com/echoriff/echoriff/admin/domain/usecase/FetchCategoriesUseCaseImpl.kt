package com.echoriff.echoriff.admin.domain.usecase

import com.echoriff.echoriff.admin.data.AdminRepository
import com.echoriff.echoriff.radio.domain.model.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FetchCategoriesUseCaseImpl(private val repository: AdminRepository) : FetchCategoriesUseCase {
    override suspend fun fetchCategories(): List<Category> {
        return withContext(Dispatchers.IO) {
            try{
                repository.getAllCategories()
            } catch (e: Exception){
                emptyList()
            }
        }
    }
}