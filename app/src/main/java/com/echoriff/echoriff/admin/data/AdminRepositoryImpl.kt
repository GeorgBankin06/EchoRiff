package com.echoriff.echoriff.admin.data

import android.util.Log
import com.echoriff.echoriff.radio.domain.model.Category
import com.echoriff.echoriff.radio.domain.model.Radio
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class AdminRepositoryImpl(): AdminRepository {
    private val db = FirebaseDatabase.getInstance().getReference("categories")

    override suspend fun getAllCategories(): List<Category> {
        val snapshot = db.get().await()
        return snapshot.children.mapNotNull {
            it.getValue(Category::class.java)
        }
    }

    override suspend fun addRadioToCategory(categoryTitle: String, newRadio: Radio) {
        val categoriesRef = FirebaseDatabase.getInstance().getReference("categories")

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
}