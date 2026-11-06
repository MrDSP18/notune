package echo.music.iad1tya.notune.ai.adaptive

import kotlin.math.abs

/**
 * Manages dynamic session queue re-ranking and candidate injection based on user behavior signals.
 */
class AdaptiveQueueManager(
    val scoringEngine: AdaptiveScoringEngine = AdaptiveScoringEngine()
) {
    private val feedbackHistory = mutableListOf<FeedbackEvent>()
    private val sessionSkippedTrackIds = mutableSetOf<String>()
    private val sessionReplayedTrackIds = mutableSetOf<String>()
    private val sessionLikedTrackIds = mutableSetOf<String>()
    private val sessionDislikedTrackIds = mutableSetOf<String>()

    /**
     * Record a user feedback event and update session behavioral state.
     */
    fun recordFeedback(event: FeedbackEvent) {
        feedbackHistory.add(event)
        when (event.eventType) {
            FeedbackEventType.SKIP -> sessionSkippedTrackIds.add(event.trackId)
            FeedbackEventType.REPLAY -> sessionReplayedTrackIds.add(event.trackId)
            FeedbackEventType.LIKE -> {
                sessionLikedTrackIds.add(event.trackId)
                sessionDislikedTrackIds.remove(event.trackId)
            }
            FeedbackEventType.DISLIKE -> {
                sessionDislikedTrackIds.add(event.trackId)
                sessionLikedTrackIds.remove(event.trackId)
            }
            else -> {}
        }
    }

    /**
     * Clear all recorded session state.
     */
    fun resetSession() {
        feedbackHistory.clear()
        sessionSkippedTrackIds.clear()
        sessionReplayedTrackIds.clear()
        sessionLikedTrackIds.clear()
        sessionDislikedTrackIds.clear()
    }

    /**
     * Dynamically adapt the remaining queue when user interaction signals occur.
     */
    fun adaptQueue(
        currentTrack: AdaptiveTrackContext?,
        remainingQueue: List<AdaptiveTrackContext>,
        candidatePool: List<AdaptiveTrackContext>,
        playedHistory: List<AdaptiveTrackContext> = emptyList()
    ): List<AdaptiveTrackContext> {
        val request = RecommendationRequest(
            sessionId = "active-session",
            currentTrack = currentTrack,
            currentQueue = remainingQueue,
            history = playedHistory,
            skippedTracks = sessionSkippedTrackIds.toList(),
            replayedTracks = sessionReplayedTrackIds.toList(),
            likedTrackIds = sessionLikedTrackIds,
            dislikedTrackIds = sessionDislikedTrackIds
        )

        // 1. Generate candidate list combining unplayed queue items + candidate pool
        val allCandidates = (remainingQueue + candidatePool)
            .distinctBy { it.trackId }
            .filter { candidate -> !sessionDislikedTrackIds.contains(candidate.trackId) }

        // 2. Compute recent artist & genre counts for anti-fatigue filtering
        val recentArtists = (playedHistory.takeLast(5) + listOfNotNull(currentTrack))
            .map { it.artist.lowercase().trim() }
            .groupingBy { it }
            .eachCount()

        val recentGenres = (playedHistory.takeLast(5) + listOfNotNull(currentTrack))
            .mapNotNull { it.genre?.lowercase()?.trim() }
            .groupingBy { it }
            .eachCount()

        // 3. Score all candidates
        val scoredCandidates = allCandidates.map { candidate ->
            scoringEngine.scoreCandidate(candidate, request, recentArtists, recentGenres)
        }

        // 4. Apply Anti-Fatigue & Duplicate Filtering
        val filteredCandidates = filterAntiFatigue(scoredCandidates, playedHistory, currentTrack)

        // 5. Rank candidates by final score descending
        val rankedQueue = filteredCandidates
            .sortedByDescending { it.score }
            .map { it.track }

        return rankedQueue
    }

    /**
     * Filter out recent duplicates, tracks exceeding BPM delta limits, or over-repeated artists.
     */
    fun filterAntiFatigue(
        scoredTracks: List<ScoredTrack>,
        playedHistory: List<AdaptiveTrackContext>,
        currentTrack: AdaptiveTrackContext?
    ): List<ScoredTrack> {
        val playedTrackIds = playedHistory.map { it.trackId }.toSet()
        val result = mutableListOf<ScoredTrack>()
        val artistCountsInResult = mutableMapOf<String, Int>()

        for (scored in scoredTracks) {
            val track = scored.track
            val artistKey = track.artist.lowercase().trim()

            // Skip recently played tracks
            if (playedTrackIds.contains(track.trackId)) continue

            // Anti-Fatigue: Max artist repetition limit in queue window
            val currentArtistCount = artistCountsInResult[artistKey] ?: 0
            if (currentArtistCount >= scoringEngine.config.artistRepetitionLimit) continue

            // BPM transition delta filter (if BPM metadata present)
            if (currentTrack?.bpm != null && track.bpm != null && currentTrack.bpm > 0 && track.bpm > 0) {
                val bpmDelta = abs(track.bpm - currentTrack.bpm) / currentTrack.bpm
                if (bpmDelta > scoringEngine.config.maxBpmTransitionDelta + 0.20f) { // Allow margin
                    continue
                }
            }

            result.add(scored)
            artistCountsInResult[artistKey] = currentArtistCount + 1
        }

        return result
    }
}
