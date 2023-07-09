package io.upnextgpt.base.image

import android.content.Context
import android.net.Uri
import coil.ImageLoader
import coil.decode.DataSource
import coil.decode.ImageSource
import coil.disk.DiskCache
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.fetch.SourceResult
import coil.request.Options
import okio.buffer
import okio.source
import java.io.File

private const val MAX_CACHE_SIZE = 100L * 1024 * 1024
private const val DISK_CACHE_DIR = "images"

fun newImageLoader(
    context: Context,
    diskImageStore: DiskImageStore,
): ImageLoader {
    val appContext = context.applicationContext
    return ImageLoader.Builder(appContext)
        .diskCache(
            DiskCache.Builder()
                .directory(File(context.cacheDir, DISK_CACHE_DIR))
                .maxSizeBytes(MAX_CACHE_SIZE)
                .build()
        )
        .components {
            add(
                factory = LocalImageFetcher.Factory(
                    context = appContext,
                    diskImageStore = diskImageStore,
                ),
            )
        }
        .build()
}

class LocalImageFetcher(
    private val context: Context,
    private val key: String,
    private val diskImageStore: DiskImageStore,
) : Fetcher {
    override suspend fun fetch(): FetchResult? {
        val inputStream = diskImageStore.open(key) ?: return null
        return SourceResult(
            source = ImageSource(
                source = inputStream.source().buffer(),
                context = context,
            ),
            mimeType = null,
            dataSource = DataSource.DISK,
        )
    }

    class Factory(
        private val context: Context,
        private val diskImageStore: DiskImageStore,
    ) : Fetcher.Factory<Uri> {
        override fun create(
            data: Uri,
            options: Options,
            imageLoader: ImageLoader
        ): Fetcher? {
            if (data.scheme != DiskImageStore.URL_SCHEMA) {
                return null
            }
            val key = data.host ?: return null
            return LocalImageFetcher(
                context = context,
                key = key,
                diskImageStore = diskImageStore,
            )
        }
    }
}
