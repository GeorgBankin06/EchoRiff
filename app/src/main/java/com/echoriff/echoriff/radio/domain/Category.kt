package com.echoriff.echoriff.radio.domain

import com.echoriff.echoriff.radio.domain.model.CategoryDto

data class Category(
    val bgImgUrl: String,
    val tittle: String,
    val radios: List<Radio>?
)

fun List<CategoryDto>.toCategories(): List<Category> {
    return this.map { categoryDto ->
        Category(
            bgImgUrl = categoryDto.bgImgUrl,
            tittle = categoryDto.title,
            radios = categoryDto.radios.map { radioDto ->
                Radio(
                    coverArtUrl = radioDto.coverArtUrl,
                    streamUrl = radioDto.streamUrl,
                    title = radioDto.title,
                    webUrl = radioDto.webUrl,
                    intro = radioDto.intro
                )
            }
        )
    }
}