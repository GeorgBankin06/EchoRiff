package com.echoriff.echoriff.favorite.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.echoriff.echoriff.favorite.domain.LikedSongsState
import com.echoriff.echoriff.favorite.domain.usecase.DeleteSongUseCase
import com.echoriff.echoriff.favorite.domain.usecase.FetchLikedSongsUseCase
import com.echoriff.echoriff.favorite.domain.usecase.UpdateLikedSongsUseCase
import com.echoriff.echoriff.radio.domain.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LikedSongsViewModel(
    private val fetchLikedSongsUseCase: FetchLikedSongsUseCase,
    private val updatedRadioList: UpdateLikedSongsUseCase,
    private val removeSong: DeleteSongUseCase
) : ViewModel() {

    private val _likedSongsState = MutableStateFlow<LikedSongsState>(LikedSongsState.Loading)
    val likedSongsState = _likedSongsState.asStateFlow()

    init {
        fetchSongsRadios()
    }

    fun updateSongList(updateSongList: List<Song>){
        viewModelScope.launch {
            updatedRadioList.saveUpdatedSongList(updateSongList)
        }
    }

    fun deleteSong(song: Song){
        viewModelScope.launch {
            removeSong.deleteSong(song)
        }
    }

    private fun fetchSongsRadios() {
        viewModelScope.launch {
            _likedSongsState.value = LikedSongsState.Loading

            val result = fetchLikedSongsUseCase.fetchLikedSongs()
            _likedSongsState.value = result
        }
    }
}
