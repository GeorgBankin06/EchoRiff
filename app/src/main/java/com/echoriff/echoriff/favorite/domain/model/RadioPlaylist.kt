package com.echoriff.echoriff.favorite.domain.model

import com.echoriff.echoriff.radio.domain.model.Radio

data class RadioPlaylist(
    val type: String, // Radio
    val coverImage: String,
    val title: String,
    val radiosList: List<Radio>
)