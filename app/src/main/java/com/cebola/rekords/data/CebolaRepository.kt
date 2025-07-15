package com.cebola.rekords.data

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import com.cebola.rekords.data.database.TrackDao
import com.cebola.rekords.data.database.TrackEntity
import com.cebola.rekords.di.IoDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CebolaRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val trackDao: TrackDao,
    private val localDataSource: LocalTrackDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    companion object {
        private const val TAG = "CebolaRepository"
    }

    suspend fun getArtists(): List<Artist> = withContext(ioDispatcher) {
        try {
            localDataSource.getArtists()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting artists", e)
            emptyList()
        }
    }

    fun getAllTracks(): Flow<List<Track>> {
        return trackDao.getAllTracks()
            .mapToDomain(context)
            .catch { e ->
                Log.e(TAG, "Error getting tracks", e)
                emit(emptyList())
            }
            .flowOn(ioDispatcher)
    }

    /**
     * OTIMIZAÇÃO: Função de extensão para centralizar a lógica de mapeamento de Flow<List<TrackEntity>>
     * para Flow<List<Track>>, evitando repetição de código.
     */
    private fun Flow<List<TrackEntity>>.mapToDomain(context: Context): Flow<List<Track>> {
        return this.map { entityList ->
            entityList.map { entity ->
                val uri = "android.resource://${context.packageName}/${entity.audioFileResId}".toUri()
                entity.toDomainModel(uri)
            }
        }
    }
}