package com.cebolarekords.player.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferencesRepository @Inject constructor(@ApplicationContext private val context: Context) {

    private object PreferencesKeys {
        val LAST_PLAYED_POSITION = longPreferencesKey("last_played_position")
        // Poderíamos adicionar aqui LAST_PLAYED_TRACK_ID, SHUFFLE_MODE, etc.
    }

    val lastPlayedPosition: Flow<Long> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.LAST_PLAYED_POSITION] ?: 0L
        }

    suspend fun saveLastPlayedPosition(position: Long) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_PLAYED_POSITION] = position
        }
    }
}