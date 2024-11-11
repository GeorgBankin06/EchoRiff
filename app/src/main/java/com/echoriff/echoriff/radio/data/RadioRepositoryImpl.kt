package com.echoriff.echoriff.radio.data

import com.echoriff.echoriff.radio.domain.model.CategoryDto
import com.echoriff.echoriff.radio.domain.model.RadioDto
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

class RadioRepositoryImpl : RadioRepository {
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
}