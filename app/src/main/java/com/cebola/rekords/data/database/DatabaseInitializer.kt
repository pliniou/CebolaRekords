package com.cebola.rekords.data.database

import android.util.Log
import com.cebola.rekords.data.LocalTrackDataSource
import com.cebola.rekords.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseInitializer @Inject constructor(
    private val trackDao: TrackDao,
    private val localDataSource: LocalTrackDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    companion object {
        private const val TAG = "DatabaseInitializer"
    }

    private val initializationMutex = Mutex()
    private var _isInitialized = false

    suspend fun initialize() {
        initializationMutex.withLock {
            if (_isInitialized) return
            try {
                withContext(ioDispatcher) {
                    val trackCountInDb = trackDao.getTrackCount()
                    if (trackCountInDb == 0) {
                        val localTracks = localDataSource.fetchTracksFromLocalResources()
                        if (localTracks.isNotEmpty()) {
                            trackDao.insertAll(localTracks)
                        }
                    }
                    _isInitialized = true
                }
            } catch (e: Exception) {
                Log.e(TAG, "Database initialization failed", e)
                throw e
            }
        }
    }
}