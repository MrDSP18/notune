package com.music.echo.notune.intelligence.queue

import com.music.echo.notune.intelligence.musicbrain.MusicBrain
import com.music.echo.notune.intelligence.personalization.TasteProfileStore
import com.music.echo.notune.intelligence.session.SessionSeedManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

enum class FeedbackSignalType {
    EARLY_SKIP,   // < 10s (-0.8)
    SKIP,         // 10-30% (-0.5)
    LATE_SKIP,    // 70-90% (-0.2)
    COMPLETE,     // 100% (+0.4)
    REPLAY,       // (+0.7)
    FAVORITE      // (+1.0)
}

data class QueueTiers(
    val now: QueueTrack? = null,
    val next3: List<QueueTrack> = emptyList(),
    val balanced5: List<QueueTrack> = emptyList(),
    val discovery5: List<QueueTrack> = emptyList(),
    val reservePool: List<QueueTrack> = emptyList()
)

/**
 * NØTUNE Adaptive Queue Engine
 *
 * Continuously monitors user interactions (plays, weighted skips, replays, favorites)
 * and re-ranks upcoming queue tracks using TasteMatch, CurrentMood, TransitionQuality,
 * feature-level feedback adjustments, and Repetition Penalties.
 */
@Singleton
class AdaptiveQueueEngine @Inject constructor(
    private val musicBrain: MusicBrain,
    private val transitionScorer: TransitionScorer,
    private val transitionEngine: TransitionEngine,
    private val repetitionController: RepetitionController,
    private val tasteProfileStore: TasteProfileStore,
    private val sessionSeedManager: SessionSeedManager
) {

    private val _queueState = MutableStateFlow(QueueState())
    val queueState: StateFlow<QueueState> = _queueState.asStateFlow()

    private val _queueTiers = MutableStateFlow(QueueTiers())
    val queueTiers: StateFlow<QueueTiers> = _queueTiers.asStateFlow()

    fun setQueue(current: QueueTrack?, upcoming: List<QueueTrack>) {
        val reordered = reorderQueue(current, upcoming)
        _queueState.update {
            it.copy(
                currentlyPlaying = current,
                upcomingQueue = reordered,
                overallFlowScore = calculateAverageFlow(current, reordered)
            )
        }
        updateQueueTiers(current, reordered)
    }

    /**
     * Called whenever playback state changes or user gives feedback.
     */
    fun onFeedback(playedTrack: QueueTrack, signal: FeedbackSignalType, progressPct: Float = 1.0f) {
        val weight = when (signal) {
            FeedbackSignalType.EARLY_SKIP -> -0.8f
            FeedbackSignalType.SKIP -> -0.5f
            FeedbackSignalType.LATE_SKIP -> -0.2f
            FeedbackSignalType.COMPLETE -> +0.4f
            FeedbackSignalType.REPLAY -> +0.7f
            FeedbackSignalType.FAVORITE -> +1.0f
        }

        // Apply feature-level feedback adjustments to current session profile
        if (weight < 0f) {
            sessionSeedManager.updateSessionProfile { profile ->
                val adjustedEnergy = (profile.currentEnergyTarget + (profile.currentEnergyTarget - playedTrack.embedding.energy) * 0.15f).coerceIn(0.1f, 1.0f)
                val adjustedTempo = (profile.currentTempoTarget + (profile.currentTempoTarget - playedTrack.embedding.tempoBpm) * 0.10f).coerceIn(60f, 180f)
                profile.copy(
                    currentEnergyTarget = adjustedEnergy,
                    currentTempoTarget = adjustedTempo
                )
            }
        } else {
            sessionSeedManager.updateSessionProfile { profile ->
                profile.copy(
                    currentEnergyTarget = (profile.currentEnergyTarget * 0.8f + playedTrack.embedding.energy * 0.2f).coerceIn(0.1f, 1.0f),
                    currentTempoTarget = (profile.currentTempoTarget * 0.8f + playedTrack.embedding.tempoBpm * 0.2f).coerceIn(60f, 180f)
                )
            }
        }

        onPlaybackStateChanged(playedTrack, isSkippedEarly = weight < -0.4f)
    }

    fun onPlaybackStateChanged(
        playedTrack: QueueTrack,
        isSkippedEarly: Boolean
    ) {
        _queueState.update { state ->
            val updatedHistory = state.playedHistory + playedTrack
            val remainingUpcoming = state.upcomingQueue.filterNot { it.id == playedTrack.id }

            val reordered = reorderQueue(remainingUpcoming.firstOrNull(), remainingUpcoming.drop(1))
            val newCurrent = remainingUpcoming.firstOrNull()
            updateQueueTiers(newCurrent, reordered)

            state.copy(
                currentlyPlaying = newCurrent,
                upcomingQueue = reordered,
                playedHistory = updatedHistory,
                overallFlowScore = calculateAverageFlow(newCurrent, reordered)
            )
        }
    }

    fun setFlowMode(mode: NotuneFlowMode) {
        _queueState.update { it.copy(flowMode = mode) }
        sessionSeedManager.updateSessionProfile { it.copy(flowMode = mode) }
    }

    fun reorderQueue(
        current: QueueTrack?,
        upcoming: List<QueueTrack>
    ): List<QueueTrack> {
        if (upcoming.isEmpty()) return emptyList()

        val userDna = tasteProfileStore.getDnaSnapshot()
        val currentHistory = _queueState.value.playedHistory

        // Separate user-locked tracks (manual additions) from dynamic AI candidates
        val lockedTracks = upcoming.filter { it.isLockedByUser }
        val dynamicCandidates = upcoming.filterNot { it.isLockedByUser }

        val scoredDynamic = dynamicCandidates.map { track ->
            val brainScore = musicBrain.scoreTrack(track.embedding, userDna).totalScore

            val transitionEval = if (current != null) {
                transitionEngine.evaluateTransition(current.embedding, track.embedding)
            } else null

            val transitionQuality = transitionEval?.transitionQuality ?: 0.85f

            val overexposurePenalty = repetitionController.calculateOverexposurePenalty(
                candidateArtist = track.artistName,
                candidateTrackId = track.id,
                recentPlayed = currentHistory,
                upcomingQueue = upcoming
            )

            val finalScore = (brainScore * 0.45f + transitionQuality * 0.35f - overexposurePenalty * 0.20f).coerceIn(0f, 1f)

            val details = TrackScoreDetails(
                totalScore = finalScore,
                tasteMatch = brainScore,
                currentMoodMatch = userDna.sessionTaste.currentEnergy,
                transitionQuality = transitionQuality,
                repetitionPenalty = overexposurePenalty,
                explanation = "Flow score: ${(transitionQuality * 100).toInt()}% • Taste match: ${(brainScore * 100).toInt()}%"
            )

            track.copy(scoreDetails = details)
        }.sortedByDescending { it.scoreDetails.totalScore }

        return lockedTracks + scoredDynamic
    }

    private fun updateQueueTiers(current: QueueTrack?, upcoming: List<QueueTrack>) {
        val next3 = upcoming.take(3)
        val balanced5 = upcoming.drop(3).take(5)
        val discovery5 = upcoming.drop(8).take(5)
        val reservePool = upcoming.drop(13)

        _queueTiers.value = QueueTiers(
            now = current,
            next3 = next3,
            balanced5 = balanced5,
            discovery5 = discovery5,
            reservePool = reservePool
        )
    }

    private fun calculateAverageFlow(current: QueueTrack?, upcoming: List<QueueTrack>): Float {
        if (current == null || upcoming.isEmpty()) return 0.85f
        var sum = 0f
        var count = 0
        var prev: QueueTrack? = current
        for (next in upcoming.take(5)) {
            val p = prev
            if (p != null) {
                sum += transitionScorer.calculateTransition(p.embedding, next.embedding).transitionQuality
                count++
            }
            prev = next
        }
        return if (count > 0) sum / count else 0.85f
    }
}
