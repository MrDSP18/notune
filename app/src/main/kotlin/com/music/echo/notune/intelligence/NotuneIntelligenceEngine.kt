package com.music.echo.notune.intelligence

import com.music.echo.notune.intelligence.context.ContextEngine
import com.music.echo.notune.intelligence.context.InferredSituation
import com.music.echo.notune.intelligence.explanation.RecommendationExplanation
import com.music.echo.notune.intelligence.explanation.TasteExplanation
import com.music.echo.notune.intelligence.explanation.TrackExplanationCard
import com.music.echo.notune.intelligence.feedback.FeedbackProcessor
import com.music.echo.notune.intelligence.feedback.SkipReasonAnalyzer
import com.music.echo.notune.intelligence.feedback.UserEvent
import com.music.echo.notune.intelligence.musicbrain.InferredMusicState
import com.music.echo.notune.intelligence.musicbrain.MusicBrain
import com.music.echo.notune.intelligence.musicbrain.MusicStateEngine
import com.music.echo.notune.intelligence.personalization.InstantOverrideAction
import com.music.echo.notune.intelligence.personalization.NotuneUserDNA
import com.music.echo.notune.intelligence.personalization.PreferenceRule
import com.music.echo.notune.intelligence.personalization.SessionOverrideEngine
import com.music.echo.notune.intelligence.personalization.TasteProfileStore
import com.music.echo.notune.intelligence.personalization.TeachNotuneEngine
import com.music.echo.notune.intelligence.queue.AdaptiveQueueEngine
import com.music.echo.notune.intelligence.queue.NotuneFlowMode
import com.music.echo.notune.intelligence.queue.QueueTrack
import com.music.echo.notune.intelligence.recommendation.RecommendationBatch
import com.music.echo.notune.intelligence.recommendation.RecommendationCandidate
import com.music.echo.notune.intelligence.recommendation.RecommendationEngine
import com.music.echo.notune.intelligence.search.PersonalizedSearchEngine
import com.music.echo.notune.intelligence.search.QualityGateDecision
import com.music.echo.notune.intelligence.search.RankedSearchResult
import com.music.echo.notune.intelligence.search.SearchCandidate
import com.music.echo.notune.intelligence.search.SearchQualityGate
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * NØTUNE Personal Music Intelligence Engine v1
 *
 * Unified Coordinator:
 * 1. Personal User DNA (3-Layer Taste Architecture)
 * 2. Personalized Search Engine & Quality Gate
 * 3. Context Engine & Non-Judgmental Music State Engine
 * 4. Music Brain & Feature Vector Engines
 * 5. Adaptive Queue Engine & Flow Selector
 * 6. Continuous Feedback Learning & Skip Analyzer
 * 7. Teach NØTUNE & Instant Session Overrides
 * 8. Recommendation Explanation & Control Center
 */
