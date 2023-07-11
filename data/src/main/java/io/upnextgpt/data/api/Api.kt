package io.upnextgpt.data.api

interface Api {
    /**
     * Create a retrofit server.
     *
     * @throws IllegalArgumentException If an unsupported API base url
     * has been configured.
     */
    suspend fun <T> service(clazz: Class<T>): T

    companion object {
        const val BASE_URL = "https://upnextgpt.vercel.app"
    }
}

suspend inline fun <reified T> Api.service(): T {
    return service(T::class.java)
}