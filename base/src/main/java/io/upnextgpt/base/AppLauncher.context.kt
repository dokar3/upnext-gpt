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

    override fun lunchPackage(packageName: String) {
        IntentUtil.lunchApp(context, packageName)
    }
}