package io.upnextgpt.base.util

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.NameNotFoundException
import android.provider.MediaStore
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

    fun playTrack(
        context: Context,
        packageName: String,
        title: String,
        artist: String,
        album: String? = null,
    ) {
        val intent =
            Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                `package` = packageName
                putExtra(MediaStore.EXTRA_MEDIA_TITLE, title)
                putExtra(MediaStore.EXTRA_MEDIA_ARTIST, artist)
                if (album != null) {
                    putExtra(MediaStore.EXTRA_MEDIA_ALBUM, album)
                }
                putExtra(SearchManager.QUERY, "$artist - $title")
            }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }
}