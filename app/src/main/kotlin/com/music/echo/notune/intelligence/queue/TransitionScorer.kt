package com.music.echo.notune.intelligence.queue

import com.music.echo.notune.intelligence.musicbrain.TrackEmbedding
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

/**
 * NØTUNE Flow Engine: Transition Scorer
 *
 * Evaluates how smoothly Track A transitions into Track B based on:
 * - Energy continuity (prevents jarring 0.2 → 0.9 energy jumps)
 * - Tempo/BPM ratio compatibility
 * - Mood valence flow
 * - Genre jump penalty
 */
@Singleton
class TransitionScorer @Inject constructor() {

    fun calculateTransition(fromTrack: TrackEmbedding, toTrack: TrackEmbedding): TransitionScore {
        val energyDelta = abs(fromTrack.energy - toTrack.energy)
        val energyPenalty = (energyDelta * 0.45f).coerceIn(0f, 0.45f)

        val bpmRatio = if (fromTrack.tempoBpm > 0f) toTrack.tempoBpm / fromTrack.tempoBpm else 1.0f
        val bpmDiff = abs(1.0f - bpmRatio)
        val bpmPenalty = (bpmDiff * 0.35f).coerceIn(0f, 0.35f)

        val moodDelta = abs(fromTrack.moodValence - toTrack.moodValence)
        val moodPenalty = (moodDelta * 0.20f).coerceIn(0f, 0.20f)

        val genreJumpPenalty = if (!fromTrack.genre.equals(toTrack.genre, ignoreCase = true) &&
            !fromTrack.language.equals(toTrack.language, ignoreCase = true)
        ) 0.15f else 0.0f

        val quality = (1.0f - energyPenalty - bpmPenalty - moodPenalty - genreJumpPenalty).coerceIn(0f, 1f)

        val flowLabel = when {
            quality >= 0.85f -> "Seamless Harmonic Flow"
            quality >= 0.70f -> "Smooth Transition"
            quality >= 0.50f -> "Acceptable Shift"
            else -> "Jarring Transition Risk"
        }

        return TransitionScore(
            fromTrackTitle = fromTrack.title,
            toTrackTitle = toTrack.title,
            transitionQuality = quality,
            energyDelta = energyDelta,
            bpmRatio = bpmRatio,
            flowLabel = flowLabel
        )
    }
}
