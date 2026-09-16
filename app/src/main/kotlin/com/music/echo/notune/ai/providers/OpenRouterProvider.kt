
package echo.music.iad1tya.notune.ai.providers

import android.content.Context
import echo.music.iad1tya.constants.OpenRouterApiKeyExtra
import echo.music.iad1tya.notune.ai.*
import echo.music.iad1tya.utils.dataStore
import echo.music.iad1tya.utils.get
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.*
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class OpenRouterProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val client: HttpClient
) : AiProvider {
    override val type: AiProviderType = AiProviderType.OPENROUTER

    override suspend fun isConfigured(): Boolean = context.dataStore.get(OpenRouterApiKeyExtra, "").isNotBlank()

    override suspend fun getModels(): List<AiModel> = listOf(
        AiModel("google/gemini-2.0-flash-exp:free", "Gemini 2.0 Flash (Free)", listOf(ModelCapability.TEXT_GENERATION), isFree = true),
        AiModel("meta-llama/llama-3.1-8b-instruct:free", "Llama 3.1 8B (Free)", listOf(ModelCapability.TEXT_GENERATION), isFree = true),
        AiModel("mistralai/mistral-7b-instruct:free", "Mistral 7B (Free)", listOf(ModelCapability.TEXT_GENERATION), isFree = true)
    )

    override suspend fun generateResponse(
        prompt: String,
        modelId: String?,
        tools: List<AiTool>?,
        systemInstruction: String?,
        history: List<AiChatMessage>?
    ): Result<AiResponse> = runCatching {
        val apiKey = context.dataStore.get(OpenRouterApiKeyExtra, "")
        val targetModel = modelId ?: "google/gemini-2.0-flash-exp:free"
        val url = "https://openrouter.ai/api/v1/chat/completions"

        val messages = mutableListOf<JsonObject>()
        if (systemInstruction != null) {
            messages.add(buildJsonObject {
                put("role", "system")
                put("content", systemInstruction)
            })
        }
        messages.add(buildJsonObject {
            put("role", "user")
            put("content", prompt)
        })

        val requestBody = buildJsonObject {
            put("model", targetModel)
            put("messages", buildJsonArray { messages.forEach { add(it) } })
        }

        val response: HttpResponse = client.post(url) {
            header("Authorization", "Bearer $apiKey")
            header("HTTP-Referer", "https://github.com/notune-music")
            header("X-Title", "NØTUNE Music")
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }

        if (!response.status.isSuccess()) {
            throw Exception("OpenRouter API Error: ${response.status}")
        }

        val body: JsonObject = response.body()
        val choice = body["choices"]?.jsonArray?.firstOrNull()?.jsonObject
        val message = choice?.get("message")?.jsonObject
        val responseText = message?.get("content")?.jsonPrimitive?.content ?: ""

        AiResponse(
            text = responseText,
            modelId = targetModel
        )
    }
}
