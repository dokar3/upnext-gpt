package io.upnextgpt.data.fetcher

import io.upnextgpt.base.SealedResult
import io.upnextgpt.data.api.Api
import io.upnextgpt.data.api.TrackService
import io.upnextgpt.data.api.service
import io.upnextgpt.data.model.Track

class GptNextTrackFetcher(
    private val api: Api,
) : NextTrackFetcher {
    override suspend fun fetch(
        queue: List<Track>
    ): SealedResult<Track, Exception> {
        val service = api.service<TrackService>()
        val res = try {
            val body = TrackService.NextTrackBody(queue = queue)
            service.nextTrack(body)
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