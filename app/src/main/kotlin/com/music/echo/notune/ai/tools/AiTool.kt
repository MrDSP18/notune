package echo.music.iad1tya.notune.ai.tools

import echo.music.iad1tya.notune.ai.safety.ActionPolicy
import echo.music.iad1tya.notune.ai.safety.ToolPermission

sealed class ToolResult {
    data class Success(val data: Map<String, Any?> = emptyMap(), val message: String = "Action completed") : ToolResult()
    data class Failure(val reason: String, val isRecoverable: Boolean = true) : ToolResult()
    object RequiresConfirmation : ToolResult()
}

data class AiTool(
    val id: String,
    val name: String,
    val description: String,
    val permission: ToolPermission = ToolPermission.READ,
    val policy: ActionPolicy = ActionPolicy(),
    val execute: suspend (args: Map<String, Any?>) -> ToolResult
)
