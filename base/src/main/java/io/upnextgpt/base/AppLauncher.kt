package io.upnextgpt.base

interface AppLauncher {
    fun isInstalled(packageName: String): Boolean

    fun launchPackage(packageName: String)

    fun playTrack(
        packageName: String,
        title: String,
        artist: String,
        album: String?,
    )
}