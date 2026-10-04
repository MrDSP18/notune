package com.music.echo.notune.ai.core

import com.music.echo.notune.ai.music.MusicBrain
import com.music.echo.notune.ai.music.TrackFeatures
import com.music.echo.notune.ai.tools.CommandRouter
import com.music.echo.notune.ai.tools.ToolResult

object NoAiEngine {

    private var activeRuntime: NoAiRuntime = RuleBasedRuntime()
    private var activeModel: NoAiModel = NoAiModelRegistry.Models.first()

    fun getActiveModel(): NoAiModel = activeModel

    fun setActiveModel(model: NoAiModel) {
        activeModel = model
        activeRuntime = RuleBasedRuntime() // Fallback rule-based executor always active
    }

    suspend fun executeQuery(prompt: String): AiInferenceResult {
        return activeRuntime.processPrompt(prompt, contextJson = "{}")
    }

    suspend fun executeTool(toolName: String, args: Map<String, Any?>): ToolResult {
        return CommandRouter.dispatch(toolName, args)
    }

    fun scoreCandidateTrack(candidate: TrackFeatures, currentTrack: TrackFeatures?) =
        MusicBrain.scoreCandidate(candidate, currentTrack)
}
