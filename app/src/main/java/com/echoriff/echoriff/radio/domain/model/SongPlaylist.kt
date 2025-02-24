package com.echoriff.echoriff.radio.domain.model

data class SongPlaylist(
    val type: String, // Song
    val coverImage: String,
    val title: String,
    val songsList: List<Song>
)
