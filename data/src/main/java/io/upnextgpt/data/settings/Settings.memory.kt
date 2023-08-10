package io.upnextgpt.data.settings

import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

@VisibleForTesting
class MemorySettings : Settings {
    private val _currentPlayerFlow = MutableStateFlow<String?>(null)
    override val currentPlayerFlow: Flow<String?> = _currentPlayerFlow

    private val _apiBaseUrlFlow = MutableStateFlow<String?>(null)
    override val apiBaseUrlFlow: Flow<String?> = _apiBaseUrlFlow

    private val _trackFinishedActionFlow =
        MutableStateFlow<TrackFinishedAction?>(null)
    override val trackFinishedActionFlow: Flow<TrackFinishedAction?> =
        _trackFinishedActionFlow

    private val _serviceEnabledFlow = MutableStateFlow(true)
    override val serviceEnabledFlow: Flow<Boolean> = _serviceEnabledFlow

    private val _dynamicColorEnabledFlow = MutableStateFlow(false)
    override val dynamicColorEnabledFlow: Flow<Boolean> = _dynamicColorEnabledFlow

    override suspend fun updateCurrentPlayer(value: String?) {
        _currentPlayerFlow.value = value
    }

    override suspend fun updateApiBaseUrl(value: String?) {
        _apiBaseUrlFlow.value = value
    }

    override suspend fun updateTrackFinishedAction(
        value: TrackFinishedAction?
    ) {
        _trackFinishedActionFlow.value = value
    }

    override suspend fun updateServiceEnabledState(value: Boolean) {
        _serviceEnabledFlow.value = value
    }

    override suspend fun updateDynamicColorEnabledState(value: Boolean) {
        _dynamicColorEnabledFlow.value = value
    }
}