package com.cebolarekords.player.data

import android.content.Context
import androidx.core.net.toUri
import com.cebolarekords.player.data.database.TrackDao
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CebolaRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val trackDao: TrackDao,
    private val localDataSource: LocalTrackDataSource
) {
    suspend fun getArtists(): List<Artist> = withContext(Dispatchers.IO) {
        localDataSource.getArtists()
    }
    
    fun getAllTracks(): Flow<List<Track>> {
        return trackDao.getAllTracks().map { entityList ->
            entityList.map { entity ->
                val uri = "android.resource://${context.packageName}/${entity.audioFileResId}".toUri()
                entity.toDomainModel(uri)
            }
        }
    }
}