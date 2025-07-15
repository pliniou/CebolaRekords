package com.cebola.rekords.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {
    
    @Query("SELECT * FROM tracks ORDER BY title ASC")
    fun getAllTracks(): Flow<List<TrackEntity>>
    
    @Query("SELECT * FROM tracks WHERE id = :id")
    suspend fun getTrackById(id: Int): TrackEntity?
    @Query("SELECT * FROM tracks WHERE artistName = :artistName ORDER BY title ASC")
    fun getTracksByArtist(artistName: String): Flow<List<TrackEntity>>
    
    @Query("SELECT * FROM tracks WHERE albumName = :albumName ORDER BY title ASC")
    fun getTracksByAlbum(albumName: String): Flow<List<TrackEntity>>
    
    @Query("""
        SELECT * FROM tracks 
        WHERE title LIKE :query 
        OR artistName LIKE :query 
        OR albumName LIKE :query
 
               ORDER BY title ASC
    """)
    fun searchTracks(query: String): Flow<List<TrackEntity>>
    
    @Query("SELECT * FROM tracks ORDER BY title ASC LIMIT :limit OFFSET :offset")
    suspend fun getTracksWithPagination(limit: Int, offset: Int): List<TrackEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tracks: List<TrackEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(track: TrackEntity)
    
    @Query("DELETE FROM tracks")
    suspend fun 
    deleteAllTracks()
    
    @Transaction
    suspend fun clearAndInsertAll(tracks: List<TrackEntity>) {
        deleteAllTracks()
        insertAll(tracks)
    }
    
    @Query("SELECT COUNT(id) FROM tracks")
    suspend fun getTrackCount(): Int
    
    @Query("SELECT COUNT(DISTINCT artistName) FROM tracks")
    suspend fun getArtistCount(): Int
    
    @Query("SELECT COUNT(DISTINCT albumName) FROM tracks")
    suspend fun getAlbumCount(): Int
    
    
    @Query("SELECT DISTINCT artistName FROM tracks ORDER BY artistName ASC")
    suspend fun getAllArtists(): List<String>
    
    @Query("SELECT DISTINCT albumName FROM tracks ORDER BY albumName ASC")
    suspend fun getAllAlbums(): List<String>
    
    @Query("SELECT * FROM tracks WHERE artistName = :artistName ORDER BY albumName ASC, title ASC")
    suspend fun getTracksByArtistSync(artistName: String): List<TrackEntity>
    
    @Query("SELECT * FROM tracks WHERE albumName = :albumName ORDER BY title ASC")
    suspend fun getTracksByAlbumSync(albumName: String): List<TrackEntity>
    
  
       @Query("SELECT * FROM tracks ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomTracks(limit: Int): List<TrackEntity>
    
    @Query("SELECT * FROM tracks ORDER BY id DESC LIMIT :limit")
    suspend fun getRecentTracks(limit: Int): List<TrackEntity>
}