package com.music.echo.notune.intelligence.recommendation

import com.music.echo.notune.intelligence.musicbrain.TrackEmbedding

enum class CandidateType {
    EXPLOITATION_FAVORITE, // 70% high confidence favorites
    SIMILAR_DISCOVERY,     // 20% similar artists/genres user hasn't heard
    EXPERIMENTAL_DISCOVERY // 10% wildcard discovery
}

data class RecommendationCandidate(
    val id: String,
    val title: String,
    val artistName: String,
    val embedding: TrackEmbedding,
    val candidateType: CandidateType = CandidateType.EXPLOITATION_FAVORITE
)

data class ExplorationRatio(
    val exploitationRate: Float = 0.70f,
    val similarDiscoveryRate: Float = 0.20f,
    val experimentalDiscoveryRate: Float = 0.10f
)

data class RecommendationBatch(
    val tracks: List<RecommendationCandidate>,
    val ratioUsed: ExplorationRatio,
    val batchExplanation: String
)
