package com.music.echo.notune.intelligence.queue

import com.music.echo.notune.intelligence.musicbrain.TrackEmbedding
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

enum class TransitionType {
    SMOOTH,
    EVOLVING,
    DISCOVERY,
    MOOD_SHIFT
}

data class TransitionResult(
    val transitionType: TransitionType = TransitionType.SMOOTH,
    val distance: Float = 0.2f,
    val transitionQuality: Float = 0.85f,
    val flowLabel: String = "Seamless Harmonic Flow",
    val isSmoothEnergy: Boolean = true
)

@Singleton
class TransitionEngine @Inject constructor(
    private val transitionScorer: TransitionScorer
) {

    fun calculateDistance(a: TrackEmbedding, b: TrackEmbedding): Float {
        val energyDist = abs(a.energy - b.energy)
        val tempoDist = if (a.tempoBpm > 0f) abs(1.0f - b.tempoBpm / a.tempoBpm) else 0f
        val valenceDist = abs(a.moodValence - b.moodValence)
        val genreSame = a.genre.equals(b.genre, ignoreCase = true)
        val languageSame = a.language.equals(b.language, ignoreCase = true)
        val artistSame = a.artistName.equals(b.artistName, ignoreCase = true)

        val metadataPenalty = (if (artistSame) 0.0f else 0.1f) + (if (genreSame) 0.0f else 0.15f) + (if (languageSame) 0.0f else 0.15f)
        val distance = (energyDist * 0.35f + tempoDist * 0.25f + valenceDist * 0.15f + metadataPenalty * 0.25f).coerceIn(0f, 1f)
        return distance
    }

    fun evaluateTransition(
        fromTrack: TrackEmbedding,
        toTrack: TrackEmbedding,
        mode: NotuneFlowMode = NotuneFlowMode.SMART
    ): TransitionResult {
        val distance = calculateDistance(fromTrack, toTrack)
        val scorerResult = transitionScorer.calculateTransition(fromTrack, toTrack)

        val type = when {
            distance < 0.25f -> TransitionType.SMOOTH
            distance < 0.50f -> TransitionType.EVOLVING
            distance < 0.75f -> TransitionType.DISCOVERY
            else -> TransitionType.MOOD_SHIFT
        }

        val isSmoothEnergy = scorerResult.energyDelta < 0.35f

        return TransitionResult(
            transitionType = type,
            distance = distance,
            transitionQuality = scorerResult.transitionQuality,
            flowLabel = scorerResult.flowLabel,
            isSmoothEnergy = isSmoothEnergy
        )
    }
}
