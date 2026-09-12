package com.music.echo.notune.privacy.ai

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AIRequestSanitizer @Inject constructor() {

    private val sensitivePatterns = listOf(
        Regex("(?i)bearer\\s+[a-zA-Z0-9_\\-\\.~\\+\\/]+=*"),
        Regex("(?i)ey[Jj][a-zA-Z0-9_\\-\\.~\\+\\/]+=*"), // JWT pattern
        Regex("(?i)password\\s*=\\s*[^\\s]+"),
        Regex("(?i)api[_-]?key\\s*=\\s*[^\\s]+")
    )

    fun sanitizePrompt(rawPrompt: String): String {
        var sanitized = rawPrompt
        for (pattern in sensitivePatterns) {
            sanitized = pattern.replace(sanitized, "[REDACTED_SECRET]")
        }
        return sanitized.trim()
    }

    fun containsProhibitedPayload(text: String): Boolean {
        return text.contains("[REDACTED_SECRET]") || text.contains("PRIVATE_KEY")
    }
}
