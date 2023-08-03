package io.upnextgpt.remote

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.service.notification.StatusBarNotification
import android.text.TextUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.util.WeakHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

object Notifications {
    private const val ENABLED_NOTIFICATION_LISTENERS =
        "enabled_notification_listeners"
    private const val ACTION_NOTIFICATION_LISTENER_SETTINGS =
        "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"

    private val coroutineScope =
        CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val connections: WeakHashMap<Context, ServiceConnection> =
        WeakHashMap()

    private val callbacks: MutableList<NotificationCallback> = mutableListOf()

    private var isRegisteredBroadcast = false

    fun isNotificationServiceEnabled(context: Context): Boolean {
        val pkgName: String = context.packageName
        val flat: String = Settings.Secure.getString(
            context.contentResolver,
            ENABLED_NOTIFICATION_LISTENERS
        ) ?: return false
        if (!TextUtils.isEmpty(flat)) {
            val names = flat.split(":").toTypedArray()
            for (i in names.indices) {
                val cn = ComponentName.unflattenFromString(names[i])
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.packageName)) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun requestNotificationPermission(context: Context) {
        context.startActivity(
            Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        )
    }

    fun bindNotificationServiceSync(
        context: Context,
        timeoutMillis: Long = 5000L,
    ): Boolean {
        val latch = CountDownLatch(1)
        val intent = Intent(context, MediaNotificationService::class.java)
        val conn = object : ServiceConnection {
            override fun onServiceConnected(
                name: ComponentName?,
                service: IBinder?
            ) {
                latch.countDown()
            }

            override fun onServiceDisconnected(name: ComponentName?) {
            }
        }
        context.bindService(intent, conn, Context.BIND_AUTO_CREATE)
        connections[context] = conn

        if (!isRegisteredBroadcast) {
            registerNotificationBroadcastReceiver(context)
        }

        return latch.await(timeoutMillis, TimeUnit.MILLISECONDS)
    }

    @Synchronized
    private fun registerNotificationBroadcastReceiver(context: Context) {
        val filter = IntentFilter().apply {
            addAction(MediaNotificationService.ACTION_LISTENER_CONNECTED)
            addAction(MediaNotificationService.ACTION_LISTENER_DISCONNECTED)
            addAction(MediaNotificationService.ACTION_NOTIFICATION_POSTED)
            addAction(MediaNotificationService.ACTION_NOTIFICATION_REMOVED)
            addAction(MediaNotificationService.ACTION_ACTIVE_NOTIFICATIONS_UPDATED)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.applicationContext.registerReceiver(
                NotificationReceiver(coroutineScope),
                filter,
                Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            context.applicationContext.registerReceiver(
                NotificationReceiver(coroutineScope),
                filter
            )
        }
        isRegisteredBroadcast = true
    }

    fun unbindNotificationService(context: Context) {
        val conn = connections.remove(context)
        if (conn != null) {
            context.unbindService(conn)
        }
    }

    fun queryActiveNotifications(context: Context) {
        context.sendBroadcast(
            Intent(MediaNotificationService.ACTION_QUERY_ACTIVE_NOTIFICATIONS)
        )
    }

    fun registerNotificationCallback(callback: NotificationCallback) {
        callbacks.add(callback)
    }

    fun unregisterNotificationCallback(callback: NotificationCallback) {
        callbacks.remove(callback)
    }

    internal fun onListenerServiceConnected() {
        callbacks.forEach { it.onListenerServiceConnected() }
    }

    internal fun onListenerServiceDisconnected() {
        callbacks.forEach { it.onListenerServiceDisconnected() }
    }

    internal fun onNotificationPosted(sbn: StatusBarNotification) {
        callbacks.forEach { it.onNotificationPosted(sbn) }
    }

    internal fun onNotificationRemoved(sbn: StatusBarNotification) {
        callbacks.forEach { it.onNotificationRemoved(sbn) }
    }

    internal fun onActiveNotificationsUpdated(sbns: Array<StatusBarNotification>) {
        callbacks.forEach { it.onActiveNotificationsUpdated(sbns) }
    }
}