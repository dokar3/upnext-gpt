package io.upnextgpt.data.api

import io.upnextgpt.data.model.ApiResponse
import io.upnextgpt.data.model.TrackInfo
import retrofit2.http.Body
import retrofit2.http.POST

interface NextTrackService {
    @POST("/api/next-track")
    suspend fun nextTrack(
        @Body queue: List<TrackInfo>
    ): ApiResponse<TrackInfo>
}