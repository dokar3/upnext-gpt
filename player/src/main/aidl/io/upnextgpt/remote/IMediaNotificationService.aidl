// IMediaNotificationService.aidl
package io.upnextgpt.remote;

// Declare any non-default types here with import statements
import io.upnextgpt.remote.IMediaNotificationCallback;

interface IMediaNotificationService {
    void registerCallback(IMediaNotificationCallback callback);

    void unregisterCallback(IMediaNotificationCallback callback);
}