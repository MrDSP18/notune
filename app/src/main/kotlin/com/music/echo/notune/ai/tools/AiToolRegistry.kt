package echo.music.iad1tya.notune.ai.tools

import echo.music.iad1tya.notune.ai.safety.ActionPolicy
import echo.music.iad1tya.notune.ai.safety.ToolPermission
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

@Singleton
class AiToolRegistry @Inject constructor() {

    private val tools = mutableMapOf<String, AiTool>()

    fun registerTool(tool: AiTool) {
        tools[tool.id] = tool
        Timber.d("Registered AI Tool: ${tool.id} [${tool.permission}]")
    }

    fun getTool(id: String): AiTool? {
        return tools[id]
    }

    fun getAllTools(): List<AiTool> {
        return tools.values.toList()
    }

    suspend fun executeTool(id: String, args: Map<String, Any?>): ToolResult {
        val tool = getTool(id) ?: return ToolResult.Failure("Tool $id not registered", isRecoverable = false)
        return try {
            tool.execute(args)
        } catch (e: Exception) {
            Timber.e(e, "Error executing AI tool $id")
            ToolResult.Failure("Execution error: ${e.message}")
        }
    }
}
