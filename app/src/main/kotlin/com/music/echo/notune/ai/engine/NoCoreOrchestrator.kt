package echo.music.iad1tya.notune.ai.engine

import echo.music.iad1tya.notune.ai.AiEngine
import echo.music.iad1tya.notune.ai.AiResponse
import echo.music.iad1tya.notune.ai.tools.AiToolManager
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

@Singleton
class NoCoreOrchestrator @Inject constructor(
    private val aiEngine: AiEngine,
    private val toolManager: AiToolManager,
    private val mindMemory: NoMindMemory,
    private val dnaEngine: NoDnaEngine,
    private val guardSafety: NoGuardSafety
) {
    suspend fun processUserRequest(prompt: String): Result<AiResponse> {
        Timber.d("NøCore: Processing prompt '$prompt'")
        
        // 1. Sanitize prompt
        val cleanPrompt = guardSafety.sanitizePrompt(prompt)

        // 2. Fetch context & Music DNA
        val dnaSummary = dnaEngine.getDnaSummary()
        val systemInstruction = """
            You are NØTUNE Intelligence Engine (NøCore Orchestrator).
            Primary Domain: Music, playback control, adaptive queue, playlists, social rooms.
            USER MUSIC DNA: $dnaSummary
            Execute user commands using tools. Never state an action succeeded unless tool execution result verifies it.
        """.trimIndent()

        // 3. Execute reasoning loop
        val result = aiEngine.runNeuralMatrixLoop(
            prompt = cleanPrompt,
            systemInstruction = systemInstruction
        )

        // 4. Sanitize output text before returning
        return result.map { response ->
            response.copy(text = guardSafety.sanitizeOutput(response.text))
        }
    }
}
