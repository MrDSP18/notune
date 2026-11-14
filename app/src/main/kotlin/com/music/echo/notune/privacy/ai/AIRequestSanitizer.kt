package com.music.echo.notune.privacy.ai

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AIRequestSanitizer @Inject constructor() {

    private val secretPatterns = listOf(
        Regex("(?i)bearer\\s+[a-zA-Z0-9_\\-\\.~\\+\\/]+=*"),
        Regex("(?i)ey[Jj][a-zA-Z0-9_\\-\\.~\\+\\/]+=*"), // JWT pattern
        Regex("(?i)password\\s*=\\s*[^\\s]+"),
        Regex("(?i)api[_-]?key\\s*=\\s*[^\\s]+"),
        Regex("(?i)(BEGIN|END)\\s+(RSA|EC|OPENSSH|DSA|ENCRYPTED)?\\s*PRIVATE\\s+KEY")
    )

    private val privateKeyBlockPattern = Regex("(?s)-----BEGIN [A-Z0-9 ]*PRIVATE KEY-----.*?-----END [A-Z0-9 ]*PRIVATE KEY-----")
    private val emailPattern = Regex("(?i)[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
    private val phonePattern = Regex("(?<!\\d)(?:\\+\\d{1,3}[- ]?)?\\(?\\d{3}\\)?[-. ]?\\d{3}[-. ]?\\d{4}(?!\\d)")
    private val ipPattern = Regex("\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b")
    private val creditCardPattern = Regex("\\b(?:4[0-9]{12}(?:[0-9]{3})?|5[1-5][0-9]{14}|3[47][0-9]{13}|6(?:011|5[0-9]{2})[0-9]{12})\\b")

    fun sanitizePrompt(rawPrompt: String): String {
        var sanitized = rawPrompt
        
        sanitized = privateKeyBlockPattern.replace(sanitized, "[REDACTED_SECRET]")

        for (pattern in secretPatterns) {
            sanitized = pattern.replace(sanitized, "[REDACTED_SECRET]")
        }
        
        sanitized = emailPattern.replace(sanitized, "[REDACTED_EMAIL]")
        sanitized = phonePattern.replace(sanitized, "[REDACTED_PHONE]")
        sanitized = ipPattern.replace(sanitized, "[REDACTED_IP]")
        sanitized = creditCardPattern.replace(sanitized, "[REDACTED_CARD]")
        
        return sanitized.trim()
    }

    fun containsProhibitedPayload(text: String): Boolean {
        return listOf(
            "[REDACTED_SECRET]",
            "[REDACTED_EMAIL]",
            "[REDACTED_PHONE]",
            "[REDACTED_IP]",
            "[REDACTED_CARD]"
        ).any(text::contains)
    }
}
