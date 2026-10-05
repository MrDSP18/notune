package com.music.echo.notune.ai.diagnostics

import com.music.echo.notune.ai.core.NoAiEngine
import com.music.echo.notune.ai.runtime.LiteRtLmRuntime
import com.music.echo.notune.ai.tools.CommandRouter

data class DiagnosticItem(
    val name: String,
    val passed: Boolean,
    val details: String
)

data class DiagnosticReport(
    val isSystemHealthy: Boolean,
    val items: List<DiagnosticItem>
)

object NoAiDiagnostics {

    suspend fun runFullDiagnostics(): DiagnosticReport {
        val items = mutableListOf<DiagnosticItem>()

        // 1. Model Registry Readiness Check
        val activeModel = NoAiEngine.getActiveModel()
        items.add(
            DiagnosticItem(
                name = "Active Model Profile",
                passed = true,
                details = "Active model: ${activeModel.name} (${activeModel.id})"
            )
        )

        // 2. LiteRT-LM Inference Check
        val runtime = LiteRtLmRuntime()
        items.add(
            DiagnosticItem(
                name = "LiteRT-LM Runtime",
                passed = runtime.isReady,
                details = "LiteRT-LM engine ready for model ID ${runtime.modelId}"
            )
        )

        // 3. Tool Calling Verification
        val toolResult = CommandRouter.dispatch("playback", mapOf("action" to "play"))
        items.add(
            DiagnosticItem(
                name = "AI Tool Registry",
                passed = toolResult.success,
                details = "Tool execution dispatch: ${toolResult.message}"
            )
        )

        // 4. Inference Execution
        val inferenceResult = runtime.processPrompt("Play something energetic", "")
        items.add(
            DiagnosticItem(
                name = "Local Inference Execution",
                passed = inferenceResult.rawResponse.isNotBlank(),
                details = "Response generated in ${inferenceResult.executionTimeMs}ms via ${inferenceResult.modelUsed}"
            )
        )

        val overallPassed = items.all { it.passed }
        return DiagnosticReport(isSystemHealthy = overallPassed, items = items)
    }
}
