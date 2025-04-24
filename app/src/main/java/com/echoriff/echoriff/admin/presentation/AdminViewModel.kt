package com.echoriff.echoriff.admin.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.echoriff.echoriff.admin.domain.usecase.AddRadioUseCase
import com.echoriff.echoriff.admin.domain.usecase.FetchCategoriesUseCase
import com.echoriff.echoriff.radio.domain.model.Category
import com.echoriff.echoriff.radio.domain.model.Radio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminViewModel(
    private val fetchCategoriesUseCase: FetchCategoriesUseCase,
    private val addRadioUseCase: AddRadioUseCase
) : ViewModel() {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories = _categories.asStateFlow()

    fun loadCategories() {
        viewModelScope.launch {
            _categories.value = fetchCategoriesUseCase.fetchCategories()
        }
    }

    fun addRadioToCategory(categoryTitle: String, radio: Radio) {
        viewModelScope.launch {
            addRadioUseCase.addRadio(categoryTitle, radio)
        }
    }
}