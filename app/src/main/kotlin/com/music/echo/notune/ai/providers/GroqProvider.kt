
package echo.music.iad1tya.notune.ai.providers

import android.content.Context
import echo.music.iad1tya.constants.GroqApiKey
import echo.music.iad1tya.notune.ai.*
import echo.music.iad1tya.utils.dataStore
import echo.music.iad1tya.utils.get
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.*
import javax.inject.Inject

class GroqProvider @Inject constructor(
    private val context: Context,
    private val client: HttpClient
) : AiProvider {
    override val type: AiProviderType = AiProviderType.GROQ

    override suspend fun isConfigured(): Boolean = context.dataStore.get(GroqApiKey, "").isNotBlank()

    override suspend fun getModels(): List<AiModel> = listOf(
        AiModel("llama-3.3-70b-versatile", "Llama 3.3 70B (Powerful)", listOf(ModelCapability.TEXT_GENERATION, ModelCapability.TOOL_USE)),
        AiModel("llama-3.1-8b-instant", "Llama 3.1 8B (Super Fast)", listOf(ModelCapability.TEXT_GENERATION, ModelCapability.TOOL_USE)),
        AiModel("mixtral-8x7b-32768", "Mixtral 8x7B", listOf(ModelCapability.TEXT_GENERATION))
    )

    override suspend fun generateResponse(
        prompt: String,
        modelId: String?,
        tools: List<AiTool>?,
        systemInstruction: String?
    ): Result<AiResponse> = runCatching {
        val apiKey = context.dataStore.get(GroqApiKey, "")
        val targetModel = modelId ?: "llama-3.3-70b-versatile"
        val url = "https://api.groq.com/openai/v1/chat/completions"

        val messages = mutableListOf<JsonObject>()
        if (systemInstruction != null) {
            messages.add(buildJsonObject {
                put("role", "system")
                put("content", systemInstruction)
            })
        }
        messages.add(buildJsonObject {
            put("role", "user")
            put("content", buildJsonArray { add(buildJsonObject { put("type", "text"); put("text", prompt) }) })
        })

        val requestBody = buildJsonObject {
            put("model", targetModel)
            put("messages", buildJsonArray { messages.forEach { add(it) } })
            
            if (tools != null) {
                put("tools", buildJsonArray {
                    tools.forEach { tool ->
                        add(buildJsonObject {
                            put("type", "function")
                            put("function", buildJsonObject {
                                put("name", tool.name)
                                put("description", tool.description)
                                put("parameters", buildJsonObject {
                                    put("type", "object")
                                    put("properties", buildJsonObject {
                                        tool.parameters.forEach { (name, param) ->
                                            put(name, buildJsonObject {
                                                put("type", param.type)
                                                put("description", param.description)
                                            })
                                        }
                                    })
                                    put("required", buildJsonArray {
                                        tool.parameters.filter { it.value.required }.keys.forEach { add(it) }
                                    })
                                })
                            })
                        })
                    }
                })
            }
        }

        val response: HttpResponse = client.post(url) {
            header("Authorization", "Bearer $apiKey")
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }

        if (!response.status.isSuccess()) {
            throw Exception("Groq API Error: ${response.status}")
        }

        val body: JsonObject = response.body()
        val choice = body["choices"]?.jsonArray?.firstOrNull()?.jsonObject
        val message = choice?.get("message")?.jsonObject
        val responseText = message?.get("content")?.jsonPrimitive?.content ?: ""
        
        val toolCalls = message?.get("tool_calls")?.jsonArray?.map { toolCall ->
            val tc = toolCall.jsonObject
            val func = tc["function"]?.jsonObject
            val name = func?.get("name")?.jsonPrimitive?.content ?: ""
            val argsStr = func?.get("arguments")?.jsonPrimitive?.content ?: "{}"
            val args = Json.parseToJsonElement(argsStr).jsonObject.mapValues { it.value.jsonPrimitive.content }
            
            ToolCall(tc["id"]?.jsonPrimitive?.content ?: "", name, args)
        }

        AiResponse(
            text = responseText,
            toolCalls = if (toolCalls.isNullOrEmpty()) null else toolCalls,
            modelId = targetModel
        )
    }
}
