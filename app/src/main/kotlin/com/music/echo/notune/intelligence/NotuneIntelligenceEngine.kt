package com.music.echo.notune.intelligence

import com.music.echo.notune.intelligence.context.ContextEngine
import com.music.echo.notune.intelligence.context.InferredSituation
import com.music.echo.notune.intelligence.explanation.RecommendationExplanation
import com.music.echo.notune.intelligence.explanation.TasteExplanation
import com.music.echo.notune.intelligence.explanation.TrackExplanationCard
import com.music.echo.notune.intelligence.feedback.FeedbackProcessor
import com.music.echo.notune.intelligence.feedback.UserEvent
import com.music.echo.notune.intelligence.musicbrain.MusicBrain
import com.music.echo.notune.intelligence.personalization.NotuneUserDNA
import com.music.echo.notune.intelligence.personalization.PreferenceRule
import com.music.echo.notune.intelligence.personalization.TasteProfileStore
import com.music.echo.notune.intelligence.personalization.TeachNotuneEngine
import com.music.echo.notune.intelligence.queue.AdaptiveQueueEngine
import com.music.echo.notune.intelligence.queue.QueueTrack
import com.music.echo.notune.intelligence.recommendation.RecommendationBatch
import com.music.echo.notune.intelligence.recommendation.RecommendationCandidate
import com.music.echo.notune.intelligence.recommendation.RecommendationEngine
import com.music.echo.notune.intelligence.search.PersonalizedSearchEngine
import com.music.echo.notune.intelligence.search.RankedSearchResult
import com.music.echo.notune.intelligence.search.SearchCandidate
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * NØTUNE Personal Music Intelligence Engine v1
 *
 * Unified Coordinator for the 8 Production Components:
 * 1. Personal User DNA
 * 2. Personalized Search Engine
 * 3. Context Engine
 * 4. Music Brain
 * 5. Adaptive Queue Engine
 * 6. Continuous Feedback Learning
 * 7. Teach NØTUNE / Explicit Preferences
 * 8. Recommendation Explanation & Control Center
 */
@Singleton
class NotuneIntelligenceEngine @Inject constructor(
    val tasteProfileStore: TasteProfileStore,
    val searchEngine: PersonalizedSearchEngine,
    val contextEngine: ContextEngine,
    val musicBrain: MusicBrain,
    val adaptiveQueueEngine: AdaptiveQueueEngine,
    val feedbackProcessor: FeedbackProcessor,
    val teachNotuneEngine: TeachNotuneEngine,
    val recommendationEngine: RecommendationEngine,
    val recommendationExplanation: RecommendationExplanation,
    val tasteExplanation: TasteExplanation
) {

    val userDnaFlow: StateFlow<NotuneUserDNA> = tasteProfileStore.userDna

    fun getCurrentDna(): NotuneUserDNA = tasteProfileStore.getDnaSnapshot()

    /**
     * Executes personalized natural language search.
     */
    fun search(rawQuery: String, candidates: List<SearchCandidate>): List<RankedSearchResult> {
        return searchEngine.rankCandidates(rawQuery, candidates)
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
     * Updates adaptive queue with new playing and upcoming tracks.
     */
    fun updateQueue(current: QueueTrack?, upcoming: List<QueueTrack>) {
        adaptiveQueueEngine.setQueue(current, upcoming)
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
