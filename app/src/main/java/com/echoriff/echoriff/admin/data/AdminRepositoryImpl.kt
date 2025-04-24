package com.echoriff.echoriff.admin.data

import android.util.Log
import com.echoriff.echoriff.admin.domain.ChangeUserRoleState
import com.echoriff.echoriff.common.Constants
import com.echoriff.echoriff.radio.domain.model.Category
import com.echoriff.echoriff.radio.domain.model.CategoryDto
import com.echoriff.echoriff.radio.domain.model.Radio
import com.echoriff.echoriff.radio.domain.model.RadioDto
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

class AdminRepositoryImpl(private val firestore: FirebaseFirestore) : AdminRepository {
    private val db = FirebaseDatabase.getInstance().getReference("categories")

    override suspend fun getAllCategories(): List<Category> {
        return try {
            withTimeout(5000L) {
                val snapshot = db.get().await()
                val categories = mutableListOf<Category>()

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

                        Radio(coverArtUrl, streamUrl, radioTitle, webUrl, intro)
                    }

                    categories.add(Category(bgImageUrl, title, radios))
                }
                categories
            }
        } catch (e: TimeoutCancellationException) {
            emptyList()
        }
    }

    override suspend fun addRadioToCategory(categoryTitle: String, newRadio: Radio) {
        val categoriesRef =
            FirebaseDatabase.getInstance().getReference("categories")

        categoriesRef.get().addOnSuccessListener { snapshot ->
            val categoriesList = snapshot.children.toList()
            var targetIndex = -1

            categoriesList.forEachIndexed { index, categorySnapshot ->
                val title = categorySnapshot.child("title").getValue(String::class.java)
                if (title == categoryTitle) {
                    targetIndex = index
                    return@forEachIndexed
                }
            }

            if (targetIndex != -1) {
                val radiosRef = categoriesRef.child("$targetIndex/radios")

                radiosRef.push().setValue(newRadio)
                    .addOnSuccessListener {
                        Log.d("AdminAddRadio", "Radio added successfully!")
                    }
                    .addOnFailureListener { error ->
                        Log.e("AdminAddRadio", "Failed to add radio: ${error.message}")
                    }
            } else {
                Log.e("AdminAddRadio", "Category \"$categoryTitle\" not found.")
            }
        }.addOnFailureListener {
            Log.e("AdminAddRadio", "Failed to fetch categories: ${it.message}")
        }
    }

    override suspend fun changeUserRole(email: String, role: String): ChangeUserRoleState {
        return try {
            val usersCollection = firestore.collection(Constants.USERS)
            val querySnapshot =
                usersCollection.whereEqualTo("email", email).get().await()

            if (querySnapshot.isEmpty) {
                return ChangeUserRoleState.Failure("User not found")
            }

            val document = querySnapshot.documents.first()
            val userId = document.id

            usersCollection.document(userId).update("role", role).await()

            ChangeUserRoleState.Success("Role Changed successfully")
        } catch (e: Exception) {
            Log.e("TAGGY", "${e.printStackTrace()}")
            ChangeUserRoleState.Failure("")
        }
    }
}