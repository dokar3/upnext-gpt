package io.upnextgpt.data.model

import androidx.compose.runtime.Immutable
import com.squareup.moshi.JsonClass
import io.upnextgpt.data.db.Track as DbTrack

@Immutable
@JsonClass(generateAdapter = true)
data class Track(
    val title: String,
    val artist: String,
    val id: Long = (title + artist).hashCode().toLong(),
    val album: String? = null,
    val albumArtist: String? = null,
    val duration: Long = -1L,
    val liked: Boolean? = null,
    val disliked: Boolean? = null,
    val queueId: String? = null,
    val addedAt: Long = -1,
    val updatedAt: Long = -1,
)

fun Track.toDbTrack(): DbTrack = DbTrack(
    id = id,
    title = title,
    artist = artist,
    album = album,
    albumArtist = albumArtist,
    duration = duration,
    liked = liked?.let { if (it) 1 else 0 },
    disliked = disliked?.let { if (it) 1 else 0 },
    queueId = queueId,
    addedAt = addedAt,
    updatedAt = updatedAt,
)

fun DbTrack.toApiTrack(): Track = Track(
    id = id,
    title = title,
    artist = artist,
    album = album,
    albumArtist = albumArtist,
    duration = duration,
    liked = liked == 1L,
    disliked = liked == 1L,
    queueId = queueId,
    addedAt = addedAt,
    updatedAt = updatedAt,
)