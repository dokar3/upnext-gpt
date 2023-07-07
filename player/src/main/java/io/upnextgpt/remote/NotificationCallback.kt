package io.upnextgpt.remote

import android.service.notification.StatusBarNotification

interface NotificationCallback {
    fun onListenerServiceConnected()

    fun onListenerServiceDisconnected()

    fun onNotificationPosted(sbn: StatusBarNotification)

    fun onNotificationRemoved(sbn: StatusBarNotification)

    fun onActiveNotificationsUpdated(sbns: Array<StatusBarNotification>)
}