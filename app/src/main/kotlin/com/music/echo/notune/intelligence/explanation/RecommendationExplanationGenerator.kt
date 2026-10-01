package com.music.echo.notune.intelligence.explanation

import com.music.echo.notune.intelligence.recommendation.RankedTrackResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecommendationExplanationGenerator @Inject constructor() {

    fun explainRecommendation(result: RankedTrackResult): String {
        return "NØTUNE Intelligence selection for '${result.track.title}' by ${result.track.artist}: ${result.explanation}"
    }
}
