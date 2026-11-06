package echo.music.iad1tya.notune.ai.planner

import echo.music.iad1tya.notune.ai.tools.AiToolRegistry
import echo.music.iad1tya.notune.ai.tools.ToolResult
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

data class PlanStep(
    val stepId: String,
    val toolId: String,
    val description: String,
    val arguments: Map<String, Any?> = emptyMap(),
    val expectedOutcome: String = "Action completed"
)

data class TaskPlan(
    val planId: String = "plan_${System.currentTimeMillis()}",
    val objective: String,
    val steps: List<PlanStep>,
    val verificationCriteria: String = "All steps executed successfully"
)

@Singleton
class PlanExecutor @Inject constructor(
    private val toolRegistry: AiToolRegistry
) {
    suspend fun executePlan(plan: TaskPlan): Result<List<ToolResult>> {
        val results = mutableListOf<ToolResult>()
        Timber.d("PlanExecutor: Starting plan '${plan.objective}' with ${plan.steps.size} steps")

        for (step in plan.steps) {
            Timber.d("PlanExecutor Step: [${step.stepId}] ${step.description}")
            val res = toolRegistry.executeTool(step.toolId, step.arguments)
            results.add(res)
            if (res is ToolResult.Failure && !res.isRecoverable) {
                Timber.w("PlanExecutor: Non-recoverable failure at step ${step.stepId}: ${res.reason}")
                return Result.failure(Exception("Step ${step.stepId} failed: ${res.reason}"))
            }
        }
        return Result.success(results)
    }
}
