package io.upnextgpt.remote.palyer

import android.content.Context
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.service.notification.StatusBarNotification

object MediaNotificationHelper {
    private val mediaControllers =
        mutableMapOf<MediaSession.Token, MediaController>()

    fun parse(
        context: Context,
        sbns: Array<StatusBarNotification>
    ): List<PlaybackInfo> {
        return sbns.mapNotNull { parseNotification(context, it) }
    }

    private fun parseNotification(
        context: Context,
        sbn: StatusBarNotification
    ): PlaybackInfo? {
        val token = findMediaSession(sbn) ?: return null
        val mediaController = mediaControllers.getOrPut(token) {
            MediaController(context, token)
        }

        val metadata = mediaController.metadata
        val playbackState = mediaController.playbackState

        val playState = when (playbackState?.state) {
            PlaybackState.STATE_PLAYING -> {
                PlayState.Playing
            }

            PlaybackState.STATE_STOPPED -> {
                PlayState.Stopped
            }

            PlaybackState.STATE_PAUSED -> {
                PlayState.Paused
            }

            else -> {
                PlayState.Paused
            }
        }

        return PlaybackInfo(
            mediaSession = token,
            notificationId = sbn.id,
            packageName = sbn.packageName,
            title = metadata?.getString(MediaMetadata.METADATA_KEY_TITLE),
            artist = metadata?.getString(MediaMetadata.METADATA_KEY_ARTIST),
            albumArtist = metadata?.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST),
            album = metadata?.getString(MediaMetadata.METADATA_KEY_ALBUM),
            duration = metadata?.getLong(MediaMetadata.METADATA_KEY_DURATION)
                ?: 0L,
            position = playbackState?.position ?: 0L,
            speed = playbackState?.playbackSpeed ?: 1.0f,
            playState = playState,
            albumArt = metadata?.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART),
        )
    }

    fun findMediaController(sbn: StatusBarNotification): MediaController.TransportControls? {
        val token = findMediaSession(sbn) ?: return null
        return mediaControllers[token]?.transportControls
    }

    private fun findMediaSession(sbn: StatusBarNotification): MediaSession.Token? {
        val extras = sbn.notification.extras
        return extras.getParcelable("android.mediaSession")
    }
}
