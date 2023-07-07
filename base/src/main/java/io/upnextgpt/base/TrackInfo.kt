package io.upnextgpt.base

import androidx.compose.runtime.Immutable

@Immutable
data class TrackInfo(
    val title: String,
    val artist: String? = null,
    val album: String? = null,
    val albumArtist: String? = null,
    val duration: Long = -1L,
)