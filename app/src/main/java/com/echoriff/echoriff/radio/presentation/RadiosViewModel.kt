package com.echoriff.echoriff.radio.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.echoriff.echoriff.common.domain.UserPreferences
import com.echoriff.echoriff.radio.domain.CategoriesState
import com.echoriff.echoriff.radio.domain.model.Category
import com.echoriff.echoriff.radio.domain.usecase.FetchCategoriesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class RadiosViewModel(
    private val fetchCategoriesUseCase: FetchCategoriesUseCase
) : ViewModel() {

    private val userPreferences: UserPreferences by inject(UserPreferences::class.java)

    private val _categories = MutableStateFlow<CategoriesState>(CategoriesState.Loading)
    val categories = _categories.asStateFlow()

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    init {
        viewModelScope.launch {
            _categories.value = fetchCategoriesUseCase.fetchCategories()

            val savedCategoryTitle = userPreferences.getSelectedCategory()
            categories.collect { state ->
                when (state) {
                    is CategoriesState.Success -> _selectedCategory.value =
                        state.categories.find { it.title == savedCategoryTitle }
                            ?: state.categories.first()
                    else -> {}
                }
            }
        }
    }

    fun setSelectedCategory(index: Int) {
        val currentState = _categories.value
        if (currentState is CategoriesState.Success) {
            _selectedCategory.value = currentState.categories[index]
            userPreferences.saveSelectedCategory(selectedCategory.value!!.title)
        }
    }
}
