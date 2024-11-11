package com.echoriff.echoriff.radio.presentation

import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import com.echoriff.echoriff.MainActivity
import com.echoriff.echoriff.radio.domain.Radio
import com.echoriff.echoriff.radio.domain.model.CategoryDto
import com.echoriff.echoriff.radio.domain.model.RadioDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class RadioPlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val _nowPlayingCategory = MutableStateFlow<CategoryDto?>(null)
    val nowPlayingCategory = _nowPlayingCategory.asStateFlow()

    private val _nowPlayingRadio = MutableStateFlow<RadioDto?>(null)
    val nowPlayingRadio = _nowPlayingRadio.asStateFlow()

    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(application).build()
    private val mediaSession: MediaSession = MediaSession.Builder(application, exoPlayer)
        .setSessionActivity(createMainActivityPendingIntent())
        .build()

    override fun onCleared() {
        super.onCleared()
        mediaSession.release()
        exoPlayer.release()
    }

    fun playRadio(radio: Radio) {
        val mediaItem = MediaItem.fromUri(Uri.parse(radio.streamUrl))
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        play()
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
}