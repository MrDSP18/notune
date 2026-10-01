package com.music.echo.notune.intelligence.queue

import com.music.echo.notune.intelligence.musicbrain.MusicBrain
import com.music.echo.notune.intelligence.personalization.TasteProfileStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * NØTUNE Adaptive Queue Engine
 *
 * Continuously monitors user interactions (plays, skips, replays) and re-ranks upcoming queue
 * tracks using TasteMatch, CurrentMood, TransitionQuality (Flow), and Repetition Penalties.
 */
@Singleton
class AdaptiveQueueEngine @Inject constructor(
    private val musicBrain: MusicBrain,
    private val transitionScorer: TransitionScorer,
    private val repetitionController: RepetitionController,
    private val tasteProfileStore: TasteProfileStore
) {

    private val _queueState = MutableStateFlow(QueueState())
    val queueState: StateFlow<QueueState> = _queueState.asStateFlow()

    fun setQueue(current: QueueTrack?, upcoming: List<QueueTrack>) {
        val reordered = reorderQueue(current, upcoming)
        _queueState.update {
            it.copy(
                currentlyPlaying = current,
                upcomingQueue = reordered,
                overallFlowScore = calculateAverageFlow(current, reordered)
            )
        }
    }

    /**
     * Called whenever user completes a track or skips.
     * Re-scores all remaining tracks in the upcoming queue.
     */
    fun onPlaybackStateChanged(
        playedTrack: QueueTrack,
        isSkippedEarly: Boolean
    ) {
        _queueState.update { state ->
            val updatedHistory = state.playedHistory + playedTrack
            val remainingUpcoming = state.upcomingQueue.filterNot { it.id == playedTrack.id }

            val reordered = reorderQueue(remainingUpcoming.firstOrNull(), remainingUpcoming.drop(1))
            state.copy(
                currentlyPlaying = remainingUpcoming.firstOrNull(),
                upcomingQueue = reordered,
                playedHistory = updatedHistory,
                overallFlowScore = calculateAverageFlow(remainingUpcoming.firstOrNull(), reordered)
            )
        }
    }

    fun setFlowMode(mode: NotuneFlowMode) {
        _queueState.update { it.copy(flowMode = mode) }
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

            val transitionQuality = if (current != null) {
                transitionScorer.calculateTransition(current.embedding, track.embedding).transitionQuality
            } else 0.85f

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

        // Place user-locked tracks first, then dynamic auto-filled recommendations
        return lockedTracks + scoredDynamic
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
