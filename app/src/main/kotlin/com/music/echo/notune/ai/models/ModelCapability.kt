package echo.music.iad1tya.notune.ai.models

enum class ModelCapability {
    FAST_LOCAL,
    DEEP_REASONING,
    MUSIC_RANKING,
    OFFLINE_ONLY
}

interface NotuneModelProvider {
    val providerId: String
    val capabilities: Set<ModelCapability>
    suspend fun isAvailable(): Boolean
    suspend fun generateText(prompt: String, systemInstruction: String? = null): Result<String>
}

class CloudModelProvider(
    override val providerId: String = "cloud_gemini",
    private val apiCall: suspend (String, String?) -> Result<String>
) : NotuneModelProvider {
    override val capabilities: Set<ModelCapability> = setOf(ModelCapability.DEEP_REASONING, ModelCapability.MUSIC_RANKING)
    override suspend fun isAvailable(): Boolean = true
    override suspend fun generateText(prompt: String, systemInstruction: String?): Result<String> = apiCall(prompt, systemInstruction)
}

class OnDeviceModelProvider(
    override val providerId: String = "local_gemini_nano"
) : NotuneModelProvider {
    override val capabilities: Set<ModelCapability> = setOf(ModelCapability.FAST_LOCAL, ModelCapability.OFFLINE_ONLY)
    override suspend fun isAvailable(): Boolean = false // Enabled on supported devices via AICore
    override suspend fun generateText(prompt: String, systemInstruction: String?): Result<String> {
        return Result.failure(UnsupportedOperationException("On-device model fallback to cloud"))
    }
}
