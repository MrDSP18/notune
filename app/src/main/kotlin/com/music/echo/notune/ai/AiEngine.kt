
package echo.music.iad1tya.notune.ai

import android.content.Context
import echo.music.iad1tya.constants.PreferredAiProviderKey
import echo.music.iad1tya.notune.ai.providers.GeminiProvider
import echo.music.iad1tya.notune.ai.providers.GroqProvider
import echo.music.iad1tya.utils.dataStore
import echo.music.iad1tya.utils.get
import io.ktor.client.*
import kotlinx.coroutines.flow.first
import timber.log.Timber
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

import echo.music.iad1tya.notune.ai.providers.OpenRouterProvider
import echo.music.iad1tya.notune.ai.providers.OllamaProvider

import echo.music.iad1tya.notune.ai.providers.NoTuneBasicAiProvider
import echo.music.iad1tya.notune.personalization.repository.TasteProfileRepository
import android.os.BatteryManager
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Singleton
class AiEngine @Inject constructor(
    @ApplicationContext private val context: Context,
    private val geminiProvider: GeminiProvider,
    private val groqProvider: GroqProvider,
    private val openRouterProvider: OpenRouterProvider,
    private val ollamaProvider: OllamaProvider,
    private val noTuneBasicAiProvider: NoTuneBasicAiProvider,
    private val tasteProfileRepository: com.music.echo.notune.personalization.repository.TasteProfileRepository,
    private val toolManager: echo.music.iad1tya.notune.ai.tools.AiToolManager
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
        tools: List<AiTool>? = null,
        systemInstruction: String? = null,
        history: List<AiChatMessage>? = null
    ): Result<AiResponse> {
        val userPreferred = preferredProvider ?: context.dataStore.get(PreferredAiProviderKey, AiProviderType.NOTUNE_BASIC.name).let {
            runCatching { AiProviderType.valueOf(it) }.getOrDefault(AiProviderType.NOTUNE_BASIC)
        }

        // Fallback chain: Preferred -> Gemini -> Groq -> OpenRouter -> Ollama -> NOTUNE_BASIC
        val chain = mutableListOf(userPreferred)
        val allTypes = listOf(AiProviderType.GEMINI, AiProviderType.GROQ, AiProviderType.OPENROUTER, AiProviderType.OLLAMA, AiProviderType.NOTUNE_BASIC)
        allTypes.forEach { if (it != userPreferred) chain.add(it) }

        var lastError: Throwable? = null
        
        for (providerType in chain) {
            val provider = providers[providerType] ?: continue
            if (provider.isConfigured()) {
                Timber.d("AI_ENGINE: Trying provider $providerType")
                
                // Powerful AI: Attempt to use the most capable model available for reasoning
                val models = provider.getModels()
                val preferredModel = when (providerType) {
                    AiProviderType.GEMINI -> models.find { "pro" in it.id }?.id
                    AiProviderType.GROQ -> models.find { "70b" in it.id }?.id
                    else -> null
                }

                val result = provider.generateResponse(
                    prompt = prompt,
                    modelId = preferredModel,
                    tools = tools,
                    systemInstruction = systemInstruction,
                    history = history
                )
                if (result.isSuccess) return result
                lastError = result.exceptionOrNull()
                Timber.w("AI_ENGINE: Provider $providerType failed: ${lastError?.message}")
            }
        }

        return noTuneBasicAiProvider.generateResponse(prompt, tools = tools, systemInstruction = systemInstruction, history = history)
    }

    /**
     * Powerful reasoning loop that injects technical environment context
     * and executes multiple tool reasoning steps.
     */
    suspend fun runNeuralMatrixLoop(
        prompt: String,
        systemInstruction: String? = null,
        history: List<AiChatMessage>? = null,
        maxSteps: Int = 4
    ): Result<AiResponse> {
        // Efficiency: Trim history to last 10 messages to save tokens and maintain focus
        val trimmedHistory = history?.takeLast(10)
        
        val envContext = getEnvironmentalContext()
        val fullSystemInstruction = (systemInstruction ?: "") + "\n\nENVIRONMENT_MATRIX:\n$envContext"
        
        var currentPrompt = prompt
        var currentSteps = 0
        val tools = getMusicTools()
        
        // Initial neural pass
        var result = generateResponse(currentPrompt, tools = tools, systemInstruction = fullSystemInstruction, history = trimmedHistory)
        
        while (result.isSuccess && currentSteps < maxSteps) {
            val response = result.getOrThrow()
            if (response.toolCalls == null) return result
            
            // Parallel tool execution for efficiency
            val toolResults = response.toolCalls.map { call ->
                try {
                    val toolOutput = toolManager.executeTool(call)
                    "NODE_EXECUTION_SUCCESS [${call.functionName}]: $toolOutput"
                } catch (e: Exception) {
                    "NODE_EXECUTION_FAILURE [${call.functionName}]: ${e.message}"
                }
            }.joinToString("\n")
            
            Timber.d("NEURAL_MATRIX: Step $currentSteps - Execution Log:\n$toolResults")
            
            // Re-inject updated state for the next reasoning step
            // Enhanced prompt logic: Tell the AI exactly what happened
            currentPrompt += "\n\n--- STATE_UPDATE_LOG ---\n$toolResults\n\n--- INSTRUCTION ---\nTool execution complete. Synthesize the final status report for the user based on these results."
            
            // For subsequent steps in the loop, we don't send history to avoid confusion and save tokens
            result = generateResponse(currentPrompt, tools = tools, systemInstruction = fullSystemInstruction, history = null)
            currentSteps++
        }
        
        // If we hit max steps without a text response, force a summary
        if (result.isSuccess && result.getOrThrow().text.isBlank()) {
            return generateResponse(
                "SYSTEM_OVERRIDE: Reasoning loop limit reached. Provide a concise technical summary of the actions taken so far.",
                systemInstruction = fullSystemInstruction
            )
        }
        
        return result
    }

    private suspend fun getEnvironmentalContext(): String {
        val batteryStatus: android.content.Intent? = IntentFilter(android.content.Intent.ACTION_BATTERY_CHANGED).let { filter ->
            context.registerReceiver(null, filter)
        }
        val batteryPct = batteryStatus?.let { intent ->
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            level * 100 / scale.toFloat()
        } ?: "UNKNOWN"

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nw = connectivityManager.activeNetwork
        val act = connectivityManager.getNetworkCapabilities(nw)
        val networkType = when {
            act == null -> "OFFLINE"
            act.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WIFI_6_ENHANCED"
            act.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "CELLULAR_5G_READY"
            else -> "ETHERNET_LINK"
        }

        val taste = tasteProfileRepository.tasteProfile.first()
        val musicDna = "FAV_ARTISTS: ${taste.favoriteArtists.take(5).joinToString { it.name }}; TOP_GENRES: ${taste.favoriteGenres.take(3).joinToString()}"

        val player = toolManager.playerConnection
        val playbackState = if (player != null) {
            val meta = player.mediaMetadata.value
            "STATUS: ${if (player.isPlaying.value) "STREAMING" else "IDLE"}; TRACK: ${meta?.title ?: "NONE"}; ARTIST: ${meta?.artists?.joinToString { it.name } ?: "NONE"}"
        } else "PLAYER_ENGINE: DISCONNECTED"

        return """
            [TIME]: ${LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME)}
            [POWER]: $batteryPct%
            [NETWORK]: $networkType
            [MUSIC_DNA]: $musicDna
            [PLAYBACK_MATRIX]: $playbackState
        """.trimIndent()
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
