package com.echoriff.echoriff.radio.data

import android.util.Log
import com.echoriff.echoriff.common.Constants
import com.echoriff.echoriff.common.domain.User
import com.echoriff.echoriff.radio.domain.Radio
import com.echoriff.echoriff.radio.domain.RadioState
import com.echoriff.echoriff.radio.domain.Song
import com.echoriff.echoriff.radio.domain.SongState
import com.echoriff.echoriff.radio.domain.model.CategoryDto
import com.echoriff.echoriff.radio.domain.model.RadioDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

class RadioRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : RadioRepository {
    private val database: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("categories")


    override suspend fun fetchCategories(): List<CategoryDto> {
        return try {
            withTimeout(5000L) {
                val snapshot = database.get().await()
                val categories = mutableListOf<CategoryDto>()

                for (dataSnapshot in snapshot.children) {
                    val radiosSnapshot = dataSnapshot.child("radios")
                    val bgImageUrl = dataSnapshot.child("bgImgUrl")
                        .getValue(String::class.java) ?: ""
                    val title = dataSnapshot.child("title").getValue(String::class.java) ?: ""

                    val radios = radiosSnapshot.children.mapNotNull { radioSnapshot ->

                        val radioTitle = radioSnapshot.child("title")
                            .getValue(String::class.java) ?: ""
                        val coverArtUrl = radioSnapshot.child("coverArtUrl")
                            .getValue(String::class.java) ?: ""
                        val streamUrl = radioSnapshot.child("streamUrl")
                            .getValue(String::class.java) ?: ""
                        val webUrl = radioSnapshot.child("webUrl")
                            .getValue(String::class.java) ?: ""
                        val intro = radioSnapshot.child("intro")
                            .getValue(String::class.java) ?: ""

                        RadioDto(coverArtUrl, streamUrl, radioTitle, webUrl, intro)
                    }

                    categories.add(CategoryDto(bgImageUrl, radios, title))
                }
                categories
            }
        } catch (e: TimeoutCancellationException) {
            emptyList()
        }
    }

    override suspend fun likeRadio(radio: Radio): RadioState {
        return try {
            // Start loading
            RadioState.Loading

            // Get the currently authenticated user's ID
            val currentUser = firebaseAuth.currentUser
            val userId = currentUser?.uid ?: return RadioState.Failure("User not found")

            // Reference to the user's document in Firestore
            val userRef = firestore.collection(Constants.USERS).document(userId)
            val userSnapshot = userRef.get().await()

            if (userSnapshot.exists()) {
                // Retrieve the user's favoriteRadios
                val user = userSnapshot.toObject(User::class.java)
                val currentFavorites = user?.favoriteRadios?.toMutableList() ?: mutableListOf()

                // Check if the radio is already in favorites
                if (currentFavorites.any { it.title == radio.title }) {
                    return RadioState.Failure("Radio is already in favorites")
                }

                // Add the radio to favorites and update Firestore
                currentFavorites.add(radio)
                userRef.update("favoriteRadios", currentFavorites).await()
                RadioState.Success("Radio added to favorites")
            } else {
                RadioState.Failure("User not found.")
            }
        } catch (e: Exception) {
            RadioState.Failure(e.message ?: "Unknown error")
        }
    }

    override suspend fun saveLikedSong(song: Song): SongState {
        return try {
            SongState.Loading

            // Get the currently authenticated user's ID
            val currentUser = firebaseAuth.currentUser
            val userId = currentUser?.uid ?: return SongState.Failure("User not found")

            // Reference to the user's document in Firestore
            val userRef = firestore.collection(Constants.USERS).document(userId)
            val userSnapshot = userRef.get().await()

            if (userSnapshot.exists()) {
                // Retrieve the user's likedSongs
                val user = userSnapshot.toObject(User::class.java)
                val currentLikedSongs = user?.likedSongs?.toMutableList() ?: mutableListOf()

                // Check if the song is already liked
                if (currentLikedSongs.any { it.songName == song.songName && it.artist == song.artist }) {
                    return SongState.Failure("Song is already in liked songs")
                }

                // Add the song to likedSongs and update Firestore
                currentLikedSongs.add(song)
                userRef.update("likedSongs", currentLikedSongs).await()

                SongState.Success("Song added to liked songs")
            } else {
                SongState.Failure("User not found.")
            }
        } catch (e: Exception) {
            SongState.Failure(e.message ?: "Unknown error")
        }
    }
}