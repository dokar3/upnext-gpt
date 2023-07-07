// IMediaNotificationCallback.aidl
package io.upnextgpt.remote;

// Declare any non-default types here with import statements
import android.service.notification.StatusBarNotification;

interface IMediaNotificationCallback {
    void onListenerConnected();

    void onListenerDisconnected();

    void onNotificationPosted(in StatusBarNotification sbn);

    void onNotificationRemoved(in StatusBarNotification sbn);
}