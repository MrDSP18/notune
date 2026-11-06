package com.music.echo.notune.config

enum class EnvironmentMode {
    PRODUCTION,
    DEVELOPMENT
}

object PlatformConfig {
    const val PRODUCTION_API_BASE_URL = "https://notune-api.dharansundarapandiyan24.workers.dev"
    const val LOCAL_API_BASE_URL = "http://10.0.2.2:8787"
    const val DEFAULT_CANONICAL_DOMAIN = "notune.app"

    @Volatile
    private var currentEnvironment: EnvironmentMode = EnvironmentMode.PRODUCTION

    fun setEnvironment(mode: EnvironmentMode) {
        currentEnvironment = mode
    }

    fun getEnvironment(): EnvironmentMode = currentEnvironment

    fun getBaseUrl(): String {
        return when (currentEnvironment) {
            EnvironmentMode.PRODUCTION -> PRODUCTION_API_BASE_URL
            EnvironmentMode.DEVELOPMENT -> LOCAL_API_BASE_URL
        }
    }

    fun getWebSocketBaseUrl(): String {
        return getBaseUrl().replace("^http".toRegex(), "ws")
    }

    fun healthEndpoint(): String = "${getBaseUrl()}/api/v1/health"
    fun readyEndpoint(): String = "${getBaseUrl()}/api/v1/ready"
    fun metricsEndpoint(): String = "${getBaseUrl()}/api/v1/metrics"
    fun versionEndpoint(): String = "${getBaseUrl()}/api/v1/version"

    fun latestReleaseEndpoint(platform: String = "android", channel: String = "stable"): String {
        return "${getBaseUrl()}/api/v1/releases/latest?platform=$platform&channel=$channel"
    }

    fun sharesEndpoint(): String = "${getBaseUrl()}/api/v1/shares"

    fun roomWebSocketEndpoint(roomId: String): String {
        val cleanedRoomId = roomId.trim().replace("^#".toRegex(), "")
        return "${getWebSocketBaseUrl()}/api/v1/rooms/$cleanedRoomId/ws"
    }
}
