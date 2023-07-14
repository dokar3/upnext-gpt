package io.upnextgpt.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences>
        by preferencesDataStore(name = "settings")

class SettingsImpl(private val dataStore: DataStore<Preferences>) : Settings {
    private val currentPlayerKey = stringPreferencesKey("current_player")

    private val nextTrackIdKey = longPreferencesKey("next_track")

    private val apiBaseUrlKey = stringPreferencesKey("api_base_url")

    private val trackFinishedActionKey =
        stringPreferencesKey("track_finished_action")

    private val serviceEnabledKey = booleanPreferencesKey("service_enabled")

    override val currentPlayerFlow: Flow<String?> = dataStore.data
        .map { it[currentPlayerKey] }
        .distinctUntilChanged()

    override suspend fun updateCurrentPlayer(value: String?) {
        dataStore.edit {
            if (value == null) {
                it.remove(currentPlayerKey)
            } else {
                it[currentPlayerKey] = value
            }
        }
    }

    override val apiBaseUrlFlow: Flow<String?> = dataStore.data
        .map { it[apiBaseUrlKey] }
        .distinctUntilChanged()

    override suspend fun updateApiBaseUrl(value: String?) {
        dataStore.edit {
            if (value == null) {
                it.remove(apiBaseUrlKey)
            } else {
                it[apiBaseUrlKey] = value
            }
        }
    }

    override val trackFinishedActionFlow: Flow<TrackFinishedAction?> =
        dataStore.data
            .map { prefs ->
                val value = prefs[trackFinishedActionKey] ?: return@map null
                TrackFinishedAction.values().find { it.key == value }
            }
            .distinctUntilChanged()

    override suspend fun updateTrackFinishedAction(
        value: TrackFinishedAction?
    ) {
        dataStore.edit {
            if (value == null) {
                it.remove(trackFinishedActionKey)
            } else {
                it[trackFinishedActionKey] = value.key
            }
        }
    }

    override val serviceEnabledFlow: Flow<Boolean> = dataStore.data
        .map { it[serviceEnabledKey] ?: true }
        .distinctUntilChanged()

    override suspend fun updateServiceEnabledState(value: Boolean) {
        dataStore.edit { it[serviceEnabledKey] = value }
    }
}