package echo.music.iad1tya.notune.ai.trace

data class AgentTraceStep(
    val stepIndex: Int,
    val description: String,
    val toolId: String? = null,
    val resultStatus: String = "SUCCESS",
    val durationMs: Long = 0L,
    val details: String? = null
)

data class AgentTrace(
    val traceId: String = "trace_${System.currentTimeMillis()}",
    val userPrompt: String,
    val detectedIntent: String,
    val selectedModel: String,
    val steps: MutableList<AgentTraceStep> = mutableListOf(),
    val totalDurationMs: Long = 0L,
    val verificationSuccess: Boolean = true
)
