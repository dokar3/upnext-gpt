package io.upnextgpt.ui.settings.viewmodel

import androidx.compose.runtime.Immutable
import io.upnextgpt.data.settings.TrackFinishedAction

@Immutable
data class SettingsUiState(
    val isDynamicColorEnabled: Boolean = false,
    val isConnectedToPlayers: Boolean = true,
    val isServiceEnabled: Boolean = true,
    val apiBaseUrl: String? = null,
    val isTestingApiBaseUrl: Boolean = false,
    val testResultMessage: String? = null,
    val isApiBaseUrlWorkingProperly: Boolean? = null,
    val trackFinishedAction: TrackFinishedAction? = null,
)