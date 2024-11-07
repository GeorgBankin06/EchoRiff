package com.echoriff.echoriff.radio.data

import com.echoriff.echoriff.radio.domain.model.CategoryDto
import com.echoriff.echoriff.radio.domain.model.RadioDto
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class RadioRepositoryImpl : RadioRepository {
    private val database: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("categories")

    override suspend fun fetchCategories(): List<CategoryDto> {
        val snapshot = database.get().await() // Get data asynchronously
        val categories = mutableListOf<CategoryDto>()
        for (dataSnapshot in snapshot.children) {
            val title = dataSnapshot.child("title").getValue(String::class.java) ?: ""
            val radiosSnapshot = dataSnapshot.child("radios")
            val radios = radiosSnapshot.children.mapNotNull { radioSnapshot ->
                val radioTitle = radioSnapshot.child("title").getValue(String::class.java)
                val coverArtUrl = radioSnapshot.child("coverArtUrl").getValue(String::class.java)
                val streamUrl = radioSnapshot.child("streamUrl").getValue(String::class.java)
                if (radioTitle != null && coverArtUrl != null && streamUrl != null) {
                    RadioDto(coverArtUrl, streamUrl, radioTitle)
                } else {
                    null
                }
            }
            categories.add(CategoryDto(radios, title))
        }
        return categories
    }
}