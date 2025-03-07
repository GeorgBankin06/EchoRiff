package com.echoriff.echoriff.favorite.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.echoriff.echoriff.favorite.domain.LikedRadiosState
import com.echoriff.echoriff.favorite.domain.usecase.FetchLikedRadiosUseCase
import com.echoriff.echoriff.favorite.domain.usecase.UpdateLikedRadiosListUseCase
import com.echoriff.echoriff.radio.domain.model.Radio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LikedRadiosViewModel(
    private val fetchLikedRadiosUseCase: FetchLikedRadiosUseCase,
    private val updatedRadioList: UpdateLikedRadiosListUseCase
) : ViewModel() {

    private val _likedRadiosState = MutableStateFlow<LikedRadiosState>(LikedRadiosState.Loading)
    val likedRadiosState = _likedRadiosState.asStateFlow()

    init {
        fetchLikedRadios()
    }

    private fun fetchLikedRadios() {
        viewModelScope.launch {
            _likedRadiosState.value = LikedRadiosState.Loading

            val result = fetchLikedRadiosUseCase.fetchLikedRadios()
            _likedRadiosState.value = result
        }
    }

    fun updateRadioList(updateRadiosList: List<Radio>){
        viewModelScope.launch {
            val result = updatedRadioList.saveUpdatedRadioList(updateRadiosList)
        }
    }
}