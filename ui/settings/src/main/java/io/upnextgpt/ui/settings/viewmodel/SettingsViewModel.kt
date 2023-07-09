package io.upnextgpt.ui.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.upnextgpt.data.api.Api
import io.upnextgpt.data.api.TrackService
import io.upnextgpt.data.api.service
import io.upnextgpt.data.settings.Settings
import io.upnextgpt.remote.palyer.NotificationBasedPlayer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val player: NotificationBasedPlayer,
    private val api: Api,
    private val settings: Settings,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        listenApiBaseUrlChanges()
        updatePlayerConnectionStatus()
    }

    private fun listenApiBaseUrlChanges() = viewModelScope.launch(dispatcher) {
        settings.apiBaseUrlFlow.collect { baseUrl ->
            _uiState.update {
                it.copy(
                    apiBaseUrl = baseUrl ?: Api.BASE_URL,
                    testResultMessage = null,
                    isTestingApiBaseUrl = false,
                    isApiBaseUrlWorkingProperly = null,
                )
            }
        }
    }

    fun updatePlayerConnectionStatus() = viewModelScope.launch(dispatcher) {
        _uiState.update {
            it.copy(isConnectedToPlayers  = player.isConnected())
        }
    }

    fun connectToPlayers() {
        player.connect()
    }

    fun updateApiBaseUrl(url: String?) = viewModelScope.launch(dispatcher) {
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
        val service = api.service<TrackService>()
        val result = try {
            service.status()
        } catch (e: Exception) {
            null
        }
        _uiState.update {
            it.copy(
                testResultMessage = result?.message,
                isTestingApiBaseUrl = false,
                isApiBaseUrlWorkingProperly = result?.ok == true,
            )
        }
    }
}