package io.upnextgpt.data.api

interface Api {
    suspend fun <T> service(clazz: Class<T>): T

    companion object {
        const val BASE_URL = "https://upnextgpt.vercel.app"
    }
}

suspend inline fun <reified T> Api.service(): T {
    return service(T::class.java)
}