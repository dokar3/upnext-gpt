package io.upnextgpt.data.fetcher

import io.upnextgpt.base.SealedResult
import io.upnextgpt.data.model.Track

interface NextTrackFetcher {
    suspend fun fetch(
        queue: List<Track>
    ): SealedResult<Track, Exception>
}