@Singleton
class NotuneIntelligenceEngine @Inject constructor(
    val tasteProfileStore: TasteProfileStore,
    val musicDnaInitializer: com.music.echo.notune.intelligence.personalization.MusicDnaInitializer,
    val searchEngine: PersonalizedSearchEngine,
    val searchQualityGate: SearchQualityGate,
    val contextEngine: ContextEngine,
    val musicStateEngine: MusicStateEngine,
    val musicBrain: MusicBrain,
    val adaptiveQueueEngine: AdaptiveQueueEngine,
    val feedbackProcessor: FeedbackProcessor,
    val skipReasonAnalyzer: SkipReasonAnalyzer,
    val sessionOverrideEngine: SessionOverrideEngine,
    val teachNotuneEngine: TeachNotuneEngine,
    val recommendationEngine: RecommendationEngine,
    val recommendationExplanation: RecommendationExplanation,
    val tasteExplanation: TasteExplanation
) {

    val userDnaFlow: StateFlow<NotuneUserDNA> = tasteProfileStore.userDna

    fun getCurrentDna(): NotuneUserDNA = tasteProfileStore.getDnaSnapshot()

    /**
     * Initializes User DNA from existing database listening history on app startup.
     */
    suspend fun initializeFromHistory() {
        musicDnaInitializer.initializeDnaFromExistingHistory()
    }

    /**
     * Evaluates a search candidate against the Quality Gate.
     */
    fun evaluateQualityGate(candidate: SearchCandidate): QualityGateDecision {
        return searchQualityGate.evaluateCandidate(candidate, tasteProfileStore.getDnaSnapshot())
    }

    /**
     * Executes personalized natural language search.
     */
    fun search(rawQuery: String, candidates: List<SearchCandidate>): List<RankedSearchResult> {
        val passedCandidates = candidates.filter { evaluateQualityGate(it).isPassed }
        return searchEngine.rankCandidates(rawQuery, passedCandidates)
    }

    /**
     * Generates personalized recommendations with dynamic exploration/exploitation ratios.
     */
    fun getRecommendations(
        pool: List<RecommendationCandidate>,
        count: Int = 20,
        explicitNoveltyRequested: Boolean = false
    ): RecommendationBatch {
        return recommendationEngine.generateRecommendations(pool, count, explicitNoveltyRequested)
    }

    /**
     * Evaluates current non-invasive listening context & situation.
     */
    fun inferCurrentContext(userExplicitActivity: String? = null): InferredSituation {
        return contextEngine.inferSituation(userExplicitActivity = userExplicitActivity)
    }

    /**
     * Gets non-judgmental Inferred Music State.
     */
    fun getInferredMusicState(): InferredMusicState {
        val dna = getCurrentDna()
        return musicStateEngine.inferMusicStateFromRecent(
            recentAvgEnergy = dna.sessionTaste.currentEnergy,
            recentAvgValence = 0.55f
        )
    }

    /**
     * Updates adaptive queue with new playing and upcoming tracks.
     */
    fun updateQueue(current: QueueTrack?, upcoming: List<QueueTrack>) {
        adaptiveQueueEngine.setQueue(current, upcoming)
    }

    /**
     * Sets NØTUNE Flow selector mode (Familiar, Balanced, Smart, Discovery, Deep Discovery).
     */
    fun setFlowMode(mode: NotuneFlowMode) {
        adaptiveQueueEngine.setFlowMode(mode)
    }

    /**
     * Ingests rich user feedback (plays, skips, replays, saves, dislikes).
     */
    fun recordFeedback(event: UserEvent): Float {
        val reward = feedbackProcessor.processEvent(event)

        // If skipped or completed, trigger adaptive queue update
        when (event) {
            is UserEvent.Completed -> {
                val track = QueueTrack(event.trackId, event.title, event.artistName,
                    com.music.echo.notune.intelligence.musicbrain.TrackEmbedding(event.trackId, event.title, event.artistName)
                )
                adaptiveQueueEngine.onPlaybackStateChanged(track, isSkippedEarly = false)
            }
            is UserEvent.Skip -> {
                val track = QueueTrack(event.trackId, event.title, event.artistName,
                    com.music.echo.notune.intelligence.musicbrain.TrackEmbedding(event.trackId, event.title, event.artistName)
                )
                adaptiveQueueEngine.onPlaybackStateChanged(track, isSkippedEarly = true)
            }
            else -> {}
        }
        return reward
    }

    /**
     * Applies instant user gesture / action override to current session.
     */
    fun applyInstantOverride(
        action: InstantOverrideAction,
        currentTrackGenre: String? = null,
        currentArtistName: String? = null,
        currentTrackEnergy: Float? = null
    ) {
        sessionOverrideEngine.applyInstantOverride(action, currentTrackGenre, currentArtistName, currentTrackEnergy)
    }

    /**
     * "Teach NØTUNE": Compiles explicit natural language directive into rule.
     */
    fun teachRule(directive: String): PreferenceRule {
        val rule = teachNotuneEngine.parseDirective(directive)
        tasteProfileStore.addPreferenceRule(rule)
        return rule
    }

    /**
     * Generates "Why this song?" explanation card for a track.
     */
    fun explainTrack(track: QueueTrack): TrackExplanationCard {
        val userDna = tasteProfileStore.getDnaSnapshot()
        val scoreResult = musicBrain.scoreTrack(track.embedding, userDna)
        return recommendationExplanation.explainTrack(track.title, track.artistName, scoreResult, userDna)
    }

    /**
     * Generates summary for Personalization Control Center (Music DNA Inspector).
     */
    fun getControlCenterSummary() = tasteExplanation.generateControlCenterSummary(tasteProfileStore.getDnaSnapshot())
}
