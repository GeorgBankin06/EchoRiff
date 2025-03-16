package com.echoriff.echoriff.profile.data

import android.icu.text.Edits
import android.net.Uri
import androidx.core.net.toUri
import com.echoriff.echoriff.common.Constants
import com.echoriff.echoriff.common.domain.User
import com.echoriff.echoriff.profile.domain.EditState
import com.echoriff.echoriff.profile.domain.ProfileState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
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

    override suspend fun updateUserInfo(
        user: User,
        newProfileImageUri: Uri?,
    ): EditState {
        return try {
            EditState.Loading

            val userId =
                firebaseAuth.currentUser?.uid ?: return EditState.Failure("User not authenticated")
            val storage = FirebaseStorage.getInstance().reference
            val userRef = firestore.collection(Constants.USERS).document(userId)

            val updatedFields = mutableMapOf<String, Any>(
                "firstName" to user.firstName,
                "lastName" to user.lastName,
                "email" to user.email
            )

            // Handle profile image update
            if (newProfileImageUri != null && user.profileImage?.toUri() != newProfileImageUri) {
                val imageRef = storage.child("profile_images/$userId.jpg")

                // Delete old image if exists
                if (!user.profileImage.isNullOrEmpty()) {
                    val oldImageRef = storage.child(user.profileImage)
                    oldImageRef.delete().await()
                }

                val uploadTask = imageRef.putFile(newProfileImageUri).await()
                val imageUrl = imageRef.downloadUrl.await().toString()

                updatedFields["profileImage"] = imageUrl
            }

                userRef.update(updatedFields).await()
                EditState.Success("Success")
            } catch (e: Exception) {
                EditState.Failure("${e.message}")
            }
        }
    }
