package io.upnextgpt.remote

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import io.upnextgpt.base.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MediaNotificationService : NotificationListenerService() {
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action ?: return
            coroutineScope.launch {
                when (action) {
                    ACTION_QUERY_ACTIVE_NOTIFICATIONS -> {
                        val sbns = activeNotifications
                        Notifications.onActiveNotificationsUpdated(sbns)
                    }
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        val binder = super.onBind(intent)
        Logger.d(TAG, "onBind()")
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        Logger.d(TAG, "onCreate()")

        val filter = IntentFilter()
        filter.addAction(ACTION_QUERY_ACTIVE_NOTIFICATIONS)
        registerReceiver(broadcastReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.d(TAG, "onDestroy()")
        unregisterReceiver(broadcastReceiver)
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        Logger.d(TAG, "onRebind()")
    }

    override fun onListenerConnected() {
        Notifications.onListenerServiceConnected()
        Notifications.onActiveNotificationsUpdated(activeNotifications)
        Logger.d(TAG, "onListenerConnected() notifications: ${activeNotifications.size}")
    }

    override fun onListenerDisconnected() {
        Notifications.onListenerServiceDisconnected()
        Logger.d(TAG, "onListenerDisconnected()")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null) {
            return
        }
        Notifications.onNotificationPosted(sbn)
        Logger.d(TAG, "onNotificationPosted()")
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        if (sbn == null) {
            return
        }
        Notifications.onNotificationRemoved(sbn)
        Logger.d(TAG, "onNotificationRemoved()")
    }

    companion object {
        private const val TAG = "MediaNotifObserver"

        internal const val ACTION_LISTENER_CONNECTED =
            "io.dokar.openlyrics.action.NOTIFICATION_SERVICE_CONNECTED"

        internal const val ACTION_LISTENER_DISCONNECTED =
            "io.dokar.openlyrics.action.NOTIFICATION_SERVICE_DISCONNECTED"

        internal const val ACTION_NOTIFICATION_POSTED =
            "io.dokar.openlyrics.action.NOTIFICATION_POSTED"

        internal const val ACTION_NOTIFICATION_REMOVED =
            "io.dokar.openlyrics.action.NOTIFICATION_REMOVED"

        internal const val ACTION_ACTIVE_NOTIFICATIONS_UPDATED =
            "io.dokar.openlyrics.action.ACTIVE_NOTIFICATION_UPDATED"

        internal const val ACTION_QUERY_ACTIVE_NOTIFICATIONS =
            "io.dokar.openlyrics.action.QUERY_ACTIVE_NOTIFICATIONS"

        internal const val EXTRA_NOTIFICATION =
            "io.dokar.openlyrics.extra.NOTIFICATION"

        internal const val EXTRA_ACTIVE_NOTIFICATIONS =
            "io.dokar.openlyrics.extra.ACTIVE_NOTIFICATIONS"
    }
}