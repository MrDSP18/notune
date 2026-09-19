package echo.music.iad1tya.notune.flow

import echo.music.iad1tya.models.MediaMetadata
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlowCandidateFilter @Inject constructor() {

    fun filterCandidates(
        candidates: List<FlowCandidate>,
        currentTrack: MediaMetadata?,
        activeQueueTrackIds: List<String>,
        recentlyPlayedIds: List<String> = emptyList(),
        recentlySkippedIds: List<String> = emptyList(),
        recommendationHistory: Map<String, RecommendationHistoryRecord> = emptyMap(),
        config: FlowConfig = FlowConfig()
    ): List<FlowCandidate> {
        val currentId = currentTrack?.id
        val queueSet = activeQueueTrackIds.toSet()
        val now = System.currentTimeMillis()

        return candidates.filter { candidate ->
            val trackId = candidate.mediaMetadata.id

            // 1. Hard Structural Anti-Repetition Filters
            if (trackId.isBlank()) return@filter false
            if (trackId == currentId) return@filter false
            if (queueSet.contains(trackId)) return@filter false

            // 2. Skip Filter - reject tracks skipped within recent window
            val isRecentlySkipped = recentlySkippedIds.take(15).contains(trackId)
            if (isRecentlySkipped) return@filter false

            // 3. Recommendation Cooldown Penalty
            val recRecord = recommendationHistory[trackId]
            if (recRecord != null) {
                // If recommended 3+ times without play, or skipped 2+ times, filter out
                if (recRecord.skippedCount >= 2) return@filter false
                if (recRecord.timesRecommended >= 3 && recRecord.playedCount == 0 && (now - recRecord.recommendedAtMs) < 600_000L) {
                    return@filter false
                }
            }

            // 4. Soft Repetition Filter based on candidate pool size
            val isRecentlyPlayed = recentlyPlayedIds.take(config.trackRepetitionWindow).contains(trackId)
            if (isRecentlyPlayed && candidates.size > config.targetQueueSize * 2) {
                return@filter false
            }

            true
        }
    }

    /**
     * Applies artist & album diversification and prevents alternating A-B-A-B flip-flop patterns.
     */
    fun diversifyCandidates(
        scoredCandidates: List<FlowScore>,
        config: FlowConfig = FlowConfig()
    ): List<FlowScore> {
        val result = mutableListOf<FlowScore>()
        val recentArtistIds = mutableListOf<String>()
        val recentAlbumNames = mutableListOf<String>()
        val recentTrackIds = mutableListOf<String>()

        for (scored in scoredCandidates) {
            val trackId = scored.candidate.mediaMetadata.id
            val artistId = scored.candidate.mediaMetadata.artists.firstOrNull()?.id.orEmpty()
            val albumName = scored.candidate.mediaMetadata.album?.title.orEmpty()

            // Count artist & album occurrences in recent window
            val artistCount = recentArtistIds.takeLast(config.artistRepetitionWindow).count { it == artistId && it.isNotEmpty() }
            val albumCount = recentAlbumNames.takeLast(config.albumRepetitionWindow).count { it == albumName && it.isNotEmpty() }

            if (artistCount >= 2 || albumCount >= 2) {
                continue
            }

            // Detect A-B-A alternating track pattern (if track was played 1 or 2 slots ago)
            if (recentTrackIds.takeLast(2).contains(trackId)) {
                continue
            }

            // Detect Artist A - Artist B - Artist A pattern
            if (recentArtistIds.takeLast(2).contains(artistId) && artistId.isNotEmpty()) {
                val lastArtist = recentArtistIds.lastOrNull()
                if (lastArtist != null && lastArtist != artistId) {
                    // Alternating artist pattern A -> B -> A detected
                    continue
                }
            }

            result.add(scored)
            recentTrackIds.add(trackId)
            if (artistId.isNotEmpty()) recentArtistIds.add(artistId)
            if (albumName.isNotEmpty()) recentAlbumNames.add(albumName)
        }

        // If diversification filtered out all candidates, fallback to top scored candidates
        if (result.isEmpty()) {
            for (scored in scoredCandidates) {
                if (!result.contains(scored)) {
                    result.add(scored)
                }
            }
        }

        return result
    }
}
