package io.upnextgpt.ui.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.upnextgpt.data.api.Api
import io.upnextgpt.data.api.TrackService
import io.upnextgpt.data.api.service
import io.upnextgpt.data.settings.Settings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val api: Api,
    private val settings: Settings,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        listenApiBaseUrlChanges()
    }

    private fun listenApiBaseUrlChanges() = viewModelScope.launch(dispatcher) {
        settings.apiBaseUrlFlow.collect { baseUrl ->
            _uiState.update {
                it.copy(
                    apiBaseUrl = baseUrl ?: Api.BASE_URL,
                    isTestingApiBaseUrl = false,
                    isApiBaseUrlWorkingProperly = null,
                )
            }
        }
    }

    fun updateApiBaseUrl(url: String?) = viewModelScope.launch(dispatcher) {
        settings.updateApiBaseUrl(url?.ifEmpty { null })
    }

    fun testApiBaseUrl() = viewModelScope.launch(dispatcher) {
        _uiState.update {
            it.copy(
                isTestingApiBaseUrl = true,
                isApiBaseUrlWorkingProperly = null,
            )
        }
        val service = api.service<TrackService>()
        val isWorkingProperly = try {
            service.status().ok
        } catch (e: Exception) {
            false
        }
        _uiState.update {
            it.copy(
                isTestingApiBaseUrl = false,
                isApiBaseUrlWorkingProperly = isWorkingProperly,
            )
        }
    }
}