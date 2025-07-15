package com.cebola.rekords.data.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.cebola.rekords.di.IoDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    companion object {
        private const val TAG = "UserPreferencesRepository"
    }

    private object PreferencesKeys {
        val LAST_PLAYED_POSITION = longPreferencesKey("last_played_position")
        val SHUFFLE_MODE = booleanPreferencesKey("shuffle_mode")
        val REPEAT_MODE = intPreferencesKey("repeat_mode")
    }

    suspend fun saveLastPlayedPosition(position: Long) {
        try {
            withContext(ioDispatcher) {
                context.dataStore.edit { preferences ->
                    preferences[PreferencesKeys.LAST_PLAYED_POSITION] = position
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error saving last played position", e)
        }
    }

    suspend fun saveShuffleMode(enabled: Boolean) {
        try {
            withContext(ioDispatcher) {
                context.dataStore.edit { preferences ->
                    preferences[PreferencesKeys.SHUFFLE_MODE] = enabled
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error saving shuffle mode", e)
        }
    }

    suspend fun saveRepeatMode(mode: Int) {
        try {
            withContext(ioDispatcher) {
                context.dataStore.edit { preferences ->
                    preferences[PreferencesKeys.REPEAT_MODE] = mode
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error saving repeat mode", e)
        }
    }

}