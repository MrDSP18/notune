package com.music.echo.notune.ai.tools

object CommandRouter {
    private val tools = mutableMapOf<String, NotuneAiTool>()

    init {
        registerTool(PlaybackTool())
        registerTool(QueueTool())
        registerTool(SearchTool())
        registerTool(PlaylistTool())
        registerTool(LyricsTool())
        registerTool(FlowTool())
        registerTool(MusicDnaTool())
        registerTool(HistoryTool())
        registerTool(AudioTool())
        registerTool(RoomTool())
        registerTool(SharingTool())
    }

    fun registerTool(tool: NotuneAiTool) {
        tools[tool.name] = tool
    }

    fun getTool(name: String): NotuneAiTool? = tools[name]

    fun getAllTools(): List<NotuneAiTool> = tools.values.toList()

    suspend fun dispatch(toolName: String, args: Map<String, Any?>): ToolResult {
        val tool = tools[toolName]
            ?: return ToolResult(
                success = false,
                actionName = toolName,
                message = "Tool '$toolName' is not registered in NØ AI Tool Registry."
            )
        return try {
            tool.execute(args)
        } catch (e: Exception) {
            ToolResult(
                success = false,
                actionName = toolName,
                message = "Tool '$toolName' execution error: ${e.message}"
            )
        }
    }
}
