package com.music.echo.notune.intelligence.explanation

import com.music.echo.notune.intelligence.recommendation.RankedTrack
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecommendationExplanationEngine @Inject constructor() {
    fun generateExplanation(rankedTrack: RankedTrack): String {
        val track = rankedTrack.track
        val artist = track.canonicalArtists.firstOrNull() ?: "Artist"

        return when {
            rankedTrack.score >= 0.85f -> "Because you replay $artist often and matches your ${track.primaryLanguage} taste."
            rankedTrack.tasteFit >= 0.75f -> "Fits your top ${track.primaryGenre} & ${track.primaryLanguage} taste profile."
            rankedTrack.sessionFit >= 0.70f -> "Similar energy & mood to your current listening session."
            else -> "New ${track.primaryLanguage} discovery related to artists you listen to."
        }
    }
}
