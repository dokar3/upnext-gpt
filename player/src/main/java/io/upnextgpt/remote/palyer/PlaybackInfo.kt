package io.upnextgpt.remote.palyer

import android.graphics.Bitmap
import android.media.session.MediaSession
import io.upnextgpt.data.model.TrackInfo

data class PlaybackInfo(
    val mediaSession: MediaSession.Token,
    val notificationId: Int,
    val packageName: String,
    val title: String?,
    val artist: String?,
    val albumArtist: String?,
    val album: String?,
    val duration: Long,
    val position: Long,
    val speed: Float,
    val playState: PlayState,
    val albumArt: Bitmap?,
)

fun PlaybackInfo.toTrackInfo(): TrackInfo {
    return TrackInfo(
        title = this.title ?: "Unknown",
        artist = this.artist ?: "Unknown",
        album = this.album,
        albumArtist = this.albumArtist,
        duration = this.duration,
    )
}