package com.echoriff.echoriff.radio.domain.model

data class Radio(
    val coverArtUrl: String = "",
    val streamUrl: String = "",
    val title: String = "",
    val webUrl: String? = "",
    val intro: String = "",
    val liked: Boolean = false
)