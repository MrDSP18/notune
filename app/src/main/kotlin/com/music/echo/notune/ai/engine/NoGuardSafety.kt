package echo.music.iad1tya.notune.ai.engine

import echo.music.iad1tya.notune.ai.ToolCall
import echo.music.iad1tya.notune.ai.safety.ActionPolicy
import echo.music.iad1tya.notune.ai.safety.ToolGuard
import echo.music.iad1tya.notune.ai.safety.ToolPermission
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

data class ToolValidationResult(
    val isValid: Boolean,
    val reason: String? = null,
    val requiresConfirmation: Boolean = false
)

@Singleton
class NoGuardSafety @Inject constructor(
    private val toolGuard: ToolGuard
) {
    constructor() : this(ToolGuard())

    private val toolPolicies: Map<String, Pair<ToolPermission, ActionPolicy>> = mapOf(
        // High risk / Confirmation required actions
        "leave_room" to Pair(ToolPermission.HIGH_RISK, ActionPolicy(requiresConfirmation = true, isDestructive = true)),
        "create_room" to Pair(ToolPermission.HIGH_RISK, ActionPolicy(requiresConfirmation = true)),
        "invite_friend" to Pair(ToolPermission.HIGH_RISK, ActionPolicy(requiresConfirmation = true)),
        "toggle_incognito" to Pair(ToolPermission.HIGH_RISK, ActionPolicy(requiresConfirmation = true)),

        // Medium risk
        "join_room" to Pair(ToolPermission.MEDIUM_RISK, ActionPolicy()),

        // Low risk write / playback
        "play_song" to Pair(ToolPermission.LOW_RISK_WRITE, ActionPolicy()),
        "pause_music" to Pair(ToolPermission.LOW_RISK_WRITE, ActionPolicy()),
        "resume_music" to Pair(ToolPermission.LOW_RISK_WRITE, ActionPolicy()),
        "skip_music" to Pair(ToolPermission.LOW_RISK_WRITE, ActionPolicy()),
        "previous_music" to Pair(ToolPermission.LOW_RISK_WRITE, ActionPolicy()),
        "create_playlist" to Pair(ToolPermission.LOW_RISK_WRITE, ActionPolicy()),
        "toggle_flow" to Pair(ToolPermission.LOW_RISK_WRITE, ActionPolicy()),
        "set_sleep_timer" to Pair(ToolPermission.LOW_RISK_WRITE, ActionPolicy()),
        "set_equalizer_preset" to Pair(ToolPermission.LOW_RISK_WRITE, ActionPolicy()),
        "change_theme" to Pair(ToolPermission.LOW_RISK_WRITE, ActionPolicy()),
        "change_logo" to Pair(ToolPermission.LOW_RISK_WRITE, ActionPolicy()),
        "change_font" to Pair(ToolPermission.LOW_RISK_WRITE, ActionPolicy()),
        "change_player_style" to Pair(ToolPermission.LOW_RISK_WRITE, ActionPolicy()),
        "change_navigation_style" to Pair(ToolPermission.LOW_RISK_WRITE, ActionPolicy()),
        "change_mini_player_style" to Pair(ToolPermission.LOW_RISK_WRITE, ActionPolicy()),
        "change_appearance_mode" to Pair(ToolPermission.LOW_RISK_WRITE, ActionPolicy()),

        // Read-only tools
        "search_songs" to Pair(ToolPermission.READ, ActionPolicy(isReadOnly = true)),
        "get_user_history" to Pair(ToolPermission.READ, ActionPolicy(isReadOnly = true)),
        "get_current_track" to Pair(ToolPermission.READ, ActionPolicy(isReadOnly = true)),
        "explain_song" to Pair(ToolPermission.READ, ActionPolicy(isReadOnly = true)),
        "get_vibe_check" to Pair(ToolPermission.READ, ActionPolicy(isReadOnly = true)),
        "get_song_trivia" to Pair(ToolPermission.READ, ActionPolicy(isReadOnly = true)),
        "get_artist_journey" to Pair(ToolPermission.READ, ActionPolicy(isReadOnly = true)),
        "blend_genres" to Pair(ToolPermission.READ, ActionPolicy(isReadOnly = true)),
        "decade_time_machine" to Pair(ToolPermission.READ, ActionPolicy(isReadOnly = true)),
        "ai_doctor_playlist" to Pair(ToolPermission.READ, ActionPolicy(isReadOnly = true)),
        "translate_lyrics" to Pair(ToolPermission.READ, ActionPolicy(isReadOnly = true)),
        "generate_singalong_guide" to Pair(ToolPermission.READ, ActionPolicy(isReadOnly = true)),
        "get_similarity_score" to Pair(ToolPermission.READ, ActionPolicy(isReadOnly = true)),
        "get_couple_compatibility" to Pair(ToolPermission.READ, ActionPolicy(isReadOnly = true)),
        "get_friends" to Pair(ToolPermission.READ, ActionPolicy(isReadOnly = true)),
        "get_room_status" to Pair(ToolPermission.READ, ActionPolicy(isReadOnly = true)),
        "search_library" to Pair(ToolPermission.READ, ActionPolicy(isReadOnly = true))
    )

    private val maliciousPatterns = listOf(
        "(?i)ignore\\s+previous\\s+instructions",
        "(?i)system\\s+override",
        "(?i)delete\\s+database",
        "(?i)drop\\s+table",
        "(?i)reveal\\s+secret",
        "(?i)admin\\s+password",
        "(?i)<script[^>]*>",
        "(?i)javascript:",
        "(?i)rm\\s+-rf",
        "(?i)/etc/passwd",
        "(?i)/bin/sh"
    )

    fun isToolAuthorized(functionName: String): Boolean {
        return toolPolicies.containsKey(functionName)
    }

    fun validateToolCall(toolCall: ToolCall): ToolValidationResult {
        val functionName = toolCall.functionName.trim()
        if (functionName.isBlank()) {
            return ToolValidationResult(isValid = false, reason = "Function name cannot be blank")
        }

        val policyEntry = toolPolicies[functionName]
            ?: return ToolValidationResult(
                isValid = false,
                reason = "Action '$functionName' is not authorized by security policy"
            )
        val (permission, policy) = policyEntry

        // 1. Argument validation: key length, value length, and injection checks
        for ((key, value) in toolCall.arguments) {
            if (key.length > 64) {
                return ToolValidationResult(
                    isValid = false,
                    reason = "Argument key '$key' exceeds maximum length of 64 characters"
                )
            }
            if (value.length > 300) {
                return ToolValidationResult(
                    isValid = false,
                    reason = "Argument '$key' exceeds maximum length of 300 characters"
                )
            }
            for (pattern in maliciousPatterns) {
                if (Regex(pattern).containsMatchIn(value)) {
                    return ToolValidationResult(
                        isValid = false,
                        reason = "Argument '$key' contains forbidden injection pattern"
                    )
                }
            }
        }

        // 2. Specific parameter validations
        when (functionName) {
            "change_appearance_mode" -> {
                val mode = toolCall.arguments["mode"]
                if (mode != null && !setOf("LIGHT", "DARK", "SYSTEM").contains(mode.uppercase())) {
                    return ToolValidationResult(
                        isValid = false,
                        reason = "Invalid appearance mode '$mode'. Expected LIGHT, DARK, or SYSTEM"
                    )
                }
            }
            "create_room" -> {
                val type = toolCall.arguments["type"]
                if (type != null && !setOf("PUBLIC", "PRIVATE").contains(type.uppercase())) {
                    return ToolValidationResult(
                        isValid = false,
                        reason = "Invalid room type '$type'. Expected PUBLIC or PRIVATE"
                    )
                }
            }
            "toggle_flow", "toggle_incognito" -> {
                val enable = toolCall.arguments["enable"]
                if (enable != null && !setOf("true", "false").contains(enable.lowercase())) {
                    return ToolValidationResult(
                        isValid = false,
                        reason = "Invalid boolean parameter '$enable' for $functionName. Expected true or false"
                    )
                }
            }
            "change_theme", "change_logo", "change_font",
            "change_player_style", "change_navigation_style", "change_mini_player_style" -> {
                val param = when (functionName) {
                    "change_theme" -> toolCall.arguments["theme_id"]
                    "change_logo" -> toolCall.arguments["logo_variant"]
                    "change_font" -> toolCall.arguments["font"]
                    else -> toolCall.arguments["style"]
                }
                if (param != null && !param.matches(Regex("^[a-zA-Z0-9_-]+$"))) {
                    return ToolValidationResult(
                        isValid = false,
                        reason = "Invalid identifier '$param' for $functionName. Must be alphanumeric."
                    )
                }
            }
        }

        // 3. User confirmation check for high-risk / destructive actions
        val isConfirmed = toolCall.arguments["confirmed"]?.equals("true", ignoreCase = true) == true ||
                toolCall.arguments["confirm"]?.equals("true", ignoreCase = true) == true

        if (!toolGuard.validateAction(permission, policy, isConfirmed)) {
            return ToolValidationResult(
                isValid = false,
                reason = "Action '$functionName' requires explicit user confirmation. Re-invoke with confirmed=true.",
                requiresConfirmation = true
            )
        }

        return ToolValidationResult(isValid = true)
    }

    fun sanitizePrompt(prompt: String): String {
        var clean = prompt
        maliciousPatterns.forEach { pattern ->
            clean = clean.replace(Regex(pattern), "[REDACTED]")
        }
        return clean
    }

    fun sanitizeOutput(output: String): String {
        // Redact tokens, passwords, API keys if present
        return output
            .replace(Regex("(?i)bearer\\s+[a-zA-Z0-9._-]+"), "Bearer [REDACTED]")
            .replace(Regex("(?i)key=[a-zA-Z0-9._-]+"), "key=[REDACTED]")
    }
}
