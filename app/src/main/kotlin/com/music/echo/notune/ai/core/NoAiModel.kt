package com.music.echo.notune.ai.core

enum class NoAiModelType {
    LOCAL_LLM,      // On-device Gemma / Qwen GGUF model
    LOCAL_DEEPSEEK, // On-device DeepSeek distilled GGUF model
    RULE_BASED      // Zero-download NØ AI Lite rule engine
}

data class NoAiModel(
    val id: String,
    val name: String,
    val description: String,
    val type: NoAiModelType,
    val downloadSizeMb: Int,
    val activeMemoryMb: Int,
    val isInstalled: Boolean = false,
    val isRecommendedForDevice: Boolean = false
)

object NoAiModelRegistry {
    val Models = listOf(
        NoAiModel(
            id = "no_ai_lite",
            name = "NØ AI Lite",
            description = "Instant rule-based music assistant. 0MB download, 100% offline.",
            type = NoAiModelType.RULE_BASED,
            downloadSizeMb = 0,
            activeMemoryMb = 16,
            isInstalled = true,
            isRecommendedForDevice = true
        ),
        NoAiModel(
            id = "no_fast_gemma_1b",
            name = "NØ Fast (Gemma 3 1B)",
            description = "Fast conversational music AI designed for Android mobile devices.",
            type = NoAiModelType.LOCAL_LLM,
            downloadSizeMb = 650,
            activeMemoryMb = 1024,
            isInstalled = false,
            isRecommendedForDevice = true
        ),
        NoAiModel(
            id = "no_reason_deepseek_distill",
            name = "NØ Reason (DeepSeek Distilled)",
            description = "Advanced reasoning model for complex multi-intent music requests.",
            type = NoAiModelType.LOCAL_DEEPSEEK,
            downloadSizeMb = 1400,
            activeMemoryMb = 2048,
            isInstalled = false,
            isRecommendedForDevice = false
        )
    )
}
