package com.music.echo.notune.privacy.security

import timber.log.Timber

/**
 * Privacy-preserving Timber logging tree for NØTUNE.
 * Redacts OAuth tokens, authorization headers, private keys, room tokens, and credentials
 * before sending logs to Logcat.
 */
class SanitizingTimberTree : Timber.DebugTree() {

    private val sensitivePatterns = listOf(
        Regex("(?i)(authorization[:=\\s]+bearer\\s+)([^\"'\\s&,]+)"),
        Regex("(?i)(bearer|token|access_token|refresh_token|api_key|password|secret|room_code|session_token)[:=\\s]+\\s*[\"']?([^\"'\\s&,]+)")
    )

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val sanitizedMessage = sanitizeLogMessage(message)
        super.log(priority, tag, sanitizedMessage, t)
    }

    fun sanitizeLogMessage(rawMessage: String): String {
        var cleanMessage = rawMessage
        for (pattern in sensitivePatterns) {
            cleanMessage = pattern.replace(cleanMessage) { matchResult ->
                val prefix = matchResult.groups[1]?.value ?: "secret="
                if (prefix.lowercase().startsWith("authorization")) {
                    "Authorization: Bearer [REDACTED]"
                } else {
                    "$prefix[REDACTED]"
                }
            }
        }
        return cleanMessage
    }
}
