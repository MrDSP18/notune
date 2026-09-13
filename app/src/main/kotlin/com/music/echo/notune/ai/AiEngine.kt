
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

@Singleton
class AiEngine @Inject constructor(
    @ApplicationContext private val context: Context,
    private val geminiProvider: GeminiProvider,
    private val groqProvider: GroqProvider,
    private val openRouterProvider: OpenRouterProvider,
    private val ollamaProvider: OllamaProvider,
    private val noTuneBasicAiProvider: NoTuneBasicAiProvider
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
        systemInstruction: String? = null
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
                val result = provider.generateResponse(prompt, tools = tools, systemInstruction = systemInstruction)
                if (result.isSuccess) return result
                lastError = result.exceptionOrNull()
                Timber.w("AI_ENGINE: Provider $providerType failed: ${lastError?.message}")
            }
        }

        return Result.failure(lastError ?: Exception("No AI providers configured or available"))
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
        AiTool("set_sleep_timer", "Set a sleep timer for music playback.", mapOf("minutes" to ToolParameter("string", "Duration in minutes e.g. 15, 30, 60")))
    )
}
