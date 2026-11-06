package echo.music.iad1tya.notune.ai.providers

import com.music.echo.notune.personalization.repository.TasteProfileRepository
import echo.music.iad1tya.notune.ai.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject

@Singleton
class NoTuneBasicAiProvider @Inject constructor(
    private val tasteProfileRepository: TasteProfileRepository
) : AiProvider {

    override val type: AiProviderType = AiProviderType.NOTUNE_BASIC

    override suspend fun isConfigured(): Boolean = true // Always available zero-config offline AI engine

    override suspend fun getModels(): List<AiModel> = listOf(
        AiModel(
            id = "notune-basic-intelligence",
            name = "NØTUNE Free Neural AI",
            capabilities = listOf(ModelCapability.TEXT_GENERATION, ModelCapability.TOOL_USE),
            isFree = true
        )
    )

    override suspend fun generateResponse(
        prompt: String,
        modelId: String?,
        tools: List<AiTool>?,
        systemInstruction: String?,
        history: List<AiChatMessage>?
    ): Result<AiResponse> {
        val lowerPrompt = prompt.lowercase()
        val lowerSystem = systemInstruction?.lowercase() ?: ""
        val tasteProfile = tasteProfileRepository.tasteProfile.first()

        val toolCalls = mutableListOf<ToolCall>()

        // 1. Detect structured JSON output requests (Playlist Generator or Recommendation Engine)
        val isJsonArrayRequest = "json array" in lowerPrompt || "json array" in lowerSystem || "array:" in lowerPrompt || "array:" in lowerSystem
        val isJsonObjectRequest = "json object" in lowerPrompt || "json object" in lowerSystem || "{" in lowerSystem || "playlist" in lowerSystem

        if (isJsonArrayRequest) {
            val songsJson = generateRecommendedSongsJson(lowerPrompt, tasteProfile.favoriteArtists.map { it.name })
            return Result.success(AiResponse(text = songsJson, modelId = "notune-basic-intelligence"))
        }

        if (isJsonObjectRequest && ("generate" in lowerPrompt || "create" in lowerPrompt || "playlist" in lowerPrompt || "mix" in lowerPrompt || "curate" in lowerPrompt || "weather" in lowerPrompt)) {
            val playlistJson = generatePlaylistJsonObject(lowerPrompt, tasteProfile.favoriteArtists.map { it.name })
            return Result.success(AiResponse(text = playlistJson, modelId = "notune-basic-intelligence"))
        }

        // 2. Intent and Tool Resolution
        val responseText = when {
            lowerPrompt.startsWith("play ") || lowerPrompt.startsWith("listen to ") || lowerPrompt.startsWith("put on ") -> {
                val songQuery = prompt.replace(Regex("(?i)^(play|listen to|put on)\\s+"), "").trim()
                if (songQuery.isNotBlank()) {
                    toolCalls.add(ToolCall(functionName = "search_songs", arguments = mapOf("query" to songQuery)))
                    "Searching and queuing '$songQuery' on NØTUNE..."
                } else "NØTUNE Neural AI: Please specify the song or artist you want to play."
            }

            lowerPrompt.startsWith("search ") || lowerPrompt.startsWith("find ") -> {
                val query = prompt.replace(Regex("(?i)^(search|find)\\s+"), "").trim()
                toolCalls.add(ToolCall(functionName = "search_songs", arguments = mapOf("query" to query)))
                "Searching NØTUNE network for '$query'..."
            }

            "theme" in lowerPrompt -> {
                val theme = when {
                    "cyberpunk" in lowerPrompt || "tokyo" in lowerPrompt || "neon" in lowerPrompt -> "tokyo_neon"
                    "midnight" in lowerPrompt || "dark" in lowerPrompt -> "midnight"
                    "aurora" in lowerPrompt || "green" in lowerPrompt -> "aurora"
                    "synthwave" in lowerPrompt || "retro" in lowerPrompt -> "retro_synthwave"
                    "emerald" in lowerPrompt || "crystal" in lowerPrompt -> "emerald_crystal"
                    "solar" in lowerPrompt || "sun" in lowerPrompt || "fire" in lowerPrompt -> "solaris_flame"
                    "monochrome" in lowerPrompt || "minimal" in lowerPrompt -> "monochrome_stark"
                    else -> "notune_pure"
                }
                toolCalls.add(ToolCall(functionName = "change_theme", arguments = mapOf("theme_id" to theme)))
                "Active theme palette changed to '$theme'."
            }

            "logo" in lowerPrompt -> {
                val logo = when {
                    "cyber" in lowerPrompt || "punk" in lowerPrompt -> "CYBERPUNK"
                    "orbit" in lowerPrompt || "neon" in lowerPrompt -> "NEON_ORBIT"
                    "cube" in lowerPrompt || "hyper" in lowerPrompt -> "HYPER_CUBE"
                    "wave" in lowerPrompt || "harmonic" in lowerPrompt -> "HARMONIC_WAVE"
                    "ring" in lowerPrompt || "spectrum" in lowerPrompt -> "SPECTRUM_RING"
                    else -> "WORDMARK"
                }
                toolCalls.add(ToolCall(functionName = "change_logo", arguments = mapOf("logo_variant" to logo)))
                "Updated app logo to '$logo' variant."
            }

            "incognito" in lowerPrompt || "private" in lowerPrompt -> {
                val enable = "off" !in lowerPrompt && "disable" !in lowerPrompt
                toolCalls.add(ToolCall(functionName = "toggle_incognito", arguments = mapOf("enable" to enable.toString())))
                if (enable) "Private session activated. Playback will not affect your Music DNA." else "Private session deactivated."
            }

            "flow" in lowerPrompt -> {
                val enable = "off" !in lowerPrompt && "disable" !in lowerPrompt
                toolCalls.add(ToolCall(functionName = "toggle_flow", arguments = mapOf("enable" to enable.toString())))
                if (enable) "NØTUNE FLOW mood radio engaged." else "NØTUNE FLOW mood radio disengaged."
            }

            "pause" in lowerPrompt || "stop" in lowerPrompt || "hold" in lowerPrompt -> {
                toolCalls.add(ToolCall(functionName = "pause_music", arguments = emptyMap()))
                "NEURAL_LINK: PLAYBACK_SUSPENDED."
            }

            "resume" in lowerPrompt || "unpause" in lowerPrompt || "play" in lowerPrompt && ("again" in lowerPrompt || "music" in lowerPrompt) -> {
                toolCalls.add(ToolCall(functionName = "resume_music", arguments = emptyMap()))
                "NEURAL_LINK: PLAYBACK_RESUMED."
            }

            "skip" in lowerPrompt || "next" in lowerPrompt || "don't like" in lowerPrompt -> {
                toolCalls.add(ToolCall(functionName = "skip_music", arguments = emptyMap()))
                "NEURAL_LINK: TRANSITIONING_TO_NEXT_NODE."
            }

            "previous" in lowerPrompt || "back" in lowerPrompt -> {
                toolCalls.add(ToolCall(functionName = "previous_music", arguments = emptyMap()))
                "NEURAL_LINK: REVERTING_TO_PREVIOUS_NODE."
            }

            "timer" in lowerPrompt || "sleep" in lowerPrompt -> {
                val minutes = Regex("\\d+").find(lowerPrompt)?.value ?: "30"
                toolCalls.add(ToolCall(functionName = "set_sleep_timer", arguments = mapOf("minutes" to minutes)))
                "SLEEP_CHRONOMETER_SET: $minutes MIN."
            }
            
            "vibe" in lowerPrompt || "horoscope" in lowerPrompt || "how am i" in lowerPrompt -> {
                toolCalls.add(ToolCall(functionName = "get_vibe_check", arguments = emptyMap()))
                "CALCULATING_ACOUSTIC_DNA_RESONANCE..."
            }

            "create playlist" in lowerPrompt || "make playlist" in lowerPrompt || "generate playlist" in lowerPrompt -> {
                val name = prompt.substringAfter("playlist", "AI Mix").trim().removePrefix("called ").removePrefix("named ")
                val cleanName = if (name.isNotBlank()) name else "NØTUNE Free AI Mix"
                toolCalls.add(ToolCall(functionName = "create_playlist", arguments = mapOf("name" to cleanName, "songs" to "Blinding Lights - The Weeknd, Starboy - The Weeknd, Nightcall - Kavinsky")))
                "Generating custom playlist '$cleanName'..."
            }

            "recommend" in lowerPrompt || "suggestion" in lowerPrompt || "discover" in lowerPrompt -> {
                val artists = tasteProfile.favoriteArtists.take(3).joinToString(", ")
                val summary = if (artists.isNotBlank()) "tailored to your favorites ($artists)" else "based on top global acoustic trends"
                "NØTUNE Free Neural AI: Here are recommended tracks $summary. Tap any song to play instantly."
            }

            "who are you" in lowerPrompt || "what are you" in lowerPrompt || "help" in lowerPrompt -> {
                "I am NØTUNE AI, your built-in autonomous music intelligence. I can play songs, curate custom playlists, switch theme palettes, toggle FLOW mood radio, change app logos, set sleep timers, and manage your queue 100% free with zero configuration required."
            }

            else -> {
                "NØTUNE Neural AI is online and active. Ready to curate music, adjust playback, or synthesize custom playlists for your session."
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

    private fun generateRecommendedSongsJson(prompt: String, favoriteArtists: List<String>): String {
        val result = JSONArray()
        if (favoriteArtists.isEmpty()) {
            // Heuristic fallback for zero-DNA state
            val trends = listOf("Popular Song", "New Release", "Top Hit", "Trending Track")
            trends.forEach { title ->
                result.put(JSONObject().put("title", title).put("artist", "Discovery"))
            }
        } else {
            favoriteArtists.forEach { artist ->
                result.put(JSONObject().put("title", "Essential Mix").put("artist", artist))
                result.put(JSONObject().put("title", "Deep Cut").put("artist", artist))
            }
        }
        return result.toString()
    }

    private fun generatePlaylistJsonObject(prompt: String, favoriteArtists: List<String>): String {
        val playlistTitle = when {
            "tamil" in prompt -> "Tamil Neural Matrix"
            "hindi" in prompt -> "Hindi Neural Matrix"
            "chill" in prompt -> "Acoustic Settle"
            else -> "Personalized AI Mix"
        }

        val jsonObj = JSONObject()
        jsonObj.put("name", playlistTitle)
        val songsArray = JSONArray()
        
        val targetArtists = favoriteArtists.ifEmpty { listOf("Global Trends") }
        targetArtists.take(10).forEach { artist ->
            songsArray.put(JSONObject().put("title", "Neural Selection").put("artist", artist))
        }
        
        jsonObj.put("songs", songsArray)
        return jsonObj.toString()
    }
}
