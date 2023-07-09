package io.upnextgpt.data.settings

import kotlinx.coroutines.flow.Flow

interface Settings {
    val currentPlayerFlow: Flow<String?>
    val nextTrackIdFlow: Flow<Long?>
    val apiBaseUrlFlow: Flow<String?>

    suspend fun updateCurrentPlayer(value: String?)

    suspend fun updateNextTrackId(value: Long?)

    suspend fun updateApiBaseUrl(value: String?)
}