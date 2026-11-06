package com.music.echo.notune.intelligence

import com.music.echo.notune.intelligence.intent.IntentEngine
import com.music.echo.notune.intelligence.intent.StructuredUserIntent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModelRouter @Inject constructor(
    private val intentEngine: IntentEngine
) {

    fun routeAndParseIntent(query: String, llmSuggestion: String? = null): StructuredUserIntent {
        // Fallback to deterministic intent engine if LLM suggestion is absent
        return intentEngine.parseIntent(query)
    }
}
