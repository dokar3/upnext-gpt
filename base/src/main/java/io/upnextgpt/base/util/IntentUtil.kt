package io.upnextgpt.base.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.NameNotFoundException
import io.upnextgpt.base.Logger
import io.upnextgpt.base.SealedResult

object IntentUtil {
    fun isPackageInstalled(context: Context, packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0) != null
        } catch (e: NameNotFoundException) {
            false
        }
    }

    fun lunchApp(
        context: Context,
        packageName: String
    ): SealedResult<Unit, String> {
        return try {
            val intent = context.packageManager
                .getLaunchIntentForPackage(packageName)
                ?.apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
            require(intent != null) {
                "Lunch intent not found for package '$packageName'"
            }
            context.startActivity(intent)
            SealedResult.Ok(null)
        } catch (e: Exception) {
            Logger.e(this::class.simpleName!!, "Cannot lunch app: $e")
            SealedResult.Err("Cannot lunch app.")
        }
    }
}