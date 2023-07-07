package io.upnextgpt.remote.palyer

import android.content.Context
import android.service.notification.StatusBarNotification
import io.upnextgpt.base.Logger
import io.upnextgpt.remote.NotificationCallback
import io.upnextgpt.remote.Notifications
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class NotificationBasedPlayer(
    private val context: Context,
    private val specifiedPackageName: String? = null,
) : RemotePlayer, NotificationCallback {
    private val coroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private var activeNotifications: Array<StatusBarNotification>? = null

    private var isPrepared = false

    private var currPlaybackInfo: PlaybackInfo? = null
    private val playbackInfoFlow = MutableSharedFlow<PlaybackInfo?>()

    override fun onListenerServiceConnected() {
        Logger.d(TAG, "onListenerServiceConnected()")
        Notifications.queryActiveNotifications(context)
    }

    override fun onListenerServiceDisconnected() {
        Logger.d(TAG, "onListenerServiceDisconnected()")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        Logger.d(TAG, "onNotificationPosted($sbn)")
        Notifications.queryActiveNotifications(context)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        Logger.d(TAG, "onNotificationRemoved($sbn)")
        Notifications.queryActiveNotifications(context)
    }

    override fun onActiveNotificationsUpdated(
        sbns: Array<StatusBarNotification>
    ) {
        Logger.d(TAG, "activeNotifications: ${sbns.size}")
        activeNotifications = sbns
        val playbackInfoList = MediaNotificationHelper.parse(context, sbns)
        Logger.d(TAG, "playStates: ${playbackInfoList.joinToString("\n")}")
        updatePlaybackInfoAndState(playbackInfoList)
    }

    private fun updatePlaybackInfoAndState(
        playbackInfoList: List<PlaybackInfo>
    ) {
        if (playbackInfoList.isEmpty()) {
            updatePlaybackInfo(null)
            return
        }

        // Find playing or specified
        var activePlaybackInfo = if (specifiedPackageName != null) {
            playbackInfoList.find { it.packageName == specifiedPackageName }
        } else {
            playbackInfoList.find { it.playState == PlayState.Playing }
        }

        // Find by previous package name or specified
        if (activePlaybackInfo == null && currPlaybackInfo != null) {
            activePlaybackInfo = if (specifiedPackageName != null) {
                playbackInfoList.find { it.packageName == specifiedPackageName }
            } else {
                playbackInfoList.find {
                    it.packageName == currPlaybackInfo?.packageName
                }
            }
        }

        if (activePlaybackInfo == null && specifiedPackageName == null) {
            activePlaybackInfo = playbackInfoList.first()
        }

        updatePlaybackInfo(activePlaybackInfo)
    }

    private fun updatePlaybackInfo(info: PlaybackInfo?) =
        coroutineScope.launch {
            currPlaybackInfo = info
            playbackInfoFlow.emit(info)
        }

    private fun currentNotification(): StatusBarNotification? {
        val currInfo = currPlaybackInfo ?: return null
        return activeNotifications?.find {
            it.id == currInfo.notificationId
        }
    }

    override fun prepare() {
        if (isPrepared()) {
            return
        }
        if (!isConnected()) {
            return
        }
        coroutineScope.launch {
            val isBound = Notifications.bindNotificationServiceSync(context)
            if (isBound) {
                Logger.d(TAG, "Registering notification callback")
                Notifications.registerNotificationCallback(this@NotificationBasedPlayer)
                Notifications.queryActiveNotifications(context)
            } else {
                Logger.e(TAG, "Cannot register notification callback")
            }
        }
        updatePlaybackInfo(null)
        isPrepared = true
    }

    override fun isPrepared(): Boolean {
        return isPrepared
    }

    override fun isConnected(): Boolean {
        return Notifications.isNotificationServiceEnabled(context)
    }

    override fun connect() {
        Notifications.requestNotificationPermission(context)
    }

    override fun play() {
        currentNotification()?.let {
            MediaNotificationHelper.findMediaController(it)?.play()
        }
    }

    override fun pause() {
        currentNotification()?.let {
            MediaNotificationHelper.findMediaController(it)?.pause()
        }
    }

    override fun prev() {
        currentNotification()?.let {
            MediaNotificationHelper.findMediaController(it)?.skipToPrevious()
        }
    }

    override fun next() {
        currentNotification()?.let {
            MediaNotificationHelper.findMediaController(it)?.skipToNext()
        }
    }

    override fun seek(position: Long) {
        Logger.d(TAG, "seekTo($position)")
        currentNotification()?.let {
            MediaNotificationHelper.findMediaController(it)?.seekTo(position)
            Notifications.queryActiveNotifications(context)
        }
    }

    override fun sync() {
        playbackInfoFlow.tryEmit(currPlaybackInfo)
    }

    override fun playbackInfoFlow(): Flow<PlaybackInfo?> {
        return playbackInfoFlow
            .onStart { emit(currPlaybackInfo) }
    }

    override fun destroy() {
        Notifications.unregisterNotificationCallback(this)
        Notifications.unbindNotificationService(context)
    }

    companion object {
        private const val TAG = "NotificationBasedPlayer"
    }
}