package io.upnextgpt.data.settings

import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

@VisibleForTesting
class MemorySettings : Settings {
    private val _currentPlayerFlow = MutableStateFlow<String?>(null)
    override val currentPlayerFlow: Flow<String?> = _currentPlayerFlow

    private val _nextTrackIdFlow = MutableStateFlow<Long?>(null)
    override val nextTrackIdFlow: Flow<Long?> = _nextTrackIdFlow

    private val _apiBaseUrlFlow = MutableStateFlow<String?>(null)
    override val apiBaseUrlFlow: Flow<String?> = _apiBaseUrlFlow

    override suspend fun updateCurrentPlayer(value: String?) {
        _currentPlayerFlow.value = value
    }

    override suspend fun updateNextTrackId(value: Long?) {
        _nextTrackIdFlow.value = value
    }

    override suspend fun updateApiBaseUrl(value: String?) {
        _apiBaseUrlFlow.value = value
    }
}