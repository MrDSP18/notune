package com.music.echo.notune.audio

import javax.inject.Inject
import javax.inject.Singleton

data class TransitionCompatibility(
    val score: Int, // 0 to 100
    val recommendedCrossfadeMs: Int,
    val isBpmMatched: Boolean
)

@Singleton
class SmartTransitionEngine @Inject constructor() {

    fun calculateTransition(
        currentTrackArtist: String?,
        nextTrackArtist: String?
    ): TransitionCompatibility {
        val sameArtist = currentTrackArtist != null && currentTrackArtist.equals(nextTrackArtist, ignoreCase = true)
        val score = if (sameArtist) 95 else 82
        val crossfade = if (sameArtist) 4000 else 2500

        return TransitionCompatibility(
            score = score,
            recommendedCrossfadeMs = crossfade,
            isBpmMatched = true
        )
    }
}
