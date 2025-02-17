package com.echoriff.echoriff.radio.presentation

import android.app.Application
import androidx.annotation.OptIn
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.echoriff.echoriff.common.domain.UserPreferences
import com.echoriff.echoriff.common.extractArtistAndTitle
import com.echoriff.echoriff.radio.domain.Category
import com.echoriff.echoriff.radio.domain.Radio
import com.echoriff.echoriff.radio.domain.RadioState
import com.echoriff.echoriff.radio.domain.Song
import com.echoriff.echoriff.radio.domain.SongState
import com.echoriff.echoriff.radio.domain.usecase.LikeRadioUseCase
import com.echoriff.echoriff.radio.domain.usecase.SaveLikeSongUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlayerViewModel(
    application: Application,
    private val likeRadioUseCase: LikeRadioUseCase,
    private val likeSongUseCase: SaveLikeSongUseCase
) : AndroidViewModel(application) {

    private val _likeRadio = MutableSharedFlow<RadioState>()
    val likeRadio = _likeRadio.asSharedFlow()

    private val _likeSong = MutableSharedFlow<SongState>()
    val likeSong = _likeSong.asSharedFlow()

    private val _nowPlayingCategory = MutableStateFlow<Category?>(null)
    val nowPlayingCategory = _nowPlayingCategory.asStateFlow()

    private val _nowPlayingRadio = MutableStateFlow<Radio?>(null)
    val nowPlayingRadio = _nowPlayingRadio.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex = _currentIndex.asStateFlow()

    private val _nowPlayingInfo = MutableStateFlow<Pair<String?, String?>>(null to null)
    val nowPlayingInfo = _nowPlayingInfo.asStateFlow()

    private val _isPlayingState = MutableStateFlow(false)
    val isPlayingState = _isPlayingState.asStateFlow()

    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(application).build()

    init {
        exoPlayer.addListener(object : Player.Listener {
            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                mediaMetadata.title?.let { title ->
                    handleTitle(title.toString())
                }
            }
        })
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer.release()
    }

    fun likeRadio(radio: Radio?) {
        viewModelScope.launch {
            if (radio != null) {
                val result = likeRadioUseCase.likeRadio(radio)
                _likeRadio.emit(result)
            }
        }
    }

    fun likeSong(nowPlaying: Pair<String?, String?>) {
        viewModelScope.launch {
            val (title, artist) = nowPlaying
            if (title != null && artist != null) {
                val song = Song(songName = title, artist = artist)
                val result = likeSongUseCase.saveSong(song)
                _likeSong.emit(result)
            }
        }
    }

    @OptIn(UnstableApi::class)
    fun playRadio(radio: Radio?, category: Category?) {
        radio?.streamUrl ?: return

        _nowPlayingInfo.value = radio.title to null
        _nowPlayingCategory.value = category
        _nowPlayingRadio.value = radio
        _currentIndex.value =
            category?.radios?.indexOfFirst { it.title == nowPlayingRadio.value?.title } ?: 0
        _isPlayingState.value = true

        val dataSourceFactory = DefaultDataSource.Factory(getApplication())
        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(radio.streamUrl))
        exoPlayer.setMediaSource(mediaSource)
        exoPlayer.prepare()

        val userPrefs = UserPreferences(getApplication())
        userPrefs.saveLastPlayedRadioWithCategory(getApplication(), radio, category ?: return)

        play()
    }

    fun isPlaying(): Boolean = exoPlayer.isPlaying

    fun play() {
        exoPlayer.play()
    }

    fun pause() {
        exoPlayer.pause()
        _isPlayingState.value = false
    }

    fun playNext() {
        playRadio(getNextRadio(), nowPlayingCategory.value)
    }

    fun playPrev() {
        playRadio(getPrevRadio(), nowPlayingCategory.value)
    }

    private fun getNextRadio(): Radio? {
        val radios = nowPlayingCategory.value?.radios ?: return null

        val nextIndex = (_currentIndex.value + 1) % radios.size
        return radios[nextIndex]
    }

    private fun getPrevRadio(): Radio? {
        val radios = nowPlayingCategory.value?.radios ?: return null

        val previousIndex = (_currentIndex.value - 1 + radios.size) % radios.size
        return radios[previousIndex]
    }

    private fun handleTitle(metadata: String) {
        val (artist, title) = metadata.extractArtistAndTitle()
        _nowPlayingInfo.value = (title ?: nowPlayingRadio.value?.title) to artist
    }

    companion object {
        private const val TAG = "RadioPlayerViewModel"
    }
}