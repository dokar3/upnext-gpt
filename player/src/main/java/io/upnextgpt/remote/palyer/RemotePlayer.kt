package io.upnextgpt.remote.palyer

import kotlinx.coroutines.flow.Flow

interface RemotePlayer : PlayerActions {
    fun isConnected(): Boolean

    fun isControllable(): Boolean

    fun connect()

    fun prepare()

    fun isPrepared(): Boolean

    fun sync()

    fun playbackInfoFlow(): Flow<PlaybackInfo?>

    fun playbackEventFlow(): Flow<PlaybackEvent>

    fun destroy()

    enum class PlaybackEvent {
        TrackStarted,
        TrackFinished,
    }
}