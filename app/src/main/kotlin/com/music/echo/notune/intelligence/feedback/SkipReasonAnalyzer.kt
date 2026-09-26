package com.music.echo.notune.intelligence.feedback

import com.music.echo.notune.intelligence.musicbrain.TrackEmbedding
import com.music.echo.notune.intelligence.personalization.NotuneUserDNA
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

enum class InferredSkipReason {
    ENERGY_MISMATCH,
    ARTIST_OVEREXPOSURE,
    GENRE_MISMATCH,
    MOOD_JUMP,
    LANGUAGE_MISMATCH,
    EARLY_DISLIKE,
    UNKNOWN
}

data class SkipAnalysisResult(
    val trackId: String,
    val skipReason: InferredSkipReason,
    val explanation: String,
    val confidence: Float
)

@Singleton
class SkipReasonAnalyzer @Inject constructor() {

    fun analyzeSkip(
        skippedTrack: TrackEmbedding,
        playedDurationSec: Float,
        userDna: NotuneUserDNA,
        recentPlayedArtists: List<String> = emptyList()
    ): SkipAnalysisResult {
        val session = userDna.sessionTaste

        // 1. Artist overexposure check
        if (recentPlayedArtists.takeLast(3).count { it.equals(skippedTrack.artistName, ignoreCase = true) } >= 2) {
            return SkipAnalysisResult(
                trackId = skippedTrack.trackId,
                skipReason = InferredSkipReason.ARTIST_OVEREXPOSURE,
                explanation = "Artist ${skippedTrack.artistName} was played repeatedly in short succession",
                confidence = 0.88f
            )
        }

        // 2. Energy mismatch check
        val energyDiff = abs(session.currentEnergy - skippedTrack.energy)
        if (energyDiff >= 0.40f) {
            val direction = if (skippedTrack.energy > session.currentEnergy) "too high energy" else "too low energy"
            return SkipAnalysisResult(
                trackId = skippedTrack.trackId,
                skipReason = InferredSkipReason.ENERGY_MISMATCH,
                explanation = "Track energy ($direction) differed from current session energy target",
                confidence = 0.82f
            )
        }

        // 3. Language mismatch check
        if (session.currentLanguage != null && !skippedTrack.language.equals(session.currentLanguage, ignoreCase = true)) {
            return SkipAnalysisResult(
                trackId = skippedTrack.trackId,
                skipReason = InferredSkipReason.LANGUAGE_MISMATCH,
                explanation = "Language ${skippedTrack.language} differed from active session language ${session.currentLanguage}",
                confidence = 0.78f
            )
        }

        // 4. Early skip check (<8 seconds)
        if (playedDurationSec < 8.0f) {
            return SkipAnalysisResult(
                trackId = skippedTrack.trackId,
                skipReason = InferredSkipReason.EARLY_DISLIKE,
                explanation = "Track skipped immediately within first 8 seconds",
                confidence = 0.90f
            )
        }

        return SkipAnalysisResult(
            trackId = skippedTrack.trackId,
            skipReason = InferredSkipReason.UNKNOWN,
            explanation = "General skip signal",
            confidence = 0.50f
        )
    }
}
