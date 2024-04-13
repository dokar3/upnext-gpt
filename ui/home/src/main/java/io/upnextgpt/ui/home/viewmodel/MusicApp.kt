package io.upnextgpt.ui.home.viewmodel

import androidx.compose.runtime.Immutable
import io.upnextgpt.base.MusicAppInfo

@Immutable
data class MusicApp(
    val info: MusicAppInfo,
    val isActive: Boolean = false,
    val isInstalled: Boolean = false,
)