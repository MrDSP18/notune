package com.music.echo.notune.ai.core

data class AiInferenceResult(
    val rawResponse: String,
    val parsedIntentJson: String?,
    val executionTimeMs: Long,
    val modelUsed: String
)

interface NoAiRuntime {
    val modelId: String
    val isReady: Boolean
    suspend fun processPrompt(prompt: String, contextJson: String): AiInferenceResult
}
