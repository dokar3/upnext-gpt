package io.upnextgpt.base

interface AppLauncher {
    fun isInstalled(packageName: String): Boolean

    fun launchSelf()

    fun launchPackage(packageName: String)

    fun playTrack(
        packageName: String,
        title: String,
        artist: String,
        album: String?,
    )
}