
package echo.music.iad1tya.notune.ai.providers

import android.content.Context
import echo.music.iad1tya.constants.GeminiApiKey
import echo.music.iad1tya.notune.ai.*
import echo.music.iad1tya.utils.dataStore
import echo.music.iad1tya.utils.get
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import javax.inject.Inject

class GeminiProvider @Inject constructor(
    private val context: Context,
    private val client: HttpClient
) : AiProvider {
    override val type: AiProviderType = AiProviderType.GEMINI

    override suspend fun isConfigured(): Boolean = context.dataStore.get(GeminiApiKey, "").isNotBlank()

    override suspend fun getModels(): List<AiModel> = listOf(
        AiModel("gemini-1.5-flash", "Gemini 1.5 Flash (Fast & Free Tier)", listOf(ModelCapability.TEXT_GENERATION, ModelCapability.TOOL_USE)),
        AiModel("gemini-1.5-pro", "Gemini 1.5 Pro (Powerful)", listOf(ModelCapability.TEXT_GENERATION, ModelCapability.TOOL_USE))
    )

    override suspend fun generateResponse(
        prompt: String,
        modelId: String?,
        tools: List<AiTool>?,
        systemInstruction: String?
    ): Result<AiResponse> = runCatching {
        val apiKey = context.dataStore.get(GeminiApiKey, "")
        val targetModel = modelId ?: "gemini-1.5-flash"
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$targetModel:generateContent?key=$apiKey"

        val contents = mutableListOf<JsonObject>()
        
        // System instruction if supported by model (for Gemini we often put it in a separate field or as first message)
        val systemObj = systemInstruction?.let {
             buildJsonObject {
                 put("role", "system")
                 put("parts", buildJsonArray { add(buildJsonObject { put("text", it) }) })
             }
        }

        contents.add(buildJsonObject {
            put("role", "user")
            put("parts", buildJsonArray { add(buildJsonObject { put("text", prompt) }) })
        })

        val requestBody = buildJsonObject {
            put("contents", buildJsonArray { contents.forEach { add(it) } })
            if (systemObj != null) put("system_instruction", systemObj)
            
            if (tools != null) {
                put("tools", buildJsonArray {
                    add(buildJsonObject {
                        put("function_declarations", buildJsonArray {
                            tools.forEach { tool ->
                                add(buildJsonObject {
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
                            }
                        })
                    })
                })
            }
        }

        val response: HttpResponse = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }

        if (!response.status.isSuccess()) {
            throw Exception("Gemini API Error: ${response.status}")
        }

        val body: JsonObject = response.body()
        val candidate = body["candidates"]?.jsonArray?.firstOrNull()?.jsonObject
        val content = candidate?.get("content")?.jsonObject
        val parts = content?.get("parts")?.jsonArray
        
        var responseText = ""
        val toolCalls = mutableListOf<ToolCall>()

        parts?.forEach { part ->
            part.jsonObject["text"]?.jsonPrimitive?.content?.let { responseText += it }
            part.jsonObject["functionCall"]?.jsonObject?.let { func ->
                val callId = "call_${System.currentTimeMillis()}"
                val name = func["name"]?.jsonPrimitive?.content ?: ""
                val args = mutableMapOf<String, String>()
                func["args"]?.jsonObject?.forEach { (k, v) ->
                    args[k] = v.jsonPrimitive.content
                }
                toolCalls.add(ToolCall(callId, name, args))
            }
        }

        AiResponse(
            text = responseText,
            toolCalls = if (toolCalls.isEmpty()) null else toolCalls,
            modelId = targetModel
        )
    }
}
