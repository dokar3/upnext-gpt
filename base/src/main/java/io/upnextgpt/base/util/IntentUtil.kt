package io.upnextgpt.base.util

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.NameNotFoundException
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.IntentCompat
import io.upnextgpt.base.Logger
import io.upnextgpt.base.MusicAppInfo
import io.upnextgpt.base.SealedResult

object IntentUtil {
    fun isPackageInstalled(context: Context, packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0) != null
        } catch (e: NameNotFoundException) {
            false
        }
    }

    fun launchApp(
        context: Context,
        packageName: String,
        flags: Int = Intent.FLAG_ACTIVITY_NEW_TASK,
    ): SealedResult<Unit, String> {
        return try {
            val intent = context.packageManager
                .getLaunchIntentForPackage(packageName)
                ?.apply { this.flags = flags }
            require(intent != null) {
                "Launch intent not found for package '$packageName'"
            }
            context.startActivity(intent)
            SealedResult.Ok(Unit)
        } catch (e: Exception) {
            Logger.e(this::class.simpleName!!, "Cannot launch app: $e")
            SealedResult.Err("Cannot launch app.")
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
                // https://developer.android.com/guide/components/intents-common#PlaySearch
                putExtra(
                    MediaStore.EXTRA_MEDIA_FOCUS,
                    "vnd.android.cursor.item/audio"
                )
                putExtra(IntentCompat.EXTRA_START_PLAYBACK, true)
                putExtra(MediaStore.EXTRA_MEDIA_TITLE, title)
                putExtra(MediaStore.EXTRA_MEDIA_ARTIST, artist)
                if (album != null) {
                    putExtra(MediaStore.EXTRA_MEDIA_ALBUM, album)
                }
                setPlayFromMediaQuery(packageName, title, artist)
            }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }

    private fun Intent.setPlayFromMediaQuery(
        packageName: String,
        title: String,
        artist: String
    ) {
        val query = when (packageName) {
            MusicAppInfo.AppleMusic.packageName -> title
            else -> "$artist - $title"
        }
        putExtra(SearchManager.QUERY, query)
    }

    fun openUrl(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            context.startActivity(intent)
        } catch (_: Exception) {
        }
    }
}