package com.echoriff.echoriff.register.domain

import com.echoriff.echoriff.radio.domain.Radio
import com.echoriff.echoriff.radio.domain.Song

data class User(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val profileImage: String? = null,
    val favoriteRadios: List<Radio> = emptyList(),
    val likedSongs: List<Song> = emptyList(),
    val role: String = "User" // Default role is User
)
