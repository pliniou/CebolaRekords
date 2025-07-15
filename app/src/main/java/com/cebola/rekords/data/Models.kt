package com.cebola.rekords.data

import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color

data class Track(
    val id: Int,
    val title: String,
    val artistName: String,
    val albumName: String,
    val audioFile: Int,
    val audioUri: Uri,
    val artworkData: ByteArray?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Track
        if (id != other.id) return false
        if (title != other.title) return false
        if (artistName != other.artistName) return false
        if (albumName != other.albumName) return false
        if (audioFile != other.audioFile) return false
        if (audioUri != other.audioUri) return false
        if (artworkData != null) {
            if (other.artworkData == null) return false
            if (!artworkData.contentEquals(other.artworkData)) return false
        } else if (other.artworkData != null) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + title.hashCode()
        result = 31 * result + artistName.hashCode()
        result = 31 * result + albumName.hashCode()
        result = 31 * result + audioFile
        result = 31 * result + audioUri.hashCode()
        result = 31 * result + (artworkData?.contentHashCode() ?: 0)
        return result
    }
}

data class Artist(
    val id: Int,
    val name: String,
    val description: String,
    @DrawableRes val coverImage: Int // ID do drawable
)

data class StreamContent(
    val id: String,
    val title: String,
    val embedUrl: String
)

data class SocialLink(
    val platform: String,
    val url: String,
    @DrawableRes val iconRes: Int,
    val color: Color
)