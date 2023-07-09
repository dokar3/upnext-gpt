package io.upnextgpt.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences>
        by preferencesDataStore(name = "settings")

class Settings(private val dataStore: DataStore<Preferences>) {
    private val currentPlayerKey = stringPreferencesKey("current_player")

    private val nextTrackIdKey = longPreferencesKey("next_track")

    val currentPlayerFlow: Flow<String?> = dataStore.data
        .filter { it.contains(currentPlayerKey) }
        .map { it[currentPlayerKey] }

    suspend fun updateCurrentPlayer(value: String?) {
        dataStore.edit {
            if (value == null) {
                it.remove(currentPlayerKey)
            } else {
                it[currentPlayerKey] = value
            }
        }
    }

    val nextTrackIdFlow: Flow<Long?> = dataStore.data
        .filter { it.contains(nextTrackIdKey) }
        .map { it[nextTrackIdKey] }

    suspend fun updateNextTrackId(value: Long?) {
        dataStore.edit {
            if (value == null) {
                it.remove(nextTrackIdKey)
            } else {
                it[nextTrackIdKey] = value
            }
        }
    }
}