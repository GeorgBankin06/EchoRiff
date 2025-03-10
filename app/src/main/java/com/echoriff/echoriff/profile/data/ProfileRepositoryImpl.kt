package com.echoriff.echoriff.profile.data

import com.echoriff.echoriff.common.Constants
import com.echoriff.echoriff.common.domain.User
import com.echoriff.echoriff.profile.domain.ProfileState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProfileRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : ProfileRepository {
    override suspend fun fetchUserInfo(): ProfileState {
        return try {
            ProfileState.Loading

            val userId = firebaseAuth.currentUser?.uid
                ?: return ProfileState.Failure("User not authenticated")

            val userSnapshot = firestore.collection(Constants.USERS).document(userId).get().await()

            if (userSnapshot.exists()) {
                val user = userSnapshot.toObject(User::class.java)
                user?.let {
                    ProfileState.Success(it)
                } ?: ProfileState.Failure("Failed to parse user data")
            } else {
                ProfileState.Failure("User not found")
            }
        } catch (e: Exception) {
            ProfileState.Failure(e.message ?: "Unknown error")
        }
    }
}