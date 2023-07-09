package io.upnextgpt.data

import io.upnextgpt.data.api.ApiImpl
import io.upnextgpt.data.api.service
import io.upnextgpt.data.settings.MemorySettings
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import retrofit2.http.GET

interface TestService {
    @GET("/test")
    fun dummy()
}

class ApiImplTest {
    @Test
    fun testGetService() = runBlocking {
        val settings = MemorySettings()
        val api = ApiImpl(settings = settings)

        val old = api.service<TestService>()
        val new = api.service<TestService>()
        // equals() not working here?!
        assertEquals(
            System.identityHashCode(old),
            System.identityHashCode(new)
        )

        settings.updateApiBaseUrl("https://fake.url")

        val third = api.service<TestService>()
        assertNotEquals(
            System.identityHashCode(old),
            System.identityHashCode(third)
        )
    }
}