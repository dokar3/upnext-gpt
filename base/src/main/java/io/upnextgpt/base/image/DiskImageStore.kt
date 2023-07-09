package io.upnextgpt.base.image

import android.content.Context
import android.graphics.Bitmap
import com.jakewharton.disklrucache.DiskLruCache
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream

class DiskImageStore(
    context: Context,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    private val imagesDir = File(context.filesDir, "images")

    private val diskCache = DiskLruCache.open(
        imagesDir,
        1,
        1,
        MAX_CACHE_SIZE
    )

    suspend fun save(
        bitmap: Bitmap,
        key: String,
        quality: Int = 85,
    ) = withContext(dispatcher) {
        diskCache.edit(key).let {
            it.newOutputStream(0).use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, it)
            }
            it.commit()
        }
    }

    suspend fun open(key: String): InputStream? = withContext(dispatcher) {
        diskCache.get(key).getInputStream(0)
    }

    suspend fun delete(key: String) = withContext(dispatcher) {
        diskCache.remove(key)
    }

    suspend fun clear() = withContext(dispatcher) {
        diskCache.delete()
    }

    companion object {
        const val URL_SCHEMA = "local-image"
        private const val MAX_CACHE_SIZE = 200L * 1024 * 1024

        fun url(key: String): String {
            return "$URL_SCHEMA://$key"
        }
    }
}