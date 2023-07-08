package io.upnextgpt.data.model

import androidx.compose.runtime.Immutable
import com.squareup.moshi.JsonClass

@Immutable
@JsonClass(generateAdapter = true)
data class TrackInfo(
    val title: String,
    val artist: String,
    val album: String? = null,
    val albumArtist: String? = null,
    val duration: Long = -1L,
)