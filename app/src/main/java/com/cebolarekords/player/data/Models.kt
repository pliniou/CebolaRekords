@file:Suppress("DEPRECATION")

package com.cebolarekords.player.data

import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.compose.ui.graphics.Color

data class Artist(
    val id: Int,
    val name: String,
    val description: String,
    @DrawableRes val coverImage: Int
)

data class Track(
    val id: Int,
    val title: String,
    val artistName: String,
    val albumName: String,
    @Deprecated("Usar artworkUri com Coil") val albumArt: ByteArray?,
    @RawRes val audioFile: Int,
    val audioUri: Uri? = null,
    val artworkUri: Uri? = null,
    val artworkData: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Track

        if (id != other.id) return false
        if (title != other.title) return false
        if (artistName != other.artistName) return false
        if (albumName != other.albumName) return false
        if (albumArt != null) {
            if (other.albumArt == null) return false
            if (!albumArt.contentEquals(other.albumArt)) return false
        } else if (other.albumArt != null) return false
        if (audioFile != other.audioFile) return false
        if (audioUri != other.audioUri) return false
        if (artworkUri != other.artworkUri) return false
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
        result = 31 * result + (albumArt?.contentHashCode() ?: 0)
        result = 31 * result + audioFile
        result = 31 * result + (audioUri?.hashCode() ?: 0)
        result = 31 * result + (artworkUri?.hashCode() ?: 0)
        result = 31 * result + (artworkData?.contentHashCode() ?: 0)
        return result
    }
}

data class SocialLink(
    val platform: String,
    val url: String,
    @DrawableRes val iconRes: Int,
    val color: Color
)