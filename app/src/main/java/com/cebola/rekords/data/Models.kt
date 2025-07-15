package com.cebolarekords.player.data

import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color

// Modelo de Domínio - Usado pela UI e ViewModels
data class Track(
    val id: Int,
    val title: String,
    val artistName: String,
    val albumName: String,
    val audioFile: Int, // O ID do recurso raw
    val audioUri: Uri, // O URI para o player
    val artworkData: ByteArray?
) {
    // Implemente equals e hashCode se for usar em listas com chaves (LazyColumn, etc.)
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

// Modelo do Artista
data class Artist(
    val id: Int,
    val name: String,
    val description: String,
    @DrawableRes val coverImage: Int // ID do drawable
)

// ADICIONADO: Modelo para conteúdo de Streaming
data class StreamContent(
    val id: String,
    val title: String,
    val embedUrl: String
)

// ADICIONADO: Modelo para links de redes sociais
data class SocialLink(
    val platform: String,
    val url: String,
    @DrawableRes val iconRes: Int,
    val color: Color
)