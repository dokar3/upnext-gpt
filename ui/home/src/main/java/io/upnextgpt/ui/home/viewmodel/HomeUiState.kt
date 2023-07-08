package io.upnextgpt.ui.home.viewmodel

import android.graphics.Bitmap
import io.upnextgpt.ui.shared.R
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import io.upnextgpt.data.model.TrackInfo

internal val SupportedPlayers = listOf(
    PlayerMeta(
        packageName = "com.apple.android.music",
        name = "Apple Music",
        iconRes = R.drawable.apple_music,
        themeColor = Color(0xFFF43D54),
        isActive = false,
        isInstalled = false
    ),
    PlayerMeta(
        packageName = "com.spotify.music",
        name = "Spotify",
        iconRes = R.drawable.spotify,
        themeColor = Color(0xFF1ED760),
        isActive = false,
        isInstalled = false
    ),
    PlayerMeta(
        packageName = "com.google.android.apps.youtube.music",
        name = "Youtube Music",
        iconRes = R.drawable.youtube_music,
        themeColor = Color(0xFFF9061B),
        isActive = false,
        isInstalled = false
    ),
)

@Immutable
data class HomeUiState(
    val players: List<PlayerMeta> = SupportedPlayers,
    val isConnectedToPlayers: Boolean = true,
    val currTrack: TrackInfo? = null,
    val isPlaying: Boolean = false,
    val position: Long = 0L,
    val duration: Long = 0L,
    val albumArt: Bitmap? = null,
    val nextTrack: TrackInfo? = null,
    val isLoadingNextTrack: Boolean = false,
    val error: String? = null,
) {
    val activePlayer = players.firstOrNull { it.isActive }
}