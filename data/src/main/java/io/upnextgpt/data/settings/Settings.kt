package io.upnextgpt.data.settings

import kotlinx.coroutines.flow.Flow

interface Settings {
    val currentPlayerFlow: Flow<String?>
    val apiBaseUrlFlow: Flow<String?>
    val trackFinishedActionFlow: Flow<TrackFinishedAction?>
    val serviceEnabledFlow: Flow<Boolean>

    suspend fun updateCurrentPlayer(value: String?)

    suspend fun updateApiBaseUrl(value: String?)

    suspend fun updateTrackFinishedAction(value: TrackFinishedAction?)

    suspend fun updateServiceEnabledState(value: Boolean)
}