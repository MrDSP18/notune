package echo.music.iad1tya.notune.ai.adaptive

import java.util.UUID

/**
 * Types of user interaction signals captured for adaptive queue learning.
 */
enum class FeedbackEventType {
    PLAY,
    COMPLETE,
    SKIP,
    REPLAY,
    LIKE,
    DISLIKE,
    ADD_TO_PLAYLIST
}

/**
 * Structured behavioral signal record for learning user preferences during session.
 */
data class FeedbackEvent(
    val eventId: String = UUID.randomUUID().toString(),
    val eventType: FeedbackEventType,
    val trackId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val sessionId: String = "",
    val positionMs: Long = 0L,
    val completionPercent: Float = 0.0f,
    val context: String? = null
)
