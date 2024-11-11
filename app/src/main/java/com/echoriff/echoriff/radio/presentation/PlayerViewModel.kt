package com.echoriff.echoriff.radio.presentation

import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import androidx.annotation.OptIn
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.session.MediaSession
import com.echoriff.echoriff.MainActivity
import com.echoriff.echoriff.common.extractArtistAndTitle
import com.echoriff.echoriff.radio.domain.Category
import com.echoriff.echoriff.radio.domain.Radio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val _nowPlayingCategory = MutableStateFlow<Category?>(null)
    val nowPlayingCategory = _nowPlayingCategory.asStateFlow()

    private val _nowPlayingRadio = MutableStateFlow<Radio?>(null)
    val nowPlayingRadio = _nowPlayingRadio.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex = _currentIndex.asStateFlow()

    private val _nowPlayingInfo = MutableStateFlow<Pair<String?, String?>>(null to null)
    val nowPlayingInfo = _nowPlayingInfo.asStateFlow()

    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(application).build()
    private val mediaSession: MediaSession = MediaSession.Builder(application, exoPlayer)
        .setSessionActivity(createMainActivityPendingIntent())
        .build()

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
        mediaSession.release()
        exoPlayer.release()
    }

    @OptIn(UnstableApi::class)
    fun playRadio(radio: Radio?, category: Category?) {
        radio?.streamUrl ?: return

        _nowPlayingInfo.value = radio.title to null
        _nowPlayingCategory.value = category
        _nowPlayingRadio.value = radio
        _currentIndex.value =
            category?.radios?.indexOfFirst {it.title == nowPlayingRadio.value?.title } ?: 0

        val dataSourceFactory = DefaultDataSource.Factory(getApplication())
        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(radio.streamUrl))
        exoPlayer.setMediaSource(mediaSource)
        exoPlayer.prepare()
        play()
    }

    private fun handleTitle(metadata: String) {
        val (artist, title) = metadata.extractArtistAndTitle()
        _nowPlayingInfo.value = (title ?: nowPlayingRadio.value?.title) to artist
    }

    fun isPlaying(): Boolean = exoPlayer.isPlaying

    fun play() {
        exoPlayer.play()
    }

    fun pause() {
        exoPlayer.pause()
    }

    fun playNext() {
        playRadio(getNextRadio(), nowPlayingCategory.value)
    }

    fun playPrev() {
        playRadio(getPrevRadio(), nowPlayingCategory.value)
    }

    private fun createMainActivityPendingIntent(): PendingIntent {
        val intent = Intent(getApplication(), MainActivity::class.java)
        return PendingIntent.getActivity(
            getApplication(),
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
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

    companion object {
        private const val TAG = "RadioPlayerViewModel"
    }
}