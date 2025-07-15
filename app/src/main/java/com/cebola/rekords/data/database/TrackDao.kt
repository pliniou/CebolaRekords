package com.cebolarekords.player.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {
    /**
     * REFINAMENTO: Retorna um Flow<List<TrackEntity>> para observação reativa.
     * A função não precisa mais ser 'suspend', pois o Flow do Room gerencia a execução em background,
     * simplificando a coleta de dados na camada de ViewModel e garantindo que a UI
     * reaja automaticamente a mudanças no banco de dados.
     */
    @Query("SELECT * FROM tracks ORDER BY title ASC")
    fun getAllTracks(): Flow<List<TrackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tracks: List<TrackEntity>)

    @Query("SELECT COUNT(id) FROM tracks")
    suspend fun getTrackCount(): Int
}