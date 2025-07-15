package com.cebola.rekords.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cebola.rekords.data.Track

@Entity(tableName = "tracks")
data class TrackEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val artistName: String,
    val albumName: String,
    val audioFileResId: Int,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val artworkData: ByteArray?
) {
    fun toDomainModel(uri: android.net.Uri): Track {
        return Track(
            id = id,
            title = title,
            artistName = artistName,
            albumName = albumName,
            audioFile = audioFileResId,
            audioUri = uri,
            artworkData = artworkData
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as TrackEntity
        if (id != other.id) return false
        if (title != other.title) return false
        if (artistName != other.artistName) return false
        if (albumName != other.albumName) return false
        if (audioFileResId != other.audioFileResId) return false
        if (artworkData != null) {
            if (other.artworkData == null) return false
            // CORREÇÃO: Expressão quebrada unificada.
            if (!artworkData.contentEquals(other.artworkData)) return false
        } else if (other.artworkData != null) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + title.hashCode()
        result = 31 * result + artistName.hashCode()
        result = 31 * result + albumName.hashCode()
        result = 31 * result + audioFileResId
        result = 31 * result + (artworkData?.contentHashCode() ?: 0)
        return result
    }
}