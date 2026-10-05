package com.music.echo.notune.intelligence

import com.music.echo.notune.intelligence.context.UnifiedContextEngine
import com.music.echo.notune.intelligence.feedback.FeedbackEngine
import com.music.echo.notune.intelligence.feedback.UserEvent
import com.music.echo.notune.intelligence.intent.IntentEngine
import com.music.echo.notune.intelligence.intent.StructuredUserIntent
import com.music.echo.notune.intelligence.queue.AdaptiveQueueOrchestrator
import com.music.echo.notune.intelligence.queue.NaturalLanguageQueueController
import com.music.echo.notune.intelligence.recommendation.CandidateEngine
import com.music.echo.notune.intelligence.recommendation.CentralizedRankingEngine
import com.music.echo.notune.intelligence.recommendation.DiversityController
import com.music.echo.notune.intelligence.recommendation.RankedTrackResult
import com.music.echo.notune.intelligence.tools.AiToolExecutor
import echo.music.iad1tya.notune.provider.UnifiedTrack
import javax.inject.Inject
import javax.inject.Singleton

data class IntelligenceProcessResult(
    val intent: StructuredUserIntent,
    val rankedTracks: List<RankedTrackResult>,
    val queueStatusMessage: String,
    val toolExecutionReport: String? = null
)

@Singleton
class NotuneIntelligenceCoordinator @Inject constructor(
    private val intentEngine: IntentEngine,
    private val unifiedContextEngine: UnifiedContextEngine,
    private val candidateEngine: CandidateEngine,
    private val centralizedRankingEngine: CentralizedRankingEngine,
    private val diversityController: DiversityController,
    private val naturalLanguageQueueController: NaturalLanguageQueueController,
    private val adaptiveQueueOrchestrator: AdaptiveQueueOrchestrator,
    private val feedbackEngine: FeedbackEngine,
    private val aiToolExecutor: AiToolExecutor
) {

    suspend fun processUserRequest(
        userPrompt: String,
        currentTrack: UnifiedTrack? = null,
        isPlaying: Boolean = false
    ): IntelligenceProcessResult {
        // 1. Intent Engine
        val intent = intentEngine.parseIntent(userPrompt)

        // 2. Context Snapshot
        val contextSnapshot = unifiedContextEngine.captureSnapshot(isPlaying = isPlaying)

        // 3. Candidate Generation
        val candidateTracks = candidateEngine.generateCandidatePool(
            seedQuery = intent.rawQuery,
            targetArtist = intent.targetArtist,
            targetGenre = intent.targetGenre,
            targetLanguage = intent.targetLanguage,
            excludeArtists = intent.constraints.excludeArtists
        )

        // 4. Centralized Ranking Engine
        val rankedCandidates = centralizedRankingEngine.rankCandidates(candidateTracks, intent, contextSnapshot)

        // 5. Controlled Diversity Exploration
        val finalRanked = diversityController.applyDiversityFilter(
            rankedCandidates,
            discoveryTolerance = contextSnapshot.session.discoveryTolerance
        )

        // 6. Queue Planning & Execution
        val statusMessage = naturalLanguageQueueController.executeQueueDirective(intent, finalRanked)

        // 7. Telemetry & Feedback
        feedbackEngine.processUserEvent(UserEvent.Searched(userPrompt))

        return IntelligenceProcessResult(
            intent = intent,
            rankedTracks = finalRanked,
            queueStatusMessage = statusMessage
        )
    }

    fun recordPlaybackFeedback(event: UserEvent): Float {
        return feedbackEngine.processUserEvent(event)
    }
}
