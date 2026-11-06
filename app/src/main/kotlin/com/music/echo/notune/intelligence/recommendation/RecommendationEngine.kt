package com.music.echo.notune.intelligence.recommendation

import com.music.echo.notune.intelligence.musicbrain.MusicBrain
import com.music.echo.notune.intelligence.personalization.TasteProfileStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecommendationEngine @Inject constructor(
    private val explorationEngine: ExplorationEngine,
    private val musicBrain: MusicBrain,
    private val tasteProfileStore: TasteProfileStore
) {

    fun generateRecommendations(
        pool: List<RecommendationCandidate>,
        targetCount: Int = 20,
        explicitNoveltyRequested: Boolean = false
    ): RecommendationBatch {
        val dna = tasteProfileStore.getDnaSnapshot()
        val ratio = explorationEngine.getDynamicRatio(dna, explicitNoveltyRequested)

        val exploitationPool = pool.filter { it.candidateType == CandidateType.EXPLOITATION_FAVORITE }
        val similarPool = pool.filter { it.candidateType == CandidateType.SIMILAR_DISCOVERY }
        val experimentalPool = pool.filter { it.candidateType == CandidateType.EXPERIMENTAL_DISCOVERY }

        val countExploit = (targetCount * ratio.exploitationRate).toInt().coerceAtLeast(1)
        val countSimilar = (targetCount * ratio.similarDiscoveryRate).toInt()
        val countExperimental = targetCount - countExploit - countSimilar

        val scoredExploit = scoreAndTake(exploitationPool, countExploit)
        val scoredSimilar = scoreAndTake(similarPool, countSimilar)
        val scoredExperimental = scoreAndTake(experimentalPool, countExperimental)

        val combined = (scoredExploit + scoredSimilar + scoredExperimental)
            .distinctBy { it.id }
            .take(targetCount)

        val explanation = "Personalized Recommendation Batch: ${(ratio.exploitationRate * 100).toInt()}% Favorites / ${(ratio.similarDiscoveryRate * 100).toInt()}% Similar Discovery / ${(ratio.experimentalDiscoveryRate * 100).toInt()}% Wildcard"

        return RecommendationBatch(
            tracks = combined,
            ratioUsed = ratio,
            batchExplanation = explanation
        )
    }

    private fun scoreAndTake(candidates: List<RecommendationCandidate>, count: Int): List<RecommendationCandidate> {
        val dna = tasteProfileStore.getDnaSnapshot()
        return candidates
            .map { candidate ->
                val scoreResult = musicBrain.scoreTrack(candidate.embedding, dna)
                candidate to scoreResult.totalScore
            }
            .sortedByDescending { it.second }
            .take(count)
            .map { it.first }
    }
}
