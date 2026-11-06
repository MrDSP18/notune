package echo.music.iad1tya.notune.ai.agent

import echo.music.iad1tya.notune.ai.planner.TaskPlan
import echo.music.iad1tya.notune.ai.tools.ToolResult
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

@Singleton
class AgentVerifier @Inject constructor() {

    fun verifyExecution(plan: TaskPlan, results: List<ToolResult>): Boolean {
        if (results.isEmpty() || results.size != plan.steps.size) {
            Timber.w("AgentVerifier: Step count mismatch")
            return false
        }
        val allSuccessful = results.all { it is ToolResult.Success }
        Timber.d("AgentVerifier: Execution verified. All successful = $allSuccessful")
        return allSuccessful
    }
}
