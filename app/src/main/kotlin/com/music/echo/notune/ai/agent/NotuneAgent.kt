package echo.music.iad1tya.notune.ai.agent

import echo.music.iad1tya.notune.ai.brain.ModelRouter
import echo.music.iad1tya.notune.ai.context.NotuneContext
import echo.music.iad1tya.notune.ai.models.ModelCapability
import echo.music.iad1tya.notune.ai.music.MusicIntelligenceEngine
import echo.music.iad1tya.notune.ai.planner.PlanExecutor
import echo.music.iad1tya.notune.ai.planner.PlanStep
import echo.music.iad1tya.notune.ai.planner.TaskPlan
import echo.music.iad1tya.notune.ai.safety.ToolGuard
import echo.music.iad1tya.notune.ai.tools.AiToolRegistry
import echo.music.iad1tya.notune.ai.trace.AgentTrace
import echo.music.iad1tya.notune.ai.trace.AgentTraceStep
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

sealed class AgentExecutionResult {
    data class Success(val responseMessage: String, val trace: AgentTrace) : AgentExecutionResult()
    data class Failure(val errorMessage: String, val trace: AgentTrace) : AgentExecutionResult()
}

@Singleton
class NotuneAgent @Inject constructor(
    private val modelRouter: ModelRouter,
    private val toolRegistry: AiToolRegistry,
    private val planExecutor: PlanExecutor,
    private val verifier: AgentVerifier,
    private val musicIntelligence: MusicIntelligenceEngine
) {
    suspend fun executeTask(
        userPrompt: String,
        context: NotuneContext = NotuneContext()
    ): AgentExecutionResult {
        val trace = AgentTrace(userPrompt = userPrompt, detectedIntent = "AUTOMATED_EXECUTION", selectedModel = "cloud_gemini")
        Timber.d("NotuneAgent: Received task '$userPrompt'")

        // 1. Understand & Model Routing
        val model = modelRouter.routeModel(ModelCapability.DEEP_REASONING)
        trace.steps.add(AgentTraceStep(1, "Model routed to ${model.providerId}"))

        // 2. Planning (Map prompt to multi-step execution plan)
        val plan = buildPlanFromPrompt(userPrompt)
        trace.steps.add(AgentTraceStep(2, "Plan constructed with ${plan.steps.size} steps"))

        // 3. Plan Execution
        val executionResult = planExecutor.executePlan(plan)
        return executionResult.fold(
            onSuccess = { toolResults ->
                val isVerified = verifier.verifyExecution(plan, toolResults)
                trace.steps.add(AgentTraceStep(3, "Execution verified: $isVerified"))

                if (isVerified) {
                    AgentExecutionResult.Success(
                        responseMessage = "Successfully executed plan: ${plan.objective}",
                        trace = trace
                    )
                } else {
                    AgentExecutionResult.Failure(
                        errorMessage = "Plan execution completed but verification failed",
                        trace = trace
                    )
                }
            },
            onFailure = { ex ->
                trace.steps.add(AgentTraceStep(3, "Execution failed: ${ex.message}", resultStatus = "FAILURE"))
                AgentExecutionResult.Failure(
                    errorMessage = ex.message ?: "Task execution failed",
                    trace = trace
                )
            }
        )
    }

    private fun buildPlanFromPrompt(prompt: String): TaskPlan {
        val lower = prompt.lowercase()
        return when {
            lower.contains("search") || lower.contains("play") -> {
                val trackName = prompt.substringAfter("play", prompt.substringAfter("search", "Tamil")).trim()
                TaskPlan(
                    objective = "Search & Play catalog track '$trackName'",
                    steps = listOf(
                        PlanStep("step_1", "search_music", "Search catalog for '$trackName'", mapOf("query" to trackName)),
                        PlanStep("step_2", "play_track", "Play resolved track", mapOf("trackName" to trackName))
                    )
                )
            }
            lower.contains("pause") -> {
                TaskPlan(
                    objective = "Pause audio playback",
                    steps = listOf(PlanStep("step_1", "pause", "Pause player"))
                )
            }
            lower.contains("resume") -> {
                TaskPlan(
                    objective = "Resume audio playback",
                    steps = listOf(PlanStep("step_1", "resume", "Resume player"))
                )
            }
            else -> {
                TaskPlan(
                    objective = "Generic playback action",
                    steps = listOf(PlanStep("step_1", "get_now_playing", "Fetch current playback status"))
                )
            }
        }
    }
}
