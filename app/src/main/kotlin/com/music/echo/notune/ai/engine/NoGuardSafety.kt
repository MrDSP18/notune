package echo.music.iad1tya.notune.ai.engine

import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

@Singleton
class NoGuardSafety @Inject constructor() {

    private val allowedTools = setOf(
        "search_songs", "play_song", "get_user_history", "create_playlist",
        "pause_music", "resume_music", "skip_music", "previous_music",
        "get_current_track", "explain_song", "change_theme", "change_logo",
        "toggle_flow", "toggle_incognito", "set_sleep_timer", "get_vibe_check",
        "get_song_trivia", "get_artist_journey", "blend_genres", "decade_time_machine",
        "ai_doctor_playlist", "set_equalizer_preset", "translate_lyrics",
        "generate_singalong_guide", "get_similarity_score", "get_couple_compatibility",
        "get_friends", "create_room", "join_room", "leave_room", "get_room_status",
        "search_library", "invite_friend", "change_font", "change_player_style",
        "change_navigation_style", "change_mini_player_style", "change_appearance_mode"
    )

    private val injectionPatterns = listOf(
        "(?i)ignore previous instructions",
        "(?i)system override",
        "(?i)delete database",
        "(?i)drop table",
        "(?i)reveal secret",
        "(?i)admin password"
    )

    fun isToolAuthorized(functionName: String): Boolean {
        return allowedTools.contains(functionName)
    }

    fun sanitizePrompt(prompt: String): String {
        var clean = prompt
        injectionPatterns.forEach { pattern ->
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
