package echo.music.iad1tya.models

data class TechnicalTelemetry(
    val bufferState: String = "IDLE",
    val bufferPercent: Int = 0,
    val bitrate: Int? = null,
    val sampleRate: Int? = null,
    val bitDepth: Int? = null,
    val codec: String? = null,
    val channelCount: Int? = null,
    val mimeType: String? = null,
    val audioSessionId: Int? = null,
    val memoryUsage: String = "--- MB",
    val networkStatus: String = "OFFLINE",
    val syncDrift: String = "0.00ms",
    val coreTemp: String = "NORMAL"
)
