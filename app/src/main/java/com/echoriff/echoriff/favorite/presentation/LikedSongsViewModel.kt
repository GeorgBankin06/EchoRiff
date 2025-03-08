package com.echoriff.echoriff.favorite.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.echoriff.echoriff.favorite.domain.LikedSongsState
import com.echoriff.echoriff.favorite.domain.usecase.FetchLikedSongsUseCase
import com.echoriff.echoriff.favorite.domain.usecase.UpdateLikedSongsUseCase
import com.echoriff.echoriff.radio.domain.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LikedSongsViewModel(
    private val fetchLikedSongsUseCase: FetchLikedSongsUseCase,
    private val updatedRadioList: UpdateLikedSongsUseCase
) : ViewModel() {

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

    fun updateSongList(updateSongList: List<Song>){
        viewModelScope.launch {
            updatedRadioList.saveUpdatedSongList(updateSongList)
        }
    }
}