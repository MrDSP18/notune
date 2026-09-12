
package echo.music.iad1tya.notune.ai.providers

import android.content.Context
import echo.music.iad1tya.constants.OllamaBaseUrlKey
import echo.music.iad1tya.notune.ai.*
import echo.music.iad1tya.utils.dataStore
import echo.music.iad1tya.utils.get
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.*
import javax.inject.Inject

class OllamaProvider @Inject constructor(
    private val context: Context,
    private val client: HttpClient
) : AiProvider {
    override val type: AiProviderType = AiProviderType.OLLAMA

    override suspend fun isConfigured(): Boolean = context.dataStore.get(OllamaBaseUrlKey, "").isNotBlank()

    override suspend fun getModels(): List<AiModel> = listOf(
        AiModel("llama3", "Llama 3 (Local)", listOf(ModelCapability.TEXT_GENERATION), isFree = true),
        AiModel("mistral", "Mistral (Local)", listOf(ModelCapability.TEXT_GENERATION), isFree = true)
    )

    override suspend fun generateResponse(
        prompt: String,
        modelId: String?,
        tools: List<AiTool>?,
        systemInstruction: String?
    ): Result<AiResponse> = runCatching {
        val baseUrl = context.dataStore.get(OllamaBaseUrlKey, "http://localhost:11434")
        val targetModel = modelId ?: "llama3"
        val url = "$baseUrl/api/generate"

        val requestBody = buildJsonObject {
            put("model", targetModel)
            put("prompt", if (systemInstruction != null) "$systemInstruction\n\n$prompt" else prompt)
            put("stream", false)
        }

        val response: HttpResponse = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }

        if (!response.status.isSuccess()) {
            throw Exception("Ollama Error: ${response.status}")
        }

        val body: JsonObject = response.body()
        val responseText = body["response"]?.jsonPrimitive?.content ?: ""

        AiResponse(
            text = responseText,
            modelId = targetModel
        )
    }
}
