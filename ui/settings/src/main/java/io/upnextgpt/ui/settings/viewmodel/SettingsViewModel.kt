package io.upnextgpt.ui.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.upnextgpt.base.util.isHttpUrl
import io.upnextgpt.data.api.Api
import io.upnextgpt.data.api.TrackService
import io.upnextgpt.data.api.service
import io.upnextgpt.data.model.ApiResponse
import io.upnextgpt.data.settings.Settings
import io.upnextgpt.data.settings.TrackFinishedAction
import io.upnextgpt.remote.palyer.NotificationBasedPlayer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private data class SettingsValues(
    val apiBaseUrl: String?,
    val trackFinishedAction: TrackFinishedAction?,
    val serviceEnabled: Boolean,
)

class SettingsViewModel(
    private val player: NotificationBasedPlayer,
    private val api: Api,
    private val settings: Settings,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        listenSettingsChanges()
        updatePlayerConnectionStatus()
    }

    private fun listenSettingsChanges() = viewModelScope.launch(dispatcher) {
        combine(
            settings.apiBaseUrlFlow,
            settings.trackFinishedActionFlow,
            settings.serviceEnabledFlow,
            transform = ::SettingsValues
        )
            .distinctUntilChanged()
            .collect { values ->
                _uiState.update {
                    it.copy(
                        isServiceEnabled = values.serviceEnabled,
                        apiBaseUrl = values.apiBaseUrl ?: Api.BASE_URL,
                        testResultMessage = null,
                        isTestingApiBaseUrl = false,
                        isApiBaseUrlWorkingProperly = null,
                        trackFinishedAction = values.trackFinishedAction,
                    )
                }
            }
    }

    fun updatePlayerConnectionStatus() = viewModelScope.launch(dispatcher) {
        _uiState.update {
            it.copy(isConnectedToPlayers = player.isConnected())
        }
    }

    fun connectToPlayers() {
        player.connect()
    }

    fun updateApiBaseUrl(url: String?) = viewModelScope.launch(dispatcher) {
        if (url != null && !url.isHttpUrl()) {
            _uiState.update {
                it.copy(
                    apiBaseUrl = url,
                    testResultMessage = "Unsupported URL",
                    isApiBaseUrlWorkingProperly = false,
                )
            }
            return@launch
        }
        settings.updateApiBaseUrl(url?.ifEmpty { null })
    }

    fun testApiBaseUrl() = viewModelScope.launch(dispatcher) {
        _uiState.update {
            it.copy(
                testResultMessage = null,
                isTestingApiBaseUrl = true,
                isApiBaseUrlWorkingProperly = null,
            )
        }
        val result = try {
            api.service<TrackService>().status()
        } catch (e: Exception) {
            ApiResponse(
                ok = false,
                message = e.message,
                data = null,
            )
        }
        _uiState.update {
            it.copy(
                testResultMessage = result.message,
                isTestingApiBaseUrl = false,
                isApiBaseUrlWorkingProperly = result.ok,
            )
        }
    }

    fun updateTrackFinishedAction(
        action: TrackFinishedAction
    ) = viewModelScope.launch(dispatcher) {
        settings.updateTrackFinishedAction(action)
    }

    fun updateServiceEnabledState(
        value: Boolean
    ) = viewModelScope.launch(dispatcher) {
        settings.updateServiceEnabledState(value)
    }
}