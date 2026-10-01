package com.music.echo.notune.intelligence.feedback

import com.music.echo.notune.intelligence.musicbrain.MusicIdentity

enum class SkipReason {
    ARTIST_OVEREXPOSURE,
    LANGUAGE_MISMATCH,
    GENRE_MISMATCH,
    ENERGY_TOO_HIGH,
    ENERGY_TOO_LOW,
    MOOD_TRANSITION,
    SONG_ALREADY_HEARD,
    LOW_RELEVANCE,
    EARLY_DISLIKE,
    CONTEXT_MISMATCH,
    UNKNOWN
}

data class SkipIntelligenceResult(
    val reason: SkipReason,
    val artistPenaltyDelta: Float = -0.05f,
    val languagePenaltyDelta: Float = -0.02f,
    val genrePenaltyDelta: Float = -0.03f,
    val explanation: String
)

object SkipIntelligence {
    fun analyzeSkip(
        playbackPositionSeconds: Int,
        trackDurationSeconds: Int,
        track: MusicIdentity,
        recentArtistCount: Int = 0,
        currentLanguage: String = ""
    ): SkipIntelligenceResult {
        val skipRatio = if (trackDurationSeconds > 0) playbackPositionSeconds.toFloat() / trackDurationSeconds.toFloat() else 0f

        return when {
            // Early skip < 5s indicates strong immediate mismatch
            playbackPositionSeconds < 5 -> {
                if (currentLanguage.isNotEmpty() && !track.primaryLanguage.equals(currentLanguage, ignoreCase = true)) {
                    SkipIntelligenceResult(
                        reason = SkipReason.LANGUAGE_MISMATCH,
                        languagePenaltyDelta = -0.08f,
                        explanation = "Early skip due to language shift (${track.primaryLanguage})"
                    )
                } else {
                    SkipIntelligenceResult(
                        reason = SkipReason.EARLY_DISLIKE,
                        artistPenaltyDelta = -0.08f,
                        explanation = "Immediate skip (< 5s)"
                    )
                }
            }
            // Skip after playing same artist repeatedly
            recentArtistCount >= 3 -> {
                SkipIntelligenceResult(
                    reason = SkipReason.ARTIST_OVEREXPOSURE,
                    artistPenaltyDelta = -0.10f,
                    explanation = "Artist overexposure penalty for ${track.canonicalArtists.firstOrNull() ?: "Artist"}"
                )
            }
            // Late skip (> 70% completed) usually means track finished, not strong dislike
            skipRatio > 0.70f -> {
                SkipIntelligenceResult(
                    reason = SkipReason.SONG_ALREADY_HEARD,
                    artistPenaltyDelta = 0.0f,
                    explanation = "Song listened to near completion (> 70%)"
                )
            }
            else -> {
                SkipIntelligenceResult(
                    reason = SkipReason.LOW_RELEVANCE,
                    artistPenaltyDelta = -0.03f,
                    explanation = "Mid-track skip"
                )
            }
        }
    }
}
