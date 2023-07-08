package io.upnextgpt.base

interface AppLauncher {
    fun isInstalled(packageName: String): Boolean

    fun lunchPackage(packageName: String)
}