package io.upnextgpt.base

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color

enum class MusicAppInfo(
    val packageName: String,
    val appName: String,
    @DrawableRes
    val iconRes: Int,
    val themeColor: Color,
) {
    AppleMusic(
        packageName = "com.apple.android.music",
        appName = "Apple Music",
        iconRes = R.drawable.apple_music,
        themeColor = Color(0xFFF43D54),
    ),
    Spotify(
        packageName = "com.spotify.music",
        appName = "Spotify",
        iconRes = R.drawable.spotify,
        themeColor = Color(0xFF1ED760),
    ),
    YoutubeMusic(
        packageName = "com.google.android.apps.youtube.music",
        appName = "Youtube Music",
        iconRes = R.drawable.youtube_music,
        themeColor = Color(0xFFF9061B),
    ),
    YTMusicReVanced(
        packageName = "app.revanced.android.apps.youtube.music",
        appName = "YT Music ReVanced",
        iconRes = R.drawable.revanced_yt_music,
        themeColor = Color(0xFFF9061B),
    ),
    Tidal(
        packageName = "com.aspiro.tidal",
        appName = "Tidal",
        iconRes = R.drawable.tidal,
        themeColor = Color(0xFF161718),
    ),
    Deezer(
        packageName = "deezer.android.app",
        appName = "Deezer",
        iconRes = R.drawable.deezer,
        themeColor = Color(0xFF121216),
    ),
}