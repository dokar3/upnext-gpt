package io.upnextgpt.data.api

import io.upnextgpt.data.settings.Settings
import kotlinx.coroutines.flow.firstOrNull
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class ApiImpl(
    private val settings: Settings,
) : Api {
    private var currentApiBaseUrl: String? = null

    private var retrofit: Retrofit? = null

    private val serviceCache = mutableMapOf<Class<*>, CachedService<*>>()

    @Suppress("unchecked_cast")
    override suspend fun <T> service(clazz: Class<T>): T {
        val apiBaseUrl = getApiBaseUrl()
        if (apiBaseUrl != currentApiBaseUrl) {
            return retrofitOf(apiBaseUrl)
                .create(clazz)
                .also {
                    serviceCache[clazz] = CachedService(
                        apiBaseUrl = apiBaseUrl,
                        service = it,
                    )
                }
        } else {
            val cached = serviceCache[clazz]
            val service = cached?.service as? T
            if (service != null && cached.apiBaseUrl == apiBaseUrl) {
                return service
            }
            return retrofitOf(apiBaseUrl)
                .create(clazz)
                .also {
                    serviceCache[clazz] = CachedService(
                        apiBaseUrl = apiBaseUrl,
                        service = it,
                    )
                }
        }
    }

    private suspend fun getApiBaseUrl(): String {
        return settings.apiBaseUrlFlow.firstOrNull() ?: Api.BASE_URL
    }

    @Synchronized
    private fun retrofitOf(apiBaseUrl: String): Retrofit {
        val curr = retrofit
        if (apiBaseUrl == currentApiBaseUrl && curr != null) {
            return curr
        }
        return Retrofit.Builder()
            .baseUrl(apiBaseUrl)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .also {
                currentApiBaseUrl = apiBaseUrl
                retrofit = it
            }
    }

    private class CachedService<out S>(
        val apiBaseUrl: String,
        val service: S,
    )
}