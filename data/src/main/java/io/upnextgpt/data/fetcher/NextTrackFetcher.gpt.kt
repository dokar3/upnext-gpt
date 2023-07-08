package io.upnextgpt.data.fetcher

import io.upnextgpt.base.SealedResult
import io.upnextgpt.data.api.NextTrackService
import io.upnextgpt.data.model.TrackInfo

class GptNextTrackFetcher(
    private val nextTrackService: NextTrackService,
) : NextTrackFetcher {
    override suspend fun fetch(
        queue: List<TrackInfo>
    ): SealedResult<TrackInfo, Exception> {
        val res = try {
            val body = NextTrackService.NextTrackBody(queue = queue)
            nextTrackService.nextTrack(body)
        } catch (e: Exception) {
            return SealedResult.Err(e)
        }
        return if (res.ok && res.data != null) {
            SealedResult.Ok(res.data)
        } else {
            SealedResult.Err(Exception(res.message))
        }
    }
}