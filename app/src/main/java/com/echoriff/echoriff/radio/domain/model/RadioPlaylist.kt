package com.echoriff.echoriff.radio.domain.model

data class RadioPlaylist(
    val type: String, // Radio
    val coverImage: String,
    val title: String,
    val radiosList: List<Radio>
)