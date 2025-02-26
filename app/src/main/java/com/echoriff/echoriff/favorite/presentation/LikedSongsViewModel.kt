package com.echoriff.echoriff.favorite.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.echoriff.echoriff.favorite.domain.LikedSongsState
import com.echoriff.echoriff.favorite.domain.usecase.FetchLikedSongsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LikedSongsViewModel(private val fetchLikedSongsUseCase: FetchLikedSongsUseCase): ViewModel() {

    private val _likedSongsState = MutableStateFlow<LikedSongsState>(LikedSongsState.Loading)
    val likedSongsState = _likedSongsState.asStateFlow()

    init {
        fetchSongsRadios()
    }

    private fun fetchSongsRadios() {
        viewModelScope.launch {
            _likedSongsState.value = LikedSongsState.Loading

            val result = fetchLikedSongsUseCase.fetchLikedSongs()
            _likedSongsState.value = result
        }
    }
}