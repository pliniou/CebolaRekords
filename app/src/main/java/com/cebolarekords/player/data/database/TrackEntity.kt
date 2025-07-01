package com.cebolarekords.player.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cebolarekords.player.data.Track

@Entity(tableName = "tracks")
data class TrackEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val artistName: String,
    val albumName: String,
    val audioFileResId: Int,
    val artworkData: ByteArray?
) {
    // Função para mapear a entidade do banco de dados para o modelo de domínio
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
}