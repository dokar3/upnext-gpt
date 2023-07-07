package io.upnextgpt.remote

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.service.notification.StatusBarNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationReceiver(
    private val coroutineScope: CoroutineScope,
) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        coroutineScope.launch(Dispatchers.Default) {
            val action = intent?.action ?: return@launch
            when (action) {
                MediaNotificationService.ACTION_LISTENER_CONNECTED -> {
                    Notifications.onListenerServiceConnected()
                }
                MediaNotificationService.ACTION_LISTENER_DISCONNECTED -> {
                    Notifications.onListenerServiceDisconnected()
                }
                MediaNotificationService.ACTION_NOTIFICATION_POSTED -> {
                    val sbn = intent.getParcelableExtra<StatusBarNotification>(
                        MediaNotificationService.EXTRA_NOTIFICATION
                    )
                    if (sbn != null) {
                        Notifications.onNotificationPosted(sbn)
                    }
                }
                MediaNotificationService.ACTION_NOTIFICATION_REMOVED -> {
                    val sbn = intent.getParcelableExtra<StatusBarNotification>(
                        MediaNotificationService.EXTRA_NOTIFICATION
                    )
                    if (sbn != null) {
                        Notifications.onNotificationRemoved(sbn)
                    }
                }
                MediaNotificationService.ACTION_ACTIVE_NOTIFICATIONS_UPDATED -> {
                    val array = intent.getParcelableArrayExtra(
                        MediaNotificationService.EXTRA_ACTIVE_NOTIFICATIONS
                    )
                    if (array != null) {
                        val notifications = Array(array.size) {
                            array[it] as StatusBarNotification
                        }
                        Notifications.onActiveNotificationsUpdated(notifications)
                    }
                }
            }
        }
    }
}