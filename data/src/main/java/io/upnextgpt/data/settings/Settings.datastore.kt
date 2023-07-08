package io.upnextgpt.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences>
        by preferencesDataStore(name = "settings")

class Settings private constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val currentPlayerKey = stringPreferencesKey("current_player")

    val currentPlayerFlow: Flow<String?> = dataStore.data
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

    companion object {
        @Volatile
        private var instance: Settings? = null

        fun getInstance(context: Context): Settings {
            return instance ?: synchronized(Settings::class) {
                instance ?: Settings(
                    dataStore = context.applicationContext.dataStore,
                ).also { instance = it }
            }
        }
    }
}