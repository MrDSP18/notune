
package echo.music.iad1tya.notune.ai

import kotlinx.serialization.Serializable

@Serializable
enum class AiProviderType {
    NOTUNE_BASIC,
    GEMINI,
    GROQ,
    OPENROUTER,
    OLLAMA,
    LOCAL
}

@Serializable
enum class ModelCapability {
    TEXT_GENERATION,
    TOOL_USE,
    IMAGE_UNDERSTANDING,
    AUDIO_UNDERSTANDING
}

@Serializable
data class AiModel(
    val id: String,
    val name: String,
    val capabilities: List<ModelCapability>,
    val isFree: Boolean = true
)

interface AiProvider {
    val type: AiProviderType
    suspend fun isConfigured(): Boolean
    suspend fun getModels(): List<AiModel>
    suspend fun generateResponse(
        prompt: String,
        modelId: String? = null,
        tools: List<AiTool>? = null,
        systemInstruction: String? = null
    ): Result<AiResponse>
}

data class AiResponse(
    val text: String,
    val toolCalls: List<ToolCall>? = null,
    val usage: TokenUsage? = null,
    val modelId: String
)

data class ToolCall(
    val id: String,
    val functionName: String,
    val arguments: Map<String, String>
)

data class TokenUsage(
    val promptTokens: Int,
    val completionTokens: Int,
    val totalTokens: Int
)

data class AiTool(
    val name: String,
    val description: String,
    val parameters: Map<String, ToolParameter>
)

data class ToolParameter(
    val type: String, // "string", "number", "boolean"
    val description: String,
    val required: Boolean = true
)
