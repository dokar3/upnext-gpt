package io.upnextgpt.remote.palyer

import android.content.Context
import android.service.notification.StatusBarNotification
import io.upnextgpt.base.Logger
import io.upnextgpt.remote.NotificationCallback
import io.upnextgpt.remote.Notifications
import io.upnextgpt.remote.palyer.RemotePlayer.PlaybackEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class NotificationBasedPlayer(
    private val context: Context,
) : RemotePlayer, NotificationCallback {
    private val coroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private var targetPlayer: String? = null

    private var activeNotifications: Array<StatusBarNotification>? = null

    private var isPrepared = false

    private var currPlaybackInfo: PlaybackInfo? = null
    private val playbackInfoFlow = MutableSharedFlow<PlaybackInfo?>()
    private val playbackEventFlow = MutableSharedFlow<PlaybackEvent?>()

    private var listenToFinishJob: Job? = null

    override fun onListenerServiceConnected() {
        Notifications.queryActiveNotifications(context)
    }

    override fun onListenerServiceDisconnected() {
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        Notifications.queryActiveNotifications(context)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        Notifications.queryActiveNotifications(context)
    }

    override fun onActiveNotificationsUpdated(
        sbns: Array<StatusBarNotification>
    ) {
        activeNotifications = sbns
        val playbackInfoList = MediaNotificationHelper.parse(context, sbns)
        updatePlaybackInfoAndState(playbackInfoList)
    }

    fun updateTargetPlayer(packageName: String?) {
        if (targetPlayer == packageName) {
            return
        }
        targetPlayer = packageName
        pause()
        Notifications.queryActiveNotifications(context)
    }

    private fun updatePlaybackInfoAndState(
        playbackInfoList: List<PlaybackInfo>
    ) {
        if (playbackInfoList.isEmpty()) {
            updatePlaybackInfo(null)
            return
        }

        if (targetPlayer != null) {
            val info = playbackInfoList.find {
                it.packageName == targetPlayer
            }
            updatePlaybackInfo(info)
            return
        }

        // Find playing or specified
        var activePlaybackInfo = playbackInfoList.find {
            it.playState == PlayState.Playing
        }

        // Find by previous package name or specified
        if (activePlaybackInfo == null && currPlaybackInfo != null) {
            activePlaybackInfo = playbackInfoList.find {
                it.packageName == currPlaybackInfo?.packageName
            }
        }

        // Use the first
        if (activePlaybackInfo == null) {
            activePlaybackInfo = playbackInfoList.first()
        }

        updatePlaybackInfo(activePlaybackInfo)
    }

    private fun updatePlaybackInfo(
        info: PlaybackInfo?
    ) = coroutineScope.launch {
        currPlaybackInfo = info
        playbackInfoFlow.emit(info)
        triggerPlaybackEventsIfNeeded(info)
    }

    private fun triggerPlaybackEventsIfNeeded(info: PlaybackInfo?) {
        info ?: return
        listenToFinishJob?.cancel()
        coroutineScope.launch {
            if (info.playState != PlayState.Playing) {
                playbackEventFlow.emit(null)
                return@launch
            }
            if (info.position < 50) {
                playbackEventFlow.emit(PlaybackEvent.TrackStarted)
            }
            if (info.duration > 0) {
                listenToFinishJob = launch {
                    val actualDelay = (info.duration - info.position) /
                            info.speed - 50
                    delay(actualDelay.toLong())
                    playbackEventFlow.emit(PlaybackEvent.TrackFinished)
                }
            }
        }
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
                Notifications.registerNotificationCallback(this@NotificationBasedPlayer)
                Notifications.queryActiveNotifications(context)
            } else {
                Logger.e(TAG, "Cannot register notification callback")
            }
        }
        updatePlaybackInfo(currPlaybackInfo)
        isPrepared = true
    }

    override fun isPrepared(): Boolean {
        return isPrepared
    }

    override fun isConnected(): Boolean {
        return Notifications.isNotificationServiceEnabled(context)
    }

    override fun isControllable(): Boolean {
        return currentNotification() != null
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
        currentNotification()?.let {
            MediaNotificationHelper.findMediaController(it)?.seekTo(position)
            Notifications.queryActiveNotifications(context)
        }
    }

    override fun sync() {
        Notifications.queryActiveNotifications(context)
        playbackInfoFlow.tryEmit(currPlaybackInfo)
    }

    override fun playbackInfoFlow(): Flow<PlaybackInfo?> {
        return playbackInfoFlow
            .onStart { emit(currPlaybackInfo) }
    }

    override fun playbackEventFlow(): Flow<PlaybackEvent> {
        return playbackEventFlow
            .distinctUntilChanged()
            .filterNotNull()
    }

    override fun unobserve() {
        isPrepared = false
        listenToFinishJob?.cancel()
        Notifications.unregisterNotificationCallback(this)
        Notifications.unbindNotificationService(context)
    }

    companion object {
        private const val TAG = "NotificationBasedPlayer"
    }
}