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
        val res = try {
            val service = api.service<TrackService>()
            val body = TrackService.NextTrackBody(
                queue = queue.subList(
                    fromIndex = 0,
                    toIndex = minOf(queue.size, MAX_HISTORY_TRACKS)
                ),
            )
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

    companion object {
        private const val MAX_HISTORY_TRACKS = 20
    }
}