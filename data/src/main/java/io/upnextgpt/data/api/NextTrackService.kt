package io.upnextgpt.data.api

import com.squareup.moshi.JsonClass
import io.upnextgpt.data.model.ApiResponse
import io.upnextgpt.data.model.Track
import retrofit2.http.Body
import retrofit2.http.POST

interface NextTrackService {
    @POST("/api/next-track")
    suspend fun nextTrack(
        @Body body: NextTrackBody
    ): ApiResponse<Track>

    @JsonClass(generateAdapter = true)
    data class NextTrackBody(
        val queue: List<Track>,
    )
}