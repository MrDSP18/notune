package com.music.echo.notune.intelligence.explanation

import com.music.echo.notune.intelligence.musicbrain.TrackScoreResult
import com.music.echo.notune.intelligence.personalization.NotuneUserDNA
import javax.inject.Inject
import javax.inject.Singleton

data class TrackExplanationCard(
    val matchPercentage: Int,
    val headlineReason: String,
    val bulletPoints: List<String>,
    val explicitRuleMatches: List<String> = emptyList()
)

@Singleton
class RecommendationExplanation @Inject constructor() {

    fun explainTrack(
        trackTitle: String,
        artistName: String,
        scoreResult: TrackScoreResult,
        userDna: NotuneUserDNA
    ): TrackExplanationCard {
        val pct = (scoreResult.totalScore * 100).toInt().coerceIn(1, 99)

        val bullets = mutableListOf<String>()

        if (scoreResult.artistAffinity >= 0.6f) {
            bullets.add("You frequently listen to $artistName")
        }

        if (scoreResult.languageMatch >= 0.8f) {
            bullets.add("Matches your preferred language during this session")
        }

        if (scoreResult.moodMatch >= 0.7f) {
            bullets.add("Fits your current ${userDna.sessionTaste.currentMood} mood")
        }

        if (scoreResult.freshness >= 0.7f && scoreResult.discoveryValue > 0.1f) {
            bullets.add("New discovery based on your high exploration tolerance")
        } else if (scoreResult.userTaste >= 0.7f) {
            bullets.add("Matches your core genre taste profile")
        }

        if (bullets.isEmpty()) {
            bullets.add("Recommended based on your recent listening flow")
        }

        val headline = if (scoreResult.artistAffinity >= 0.7f) {
            "Because you like $artistName"
        } else if (scoreResult.moodMatch >= 0.7f) {
            "Fits your ${userDna.sessionTaste.currentMood} session vibe"
        } else {
            "Based on your NØTUNE Music DNA"
        }

        val activeRuleMatches = userDna.activeRules.map { "Active rule: '${it.condition}'" }

        return TrackExplanationCard(
            matchPercentage = pct,
            headlineReason = headline,
            bulletPoints = bullets,
            explicitRuleMatches = activeRuleMatches
        )
    }
}
