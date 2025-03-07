package com.echoriff.echoriff.favorite.data

import com.echoriff.echoriff.common.Constants
import com.echoriff.echoriff.common.domain.User
import com.echoriff.echoriff.favorite.domain.LikedRadiosState
import com.echoriff.echoriff.favorite.domain.LikedSongsState
import com.echoriff.echoriff.radio.domain.model.Radio
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FavoriteRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : FavoriteRepository {
    override suspend fun fetchLikedRadios(): LikedRadiosState {
        return try {
            LikedRadiosState.Loading

            val userId = firebaseAuth.currentUser?.uid
                ?: return LikedRadiosState.Failure("User not authenticated")
            val userSnapshot = firestore.collection(Constants.USERS).document(userId).get().await()

            if (userSnapshot.exists()) {
                val user = userSnapshot.toObject(User::class.java)
                LikedRadiosState.Success(user?.favoriteRadios ?: emptyList())
            } else {
                LikedRadiosState.Failure("User not found")
            }
        } catch (e: Exception) {
            LikedRadiosState.Failure(e.message ?: "Unknown error")
        }
    }

    override suspend fun updateLikedRadios(updatedRadios: List<Radio>): Boolean {
        return try {
            val userId = firebaseAuth.currentUser?.uid
                ?: return false

            firestore.collection(Constants.USERS)
                .document(userId)
                .update("favoriteRadios", updatedRadios)
                .await()

            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun fetchLikedSongs(): LikedSongsState {
        return try {
            LikedSongsState.Loading

            val userId = firebaseAuth.currentUser?.uid
                ?: return LikedSongsState.Failure("User not authenticated")
            val userSnapshot = firestore.collection(Constants.USERS).document(userId).get().await()

            if (userSnapshot.exists()) {
                val user = userSnapshot.toObject(User::class.java)
                LikedSongsState.Success(user?.likedSongs ?: emptyList())
            } else {
                LikedSongsState.Failure("User not found")
            }
        } catch (e: Exception) {
            LikedSongsState.Failure(e.message ?: "Unknown error")
        }
    }
}