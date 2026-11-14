package echo.music.iad1tya.notune.ai.engine

import android.content.Context
import com.music.echo.notune.privacy.PrivateSessionManager
import dagger.hilt.android.qualifiers.ApplicationContext
import echo.music.iad1tya.constants.AiPersonalizationKey
import echo.music.iad1tya.notune.ai.AiChatMessage
import echo.music.iad1tya.notune.ai.AiEngine
import echo.music.iad1tya.notune.ai.AiResponse
import echo.music.iad1tya.notune.ai.ToolCall
import echo.music.iad1tya.notune.ai.tools.AiToolManager
import echo.music.iad1tya.utils.dataStore
import echo.music.iad1tya.utils.get
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

@Singleton
class NoCoreOrchestrator @Inject constructor(
    @ApplicationContext private val context: Context,
    private val aiEngine: AiEngine,
    private val toolManager: AiToolManager,
    private val mindMemory: NoMindMemory,
    private val dnaEngine: NoDnaEngine,
    private val guardSafety: NoGuardSafety,
    private val privateSessionManager: PrivateSessionManager
) {
    suspend fun processUserRequest(
        prompt: String,
        history: List<AiChatMessage>? = null,
        toolExecutor: (suspend (ToolCall) -> String)? = null
    ): Result<AiResponse> {
        Timber.d("NøCore: Processing user request")
        
        // 1. Sanitize prompt via Safety Gateway
        val cleanPrompt = guardSafety.sanitizePrompt(prompt)

        // 2. Evaluate Privacy & Personalization Gateway
        val isPrivateSession = privateSessionManager.isPrivateSession.value
        val isPersonalizationEnabled = runCatching {
            context.dataStore.get(AiPersonalizationKey, true)
        }.getOrDefault(true)

        val allowPersonalization = !isPrivateSession && isPersonalizationEnabled

        // 3. Fetch context & Music DNA & Mind Memory
        val dnaSummary = if (allowPersonalization) dnaEngine.getDnaSummary() else "Default / Unpersonalized (Privacy Mode Active)"
        val memories = if (allowPersonalization) {
            runCatching { mindMemory.memoryFacts.first() }.getOrDefault(emptyList())
        } else {
            emptyList()
        }
        val memoryContext = if (memories.isNotEmpty()) {
            "\nRECALLED USER PREFERENCES & MEMORIES:\n" + memories.take(10).joinToString("\n") { "- $it" }
        } else ""

        val systemInstruction = """
            [IDENTITY]: NØTUNE KERNEL // NEURAL_OS (NøCore Orchestrator)
            [PURPOSE]: Autonomous Music Ecosystem Management.
            [CAPABILITIES]: Playback Control, Queue Manipulation, Taste Adaptation, Playlist Curation, Global Search, Lyrics Translation.
            [USER MUSIC DNA]: $dnaSummary$memoryContext
            
            [EXECUTION RULES]:
            1. Parse intent: If user asks to play, queue, search, or alter settings, execute appropriate tools immediately.
            2. Never invent playback or search results. Strictly report verified tool output.
            3. If user expresses personal music tastes, genres, or artist affinities, note them.
            4. Keep output concise, industrial, and helpful.
        """.trimIndent()

        // 4. Execute reasoning loop with tool authorization and safety check
        val executor: suspend (ToolCall) -> String = { call ->
            val validation = guardSafety.validateToolCall(call)
            if (!validation.isValid) {
                if (validation.requiresConfirmation) {
                    "Confirmation Required: ${validation.reason}"
                } else {
                    "Security Error: ${validation.reason}"
                }
            } else if (toolExecutor != null) {
                toolExecutor(call)
            } else {
                toolManager.executeTool(call)
            }
        }

        val result = aiEngine.runNeuralMatrixLoop(
            prompt = cleanPrompt,
            systemInstruction = systemInstruction,
            history = history,
            toolExecutor = executor
        )

        // 5. Memory extraction: learn preferences only when personalization is allowed
        if (allowPersonalization) {
            extractAndRememberPreferences(cleanPrompt)
        }

        // 6. Sanitize output text before returning
        return result.map { response ->
            response.copy(text = guardSafety.sanitizeOutput(response.text))
        }
    }

    private suspend fun extractAndRememberPreferences(prompt: String) {
        val lower = prompt.trim().lowercase()
        val memoryTriggers = listOf(
            "i like ", "i love ", "i prefer ", "my favorite ", "remember that i ",
            "always play ", "never play "
        )
        for (trigger in memoryTriggers) {
            if (lower.contains(trigger)) {
                val statement = prompt.trim()
                if (statement.length in 8..120) {
                    runCatching { mindMemory.rememberFact(statement) }
                }
                break
            }
        }
    }
}
