package com.echoriff.echoriff.radio.presentation

import android.app.Application
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.util.Log
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
import com.echoriff.echoriff.radio.domain.Radio
import com.echoriff.echoriff.radio.domain.model.CategoryDto
import com.echoriff.echoriff.radio.domain.model.RadioDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val _nowPlayingCategory = MutableStateFlow<CategoryDto?>(null)
    val nowPlayingCategory = _nowPlayingCategory.asStateFlow()

    private val _nowPlayingRadio = MutableStateFlow<RadioDto?>(null)
    val nowPlayingRadio = _nowPlayingRadio.asStateFlow()

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
    fun playRadio(radio: Radio) {
        val dataSourceFactory = DefaultDataSource.Factory(getApplication())
        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(radio.streamUrl))
        exoPlayer.setMediaSource(mediaSource)
        exoPlayer.prepare()
        play()
    }

    private fun handleTitle(title: String) {
        // Logic to handle the title, e.g., update LiveData or StateFlow for UI
        Log.d(TAG, "Now playing title: $title")
    }

    fun play() {
        exoPlayer.play()
    }

    fun pause() {
        exoPlayer.pause()
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

    companion object {
        private const val TAG = "RadioPlayerViewModel"
    }
}