package echo.music.iad1tya.notune.ai.providers

import com.music.echo.notune.personalization.repository.TasteProfileRepository
import echo.music.iad1tya.notune.ai.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first

@Singleton
class NoTuneBasicAiProvider @Inject constructor(
    private val tasteProfileRepository: TasteProfileRepository
) : AiProvider {

    override val type: AiProviderType = AiProviderType.LOCAL

    override suspend fun isConfigured(): Boolean = true // Always available zero-config offline

    override suspend fun getModels(): List<AiModel> = listOf(
        AiModel(
            id = "notune-basic-intelligence",
            name = "NØTUNE Basic AI (Offline)",
            capabilities = listOf(ModelCapability.TEXT_GENERATION, ModelCapability.TOOL_USE),
            isFree = true
        )
    )

    override suspend fun generateResponse(
        prompt: String,
        modelId: String?,
        tools: List<AiTool>?,
        systemInstruction: String?
    ): Result<AiResponse> {
        val lowerPrompt = prompt.lowercase()
        val tasteProfile = tasteProfileRepository.tasteProfile.first()

        val toolCalls = mutableListOf<ToolCall>()

        val responseText = when {
            lowerPrompt.startsWith("play ") -> {
                val songQuery = prompt.substringAfter("play ", "").trim()
                if (songQuery.isNotBlank()) {
                    toolCalls.add(ToolCall(functionName = "search_songs", arguments = mapOf("query" to songQuery)))
                    "Searching and playing '$songQuery'..."
                } else "NØTUNE Basic AI: Please specify what track or artist to play."
            }
            lowerPrompt.startsWith("search ") -> {
                val query = prompt.substringAfter("search ", "").trim()
                toolCalls.add(ToolCall(functionName = "search_songs", arguments = mapOf("query" to query)))
                "Searching for '$query'..."
            }
            "theme" in lowerPrompt -> {
                val theme = when {
                    "cyberpunk" in lowerPrompt || "tokyo" in lowerPrompt -> "tokyo_neon"
                    "midnight" in lowerPrompt -> "midnight"
                    "aurora" in lowerPrompt -> "aurora"
                    "synthwave" in lowerPrompt -> "retro_synthwave"
                    "emerald" in lowerPrompt -> "emerald_crystal"
                    "solar" in lowerPrompt -> "solaris_flame"
                    else -> "notune_pure"
                }
                toolCalls.add(ToolCall(functionName = "change_theme", arguments = mapOf("theme_id" to theme)))
                "Switching app theme palette to '$theme'..."
            }
            "logo" in lowerPrompt -> {
                val logo = when {
                    "cyber" in lowerPrompt -> "CYBERPUNK"
                    "orbit" in lowerPrompt -> "NEON_ORBIT"
                    "cube" in lowerPrompt -> "HYPER_CUBE"
                    "wave" in lowerPrompt -> "HARMONIC_WAVE"
                    "ring" in lowerPrompt -> "SPECTRUM_RING"
                    else -> "WORDMARK"
                }
                toolCalls.add(ToolCall(functionName = "change_logo", arguments = mapOf("logo_variant" to logo)))
                "Changing logo variant to '$logo'..."
            }
            "incognito" in lowerPrompt || "private" in lowerPrompt -> {
                val enable = "off" !in lowerPrompt && "disable" !in lowerPrompt
                toolCalls.add(ToolCall(functionName = "toggle_incognito", arguments = mapOf("enable" to enable.toString())))
                "Toggling private incognito session..."
            }
            "flow" in lowerPrompt -> {
                val enable = "off" !in lowerPrompt && "disable" !in lowerPrompt
                toolCalls.add(ToolCall(functionName = "toggle_flow", arguments = mapOf("enable" to enable.toString())))
                "Updating NØTUNE FLOW mode..."
            }
            "pause" in lowerPrompt -> {
                toolCalls.add(ToolCall(functionName = "pause_music", arguments = emptyMap()))
                "Pausing playback..."
            }
            "resume" in lowerPrompt -> {
                toolCalls.add(ToolCall(functionName = "resume_music", arguments = emptyMap()))
                "Resuming playback..."
            }
            "skip" in lowerPrompt || "next" in lowerPrompt -> {
                toolCalls.add(ToolCall(functionName = "skip_music", arguments = emptyMap()))
                "Skipping to next track..."
            }
            "previous" in lowerPrompt || "back" in lowerPrompt -> {
                toolCalls.add(ToolCall(functionName = "previous_music", arguments = emptyMap()))
                "Going to previous track..."
            }
            "timer" in lowerPrompt -> {
                toolCalls.add(ToolCall(functionName = "set_sleep_timer", arguments = mapOf("minutes" to "30")))
                "Setting sleep timer..."
            }
            "similar" in lowerPrompt || "like this" in lowerPrompt -> {
                "NØTUNE Basic AI: Analyzing track metadata and matching with your top genres (${tasteProfile.favoriteGenres.take(2).joinToString()}) to adjust your queue."
            }
            "calm" in lowerPrompt || "relax" in lowerPrompt || "chill" in lowerPrompt -> {
                toolCalls.add(ToolCall(functionName = "toggle_flow", arguments = mapOf("enable" to "true")))
                "NØTUNE Basic AI: Switching NØTUNE FLOW to Low Energy / Calm mode."
            }
            "energetic" in lowerPrompt || "workout" in lowerPrompt || "party" in lowerPrompt -> {
                toolCalls.add(ToolCall(functionName = "toggle_flow", arguments = mapOf("enable" to "true")))
                "NØTUNE Basic AI: Elevating queue energy to High for an upbeat session."
            }
            else -> {
                "NØTUNE Basic AI is active offline. Your playback and FLOW recommendations are optimized based on your Music DNA."
            }
        }

        return Result.success(
            AiResponse(
                text = responseText,
                toolCalls = toolCalls.ifEmpty { null },
                modelId = "notune-basic-intelligence"
            )
        )
    }
}
