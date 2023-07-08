package io.upnextgpt.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiResponse<T>(
    val ok: Boolean,
    val data: T?,
    val message: String?,
)