package com.echoriff.echoriff.favorite.domain.model

import com.echoriff.echoriff.radio.domain.model.Song

data class SongPlaylist(
    val type: String, // Song
    val coverImage: String,
    val title: String,
    val songsList: List<Song>
)
