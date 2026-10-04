package com.music.echo.notune

import com.music.echo.notune.config.EnvironmentMode
import com.music.echo.notune.config.PlatformConfig
import com.music.echo.notune.release.NoReleaseEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LivePlatformIntegrationTest {

    @Before
    fun setUp() {
        PlatformConfig.setEnvironment(EnvironmentMode.PRODUCTION)
    }

    @Test
    fun testProductionBaseUrl() {
        val baseUrl = PlatformConfig.getBaseUrl()
        assertEquals("https://notune-api.dharansundarapandiyan24.workers.dev", baseUrl)
        assertTrue(baseUrl.startsWith("https://"))
    }

    @Test
    fun testWebSocketProtocol() {
        val wsUrl = PlatformConfig.getWebSocketBaseUrl()
        assertEquals("wss://notune-api.dharansundarapandiyan24.workers.dev", wsUrl)
        assertTrue(wsUrl.startsWith("wss://"))
    }

    @Test
    fun testEndpointBuilders() {
        assertEquals("https://notune-api.dharansundarapandiyan24.workers.dev/api/v1/health", PlatformConfig.healthEndpoint())
        assertEquals("https://notune-api.dharansundarapandiyan24.workers.dev/api/v1/ready", PlatformConfig.readyEndpoint())
        assertEquals("https://notune-api.dharansundarapandiyan24.workers.dev/api/v1/metrics", PlatformConfig.metricsEndpoint())
        assertEquals("https://notune-api.dharansundarapandiyan24.workers.dev/api/v1/version", PlatformConfig.versionEndpoint())
        assertEquals("https://notune-api.dharansundarapandiyan24.workers.dev/api/v1/releases/latest?platform=android&channel=stable", PlatformConfig.latestReleaseEndpoint())
        assertEquals("wss://notune-api.dharansundarapandiyan24.workers.dev/api/v1/rooms/ROOM123/ws", PlatformConfig.roomWebSocketEndpoint("ROOM123"))
    }

    @Test
    fun testReleaseEngineUrlBinding() {
        val url = NoReleaseEngine.getReleaseCheckUrl()
        assertTrue(url.contains("notune-api.dharansundarapandiyan24.workers.dev"))
        assertTrue(url.contains("channel=stable"))
    }

    @Test
    fun testEnvironmentSwitch() {
        PlatformConfig.setEnvironment(EnvironmentMode.DEVELOPMENT)
        assertEquals("http://10.0.2.2:8787", PlatformConfig.getBaseUrl())
        assertEquals("ws://10.0.2.2:8787", PlatformConfig.getWebSocketBaseUrl())

        PlatformConfig.setEnvironment(EnvironmentMode.PRODUCTION)
        assertEquals("https://notune-api.dharansundarapandiyan24.workers.dev", PlatformConfig.getBaseUrl())
    }
}
