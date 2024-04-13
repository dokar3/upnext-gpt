package io.upnextgpt.ui.home.viewmodel

import android.graphics.Bitmap
import androidx.compose.runtime.Immutable
import io.upnextgpt.base.MusicAppInfo
import io.upnextgpt.data.model.Track

internal val SupportedPlayers by lazy {
    MusicAppInfo.entries.map(::MusicApp)
}

@Immutable
data class HomeUiState(
    val players: List<MusicApp> = SupportedPlayers,
    val isConnectedToPlayers: Boolean = true,
    val isServiceEnabled: Boolean = true,
    val currTrack: Track? = null,
    val isPlaying: Boolean = false,
    val position: Long = 0L,
    val duration: Long = 0L,
    val albumArt: Bitmap? = null,
    val nextTrack: Track? = null,
    val isLoadingNextTrack: Boolean = false,
    val error: String? = null,
) {
    val activePlayer = players.firstOrNull { it.isActive }
}
