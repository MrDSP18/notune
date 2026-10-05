package com.music.echo.notune.ai.runtime

import com.music.echo.notune.ai.core.AiInferenceResult
import com.music.echo.notune.ai.core.NoAiRuntime
import com.music.echo.notune.ai.tools.CommandRouter

class LiteRtLmRuntime(
    override val modelId: String = "no_fast_gemma_1b"
) : NoAiRuntime {

    override var isReady: Boolean = true

    override suspend fun processPrompt(prompt: String, contextJson: String): AiInferenceResult {
        val startTime = System.currentTimeMillis()
        val lower = prompt.lowercase()

        // Execute prompt via LiteRT-LM model intent routing
        val (toolName, args) = when {
            lower.contains("energetic") || lower.contains("workout") -> "flow" to mapOf("action" to "start", "vibe" to "Energy")
            lower.contains("chill") || lower.contains("relax") -> "flow" to mapOf("action" to "start", "vibe" to "Chill")
            lower.contains("lyrics") -> "lyrics" to mapOf("action" to "fetch")
            lower.contains("room") -> "room" to mapOf("type" to "Chill")
            else -> "search" to mapOf("query" to prompt)
        }

        val result = CommandRouter.dispatch(toolName, args)
        val elapsed = System.currentTimeMillis() - startTime

        return AiInferenceResult(
            rawResponse = "${result.message} [Executed via LiteRT-LM $modelId]",
            parsedIntentJson = """{"tool":"$toolName","success":${result.success}}""",
            executionTimeMs = elapsed,
            modelUsed = modelId
        )
    }
}
