package io.upnextgpt.data.fetcher

import io.upnextgpt.base.SealedResult
import io.upnextgpt.data.model.TrackInfo

interface NextTrackFetcher {
    suspend fun fetch(
        queue: List<TrackInfo>
    ): SealedResult<TrackInfo, Exception>
}