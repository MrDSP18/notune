package com.music.echo.notune.ai.core

import com.music.echo.notune.ai.tools.CommandRouter
import com.music.echo.notune.ai.tools.ToolResult

class RuleBasedRuntime : NoAiRuntime {
    override val modelId = "no_ai_lite"
    override val isReady = true

    override suspend fun processPrompt(prompt: String, contextJson: String): AiInferenceResult {
        val startTime = System.currentTimeMillis()
        val lower = prompt.lowercase()

        val (toolName, args, message) = when {
            lower.contains("energetic") || lower.contains("workout") || lower.contains("hype") -> {
                Triple("flow", mapOf("action" to "start", "vibe" to "Energy", "energyShift" to "+0.3"), "Activated high energy NØ FLOW.")
            }
            lower.contains("chill") || lower.contains("relax") || lower.contains("calm") || lower.contains("sleep") -> {
                Triple("flow", mapOf("action" to "start", "vibe" to "Chill", "energyShift" to "-0.3"), "Activated calm NØ FLOW.")
            }
            lower.contains("queue") || lower.contains("up next") -> {
                Triple("queue", mapOf("action" to "update"), "Analyzed and adjusted upcoming queue.")
            }
            lower.contains("lyrics") || lower.contains("sing") || lower.contains("words") -> {
                Triple("lyrics", mapOf("action" to "fetch"), "Fetched lyrics for active track.")
            }
            lower.contains("room") || lower.contains("party") || lower.contains("friends") -> {
                Triple("room", mapOf("type" to "Chill"), "Created live NØ ROOM.")
            }
            lower.contains("studio") || lower.contains("bass") || lower.contains("eq") || lower.contains("spatial") -> {
                Triple("audio", mapOf("preset" to "Spatial 3D"), "Configured NØ STUDIO sound preset.")
            }
            lower.contains("share") || lower.contains("snap") || lower.contains("instagram") -> {
                Triple("sharing", mapOf("action" to "snap"), "Generated NØ SNAP share card.")
            }
            else -> {
                Triple("search", mapOf("query" to prompt), "Searched music catalog for '$prompt'.")
            }
        }

        val result: ToolResult = CommandRouter.dispatch(toolName, args)
        val elapsed = System.currentTimeMillis() - startTime

        return AiInferenceResult(
            rawResponse = "${result.message} [Executed via NØ AI Lite]",
            parsedIntentJson = """{"tool":"$toolName","success":${result.success}}""",
            executionTimeMs = elapsed,
            modelUsed = modelId
        )
    }
}
