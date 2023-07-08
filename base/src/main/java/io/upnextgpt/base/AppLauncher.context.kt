package io.upnextgpt.base

import android.content.Context
import io.upnextgpt.base.util.IntentUtil

class ContextAppLauncher(
    context: Context,
) : AppLauncher {
    private val context = context.applicationContext

    override fun isInstalled(packageName: String): Boolean {
        return IntentUtil.isPackageInstalled(context, packageName)
    }

    override fun launchPackage(packageName: String) {
        IntentUtil.launchApp(context, packageName)
    }

    override fun playTrack(
        packageName: String,
        title: String,
        artist: String,
        album: String?
    ) {
        IntentUtil.playTrack(
            context = context,
            packageName = packageName,
            title = title,
            artist = artist,
            album = album,
        )
    }
}