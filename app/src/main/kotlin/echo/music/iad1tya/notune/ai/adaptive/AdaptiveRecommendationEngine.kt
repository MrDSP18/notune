package echo.music.iad1tya.notune.ai.adaptive

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Main NØ AI unified recommendation engine service.
 */
@Singleton
class AdaptiveRecommendationEngine @Inject constructor(
    val scoringEngine: AdaptiveScoringEngine = AdaptiveScoringEngine(),
    val queueManager: AdaptiveQueueManager = AdaptiveQueueManager(scoringEngine)
) {

    /**
     * Generate structured recommendations for a recommendation request.
     */
    fun generateRecommendations(
        request: RecommendationRequest,
        candidatePool: List<AdaptiveTrackContext>,
        modelSource: ModelSource = ModelSource.ANDROID_LITERT
    ): RecommendationResponse {
        val now = System.currentTimeMillis()

        // Fallback for empty candidate pool
        if (candidatePool.isEmpty()) {
            return RecommendationResponse(
                recommendedTracks = emptyList(),
                scores = emptyMap(),
                reasons = emptyMap(),
                confidence = 0.0f,
                model = ModelSource.FALLBACK,
                generatedAt = now
            )
        }

        val scoredTracks = candidatePool.map { candidate ->
            scoringEngine.scoreCandidate(candidate, request)
        }.sortedByDescending { it.score }

        val scoresMap = scoredTracks.associate { it.track.trackId to it.score }
        val reasonsMap = scoredTracks.associate { it.track.trackId to it.reason }
        val avgConfidence = if (scoredTracks.isNotEmpty()) scoredTracks.map { it.score }.average().toFloat() else 0.5f

        return RecommendationResponse(
            recommendedTracks = scoredTracks,
            scores = scoresMap,
            reasons = reasonsMap,
            confidence = avgConfidence,
            model = modelSource,
            generatedAt = now
        )
    }

    /**
     * Adapt active session queue when user feedback events occur.
     */
    fun adaptActiveQueue(
        currentTrack: AdaptiveTrackContext?,
        remainingQueue: List<AdaptiveTrackContext>,
        candidatePool: List<AdaptiveTrackContext>,
        history: List<AdaptiveTrackContext>,
        feedbackEvent: FeedbackEvent? = null
    ): List<AdaptiveTrackContext> {
        if (feedbackEvent != null) {
            queueManager.recordFeedback(feedbackEvent)
        }

        return queueManager.adaptQueue(
            currentTrack = currentTrack,
            remainingQueue = remainingQueue,
            candidatePool = candidatePool,
            playedHistory = history
        )
    }
}
