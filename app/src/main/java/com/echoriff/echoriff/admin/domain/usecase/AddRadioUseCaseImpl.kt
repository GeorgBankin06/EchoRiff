package com.echoriff.echoriff.admin.domain.usecase

import com.echoriff.echoriff.admin.data.AdminRepository
import com.echoriff.echoriff.radio.domain.model.Radio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AddRadioUseCaseImpl(private val repository: AdminRepository) : AddRadioUseCase {
    override suspend fun addRadio(categoryTitle: String, radio: Radio) {
        withContext(Dispatchers.IO) {
            repository.addRadioToCategory(categoryTitle, radio)
        }
    }
}
