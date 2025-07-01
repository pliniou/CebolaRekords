package com.cebolarekords.player.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TrackDao {
    @Query("SELECT * FROM tracks ORDER BY title ASC")
    suspend fun getAllTracks(): List<TrackEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tracks: List<TrackEntity>)

    @Query("SELECT COUNT(id) FROM tracks")
    suspend fun getTrackCount(): Int
}