package com.echoriff.echoriff.common

import java.util.regex.Pattern
import java.util.Locale

fun String.extractArtistAndTitle(): Pair<String?, String?> {
    val pattern = "^(.+?) - (.*(?: - .*)?)$"
    val regex = Pattern.compile(pattern)
    val match = regex.matcher(this.replaceEncodedChars())

    return if (match.find()) {
        val artist = match.group(1)?.replaceEncodedChars()?.trim()?.capitalizeWords()
        val title = match.group(2)?.replaceEncodedChars()?.trim()?.capitalizeWords()
        Pair(artist, title)
    } else {
        Pair(null, null)
    }
}

fun String.replaceEncodedChars(): String {
    return this
        .replace("%20", " ") // Decoding URL-encoded spaces
        .replace("â€™", "'")
        .replace("â€œ", "\"")
        .replace("â€", "\"")
        .replace("â€˜", "'")
        .replace("â€”", "—")
        // Handling specific character misinterpretations:
        .replace("Ã©", "é")
        .replace("Ã¨", "è")
        .replace("Ã«", "ë")
        .replace("Ã¡", "á")
        .replace("Ã³", "ó")
        .replace("Ãº", "ú")
        .replace("Ã±", "ñ")
        .replace("Ã", "à")
}

fun String.capitalizeWords(): String {
    return this.lowercase(Locale.getDefault()).split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }
}
