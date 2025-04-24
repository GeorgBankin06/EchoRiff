package com.echoriff.echoriff.radio.domain.model

data class Category(
    val bgImgUrl: String = "",
    val title: String = "",
    val radios: List<Radio>? = emptyList()
)

fun List<CategoryDto>.toCategories(): List<Category> {
    return this.map { categoryDto ->
        Category(
            bgImgUrl = categoryDto.bgImgUrl,
            title = categoryDto.title,
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