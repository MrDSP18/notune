package echo.music.iad1tya.notune.ai

import android.content.Context
import com.music.echo.notune.privacy.ai.AIPrivacyGateway
import com.music.echo.notune.privacy.ai.AiPrivacyDecision
import com.music.echo.notune.privacy.ai.AiPrivacyRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import echo.music.iad1tya.constants.AiPersonalizationKey
import echo.music.iad1tya.constants.PreferredAiProviderKey
import echo.music.iad1tya.constants.PrivateSessionEnabledKey
import echo.music.iad1tya.notune.ai.providers.GeminiProvider
import echo.music.iad1tya.notune.ai.providers.GroqProvider
import echo.music.iad1tya.notune.ai.providers.NoTuneBasicAiProvider
import echo.music.iad1tya.notune.ai.providers.OllamaProvider
import echo.music.iad1tya.notune.ai.providers.OpenRouterProvider
import echo.music.iad1tya.notune.ai.tools.AiToolManager
import echo.music.iad1tya.utils.dataStore
import echo.music.iad1tya.utils.get
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class AiEngine @Inject constructor(
    @ApplicationContext private val context: Context,
    private val geminiProvider: GeminiProvider,
    private val groqProvider: GroqProvider,
    private val openRouterProvider: OpenRouterProvider,
    private val ollamaProvider: OllamaProvider,
    private val noTuneBasicAiProvider: NoTuneBasicAiProvider,
    private val aiPrivacyGateway: AIPrivacyGateway,
    private val aiToolManagerProvider: Provider<AiToolManager>
) {
    private val providers = mapOf(
        AiProviderType.NOTUNE_BASIC to noTuneBasicAiProvider,
        AiProviderType.GEMINI to geminiProvider,
        AiProviderType.GROQ to groqProvider,
        AiProviderType.OPENROUTER to openRouterProvider,
        AiProviderType.OLLAMA to ollamaProvider
    )

    suspend fun getProviders(): List<AiProvider> = providers.values.toList()

    suspend fun testConnection(providerType: AiProviderType): Result<String> {
        val provider = providers[providerType] ?: return Result.failure(Exception("Provider not found"))
        return provider.generateResponse("Hello, are you online? Respond with 'ONLINE'").map { it.text }
    }

    suspend fun generateResponse(
        prompt: String,
        preferredProvider: AiProviderType? = null,
        modelId: String? = null,
        tools: List<AiTool>? = null,
        systemInstruction: String? = null,
        history: List<AiChatMessage>? = null
    ): Result<AiResponse> {
        val userPreferred = preferredProvider ?: context.dataStore.get(PreferredAiProviderKey, AiProviderType.NOTUNE_BASIC.name).let {
            runCatching { AiProviderType.valueOf(it) }.getOrDefault(AiProviderType.NOTUNE_BASIC)
        }

        // 1. Evaluate Privacy and Personalization Policies
        val isPrivateSession = context.dataStore.get(PrivateSessionEnabledKey, false)
        val isPersonalizationEnabled = context.dataStore.get(AiPersonalizationKey, true)

        val privacyResult = aiPrivacyGateway.evaluate(
            AiPrivacyRequest(
                prompt = prompt,
                provider = userPreferred,
                privateSession = isPrivateSession,
                aiPersonalizationEnabled = isPersonalizationEnabled
            )
        )

        if (privacyResult.decision == AiPrivacyDecision.DENY) {
            Timber.w("AI_ENGINE: Request blocked by privacy policy: %s", privacyResult.reason)
            return Result.success(
                AiResponse(
                    text = "PRIVACY_GUARD: ${privacyResult.reason ?: "AI requests are restricted under current privacy mode."}",
                    modelId = "notune-privacy-guard"
                )
            )
        }

        val effectivePrompt = privacyResult.prompt

        // 2. Select Provider Chain based on Privacy Level
        val chain = if (privacyResult.decision == AiPrivacyDecision.ALLOW_LOCAL) {
            listOf(AiProviderType.NOTUNE_BASIC, AiProviderType.OLLAMA)
        } else {
            val list = mutableListOf(userPreferred)
            val allTypes = listOf(AiProviderType.GEMINI, AiProviderType.GROQ, AiProviderType.OPENROUTER, AiProviderType.OLLAMA, AiProviderType.NOTUNE_BASIC)
            allTypes.forEach { if (it != userPreferred) list.add(it) }
            list
        }

        var lastError: Throwable? = null

        for (providerType in chain) {
            val provider = providers[providerType] ?: continue
            if (provider.isConfigured()) {
                Timber.d("AI_ENGINE: Trying provider $providerType")
                val result = provider.generateResponse(
                    effectivePrompt,
                    modelId = modelId,
                    tools = tools,
                    systemInstruction = systemInstruction,
                    history = history
                )
                if (result.isSuccess) return result
                lastError = result.exceptionOrNull()
                Timber.w("AI_ENGINE: Provider $providerType failed: ${lastError?.message}")
            }
        }

        return noTuneBasicAiProvider.generateResponse(
            effectivePrompt,
            modelId = modelId,
            tools = tools,
            systemInstruction = systemInstruction,
            history = history
        )
    }

    suspend fun runNeuralMatrixLoop(
        prompt: String,
        systemInstruction: String? = null,
        history: List<AiChatMessage>? = null,
        maxSteps: Int = 4,
        toolExecutor: (suspend (ToolCall) -> String)? = null
    ): Result<AiResponse> {
        val fullSystemInstruction = systemInstruction ?: "NØTUNE Neural Matrix Core"
        val tools = getMusicTools()
        val conversation = (history?.toMutableList() ?: mutableListOf()).apply {
            add(AiChatMessage("user", prompt))
        }

        var currentSteps = 0
        var currentPrompt = prompt

        var result = generateResponse(
            prompt = currentPrompt,
            tools = tools,
            systemInstruction = fullSystemInstruction,
            history = conversation.dropLast(1)
        )

        while (result.isSuccess && currentSteps < maxSteps) {
            val response = result.getOrThrow()
            val toolCalls = response.toolCalls

            // If no tools requested, return final model output
            if (toolCalls.isNullOrEmpty()) {
                return Result.success(response)
            }

            val executeSingleTool: suspend (ToolCall) -> String = { call ->
                if (toolExecutor != null) {
                    toolExecutor(call)
                } else {
                    aiToolManagerProvider.get().executeTool(call)
                }
            }

            val toolResults = mutableListOf<String>()

            for (toolCall in toolCalls) {
                Timber.d("AI_ENGINE: Executing tool %s with arguments: %s", toolCall.functionName, toolCall.arguments)
                val executionOutput = try {
                    executeSingleTool(toolCall)
                } catch (e: Exception) {
                    Timber.e(e, "AI_ENGINE: Failed to execute tool %s", toolCall.functionName)
                    "Tool execution error: ${e.message}"
                }
                toolResults.add("TOOL_RESULT [${toolCall.functionName}]: $executionOutput")

                // If user said "play X" and tool searched for songs, automatically play the first resolved song ID
                val isPlayRequest = prompt.trimStart().lowercase().let {
                    it.startsWith("play ") || it.startsWith("listen to ") || it.startsWith("put on ")
                }
                if (isPlayRequest && toolCall.functionName == "search_songs") {
                    val songId = Regex("\\(ID:\\s*([^)]+)\\)", RegexOption.IGNORE_CASE)
                        .find(executionOutput)?.groupValues?.getOrNull(1)?.trim()
                    if (!songId.isNullOrBlank()) {
                        val playResult = runCatching {
                            executeSingleTool(ToolCall(functionName = "play_song", arguments = mapOf("song_id" to songId)))
                        }.getOrElse { error -> "Playback failed: ${error.message ?: "Unknown error"}" }
                        toolResults.add("TOOL_RESULT [play_song]: $playResult")
                    }
                }
            }

            // Append assistant tool request and user feedback results to conversation history
            conversation.add(AiChatMessage("assistant", response.text.ifBlank { "EXECUTING_ACTIONS" }))
            val feedbackText = toolResults.joinToString("\n")
            conversation.add(AiChatMessage("user", "OBSERVED TOOL RESULTS:\n$feedbackText\nSynthesize the final technical summary for the user."))

            currentSteps++

            // Re-invoke AI model with the observed tool results
            result = generateResponse(
                prompt = "OBSERVED TOOL RESULTS:\n$feedbackText",
                tools = tools,
                systemInstruction = fullSystemInstruction,
                history = conversation
            )

            if (result.isFailure) {
                // If subsequent reasoning query fails, synthesize tool results directly
                val synthesizedFallback = if (response.text.isNotBlank()) {
                    "${response.text}\n\n[ACTIONS EXECUTED]:\n" + toolResults.joinToString("\n")
                } else {
                    toolResults.joinToString("\n")
                }
                return Result.success(
                    AiResponse(
                        text = synthesizedFallback,
                        modelId = response.modelId
                    )
                )
            }
        }

        return result
    }

    suspend fun getMusicTools(): List<AiTool> = listOf(
        AiTool(
            "search_songs",
            "Search for songs based on title, artist, or keywords.",
            mapOf("query" to ToolParameter("string", "The search query"))
        ),
        AiTool(
            "play_song",
            "Play a specific song by ID.",
            mapOf("song_id" to ToolParameter("string", "The YouTube video ID of the song"))
        ),
        AiTool(
            "get_user_history",
            "Get a summary of the user's recently played songs.",
            emptyMap()
        ),
        AiTool(
            "create_playlist",
            "Create a new playlist with a name and a list of song descriptions.",
            mapOf(
                "name" to ToolParameter("string", "The name of the playlist"),
                "songs" to ToolParameter("string", "A comma-separated list of 'Song Title - Artist'")
            )
        ),
        AiTool("pause_music", "Pause the current music playback.", emptyMap()),
        AiTool("resume_music", "Resume the current music playback.", emptyMap()),
        AiTool("skip_music", "Skip to the next song in the queue.", emptyMap()),
        AiTool("previous_music", "Go back to the previous song in the queue.", emptyMap()),
        AiTool("get_current_track", "Get information about the currently playing song.", emptyMap()),
        AiTool("explain_song", "Explain the meaning and background of a song.", mapOf("query" to ToolParameter("string", "The song title and artist to explain"))),
        AiTool("change_theme", "Change active experience theme palette.", mapOf("theme_id" to ToolParameter("string", "Theme ID or name e.g. tokyo_neon, midnight, aurora, synthwave"))),
        AiTool("change_logo", "Change dynamic app logo variant.", mapOf("logo_variant" to ToolParameter("string", "Logo variant title e.g. CYBERPUNK, HYPER_CUBE, NEON_ORBIT"))),
        AiTool("toggle_flow", "Enable or disable NØTUNE FLOW mood radio mode.", mapOf("enable" to ToolParameter("string", "true or false"))),
        AiTool("toggle_incognito", "Enable or disable private session mode.", mapOf("enable" to ToolParameter("string", "true or false"))),
        AiTool("set_sleep_timer", "Set a sleep timer for music playback.", mapOf("minutes" to ToolParameter("string", "Duration in minutes e.g. 15, 30, 60"))),
        AiTool("get_vibe_check", "Get user daily musical horoscope and acoustic DNA vibe check.", emptyMap()),
        AiTool("get_song_trivia", "Get interactive song trivia and creation background.", mapOf("query" to ToolParameter("string", "Song title"))),
        AiTool("get_artist_journey", "Get curated discography path for any artist.", mapOf("artist" to ToolParameter("string", "Artist name"))),
        AiTool("blend_genres", "Blend two genres into a hybrid queue.", mapOf("genre1" to ToolParameter("string", "First genre"), "genre2" to ToolParameter("string", "Second genre"))),
        AiTool("decade_time_machine", "Transpose queue aesthetic to a specific decade.", mapOf("decade" to ToolParameter("string", "e.g. 80s, 90s, 00s"))),
        AiTool("ai_doctor_playlist", "Diagnose playlist health and fix duplicate tracks.", emptyMap()),
        AiTool("set_equalizer_preset", "Set audio equalizer preset profile.", mapOf("preset" to ToolParameter("string", "e.g. BASS_BOOST, SYNTHWAVE, VOCAL_CLEAR"))),
        AiTool("translate_lyrics", "Translate sync lyrics into target language.", mapOf("language" to ToolParameter("string", "Target language e.g. English, Hindi"))),
        AiTool("generate_singalong_guide", "Generate phonetic guide for active lyrics.", emptyMap()),
        AiTool("get_similarity_score", "Compute acoustic similarity score between tracks.", emptyMap()),
        AiTool("get_couple_compatibility", "Calculate Music DNA match score for Couple Mode.", emptyMap())
    )
}
