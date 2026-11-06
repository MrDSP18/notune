package com.music.echo.notune.intelligence.recommendation

import kotlin.math.log2

data class EvaluationMetrics(
    val precisionAtK: Float,
    val recallAtK: Float,
    val ndcgAtK: Float,
    val artistDiversityScore: Float,
    val completionRate: Float,
    val skipRate: Float
)

object RecommendationEvaluation {
    fun calculateMetrics(
        recommendedTrackIds: List<String>,
        relevantTrackIds: Set<String>,
        k: Int = 10,
        artistsList: List<String> = emptyList(),
        completedCount: Int = 0,
        skippedCount: Int = 0
    ): EvaluationMetrics {
        val topK = recommendedTrackIds.take(k)
        if (topK.isEmpty()) {
            return EvaluationMetrics(0f, 0f, 0f, 0f, 0f, 0f)
        }

        val relevantHits = topK.count { it in relevantTrackIds }
        val precision = relevantHits.toFloat() / topK.size.toFloat()
        val recall = if (relevantTrackIds.isNotEmpty()) relevantHits.toFloat() / relevantTrackIds.size.toFloat() else 0f

        // DCG@K calculation
        var dcg = 0.0f
        topK.forEachIndexed { index, trackId ->
            if (trackId in relevantTrackIds) {
                dcg += 1.0f / log2((index + 2).toFloat())
            }
        }

        // Ideal DCG@K calculation
        var idcg = 0.0f
        val idealHits = minOf(k, relevantTrackIds.size)
        for (i in 0 until idealHits) {
            idcg += 1.0f / log2((i + 2).toFloat())
        }

        val ndcg = if (idcg > 0f) dcg / idcg else 0f

        // Diversity Score (unique artists ratio)
        val uniqueArtists = artistsList.take(k).toSet().size
        val diversity = if (topK.isNotEmpty()) uniqueArtists.toFloat() / topK.size.toFloat() else 0f

        val totalSessionInteractions = completedCount + skippedCount
        val compRate = if (totalSessionInteractions > 0) completedCount.toFloat() / totalSessionInteractions.toFloat() else 0f
        val skpRate = if (totalSessionInteractions > 0) skippedCount.toFloat() / totalSessionInteractions.toFloat() else 0f

        return EvaluationMetrics(
            precisionAtK = precision,
            recallAtK = recall,
            ndcgAtK = ndcg,
            artistDiversityScore = diversity,
            completionRate = compRate,
            skipRate = skpRate
        )
    }
}
