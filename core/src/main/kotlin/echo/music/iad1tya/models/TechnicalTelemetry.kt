package echo.music.iad1tya.models

data class TechnicalTelemetry(
    val bufferState: String = "IDLE",
    val bufferPercent: Int = 0,
    val bitrate: String = "--- kbps",
    val memoryUsage: String = "--- MB",
    val networkStatus: String = "OFFLINE",
    val syncDrift: String = "0.00ms",
    val coreTemp: String = "NORMAL"
)
