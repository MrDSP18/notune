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
        config: FlowConfig = FlowConfig()
    ): List<FlowCandidate> {
        val currentId = currentTrack?.id
        val queueSet = activeQueueTrackIds.toSet()

        return candidates.filter { candidate ->
            val trackId = candidate.mediaMetadata.id

            // Hard Filters
            if (trackId.isBlank()) return@filter false
            if (trackId == currentId) return@filter false
            if (queueSet.contains(trackId)) return@filter false

            // Soft Repetition Filter
            val isRecentlyPlayed = recentlyPlayedIds.take(config.trackRepetitionWindow).contains(trackId)
            val isRecentlySkipped = recentlySkippedIds.take(10).contains(trackId)

            // Skip tracks that were skipped very recently
            if (isRecentlySkipped) return@filter false

            // If library size allows, prefer non-recently played tracks
            if (isRecentlyPlayed && candidates.size > config.targetQueueSize * 2) {
                return@filter false
            }

            true
        }
    }

    /**
     * Applies artist & album diversification across the candidate list to prevent mono-artist clusters.
     */
    fun diversifyCandidates(
        scoredCandidates: List<FlowScore>,
        config: FlowConfig = FlowConfig()
    ): List<FlowScore> {
        val result = mutableListOf<FlowScore>()
        val recentArtistIds = mutableListOf<String>()
        val recentAlbumNames = mutableListOf<String>()

        for (scored in scoredCandidates) {
            val artistId = scored.candidate.mediaMetadata.artists.firstOrNull()?.id.orEmpty()
            val albumName = scored.candidate.mediaMetadata.album?.title.orEmpty()

            // Count artist occurrences in recent window
            val artistCount = recentArtistIds.takeLast(config.artistRepetitionWindow).count { it == artistId && it.isNotEmpty() }
            val albumCount = recentAlbumNames.takeLast(config.albumRepetitionWindow).count { it == albumName && it.isNotEmpty() }

            if (artistCount >= 2 || albumCount >= 2) {
                // Skip overexposed artist/album in immediate sequence if other candidates exist
                continue
            }

            result.add(scored)
            if (artistId.isNotEmpty()) recentArtistIds.add(artistId)
            if (albumName.isNotEmpty()) recentAlbumNames.add(albumName)
        }

        // If diversification filtered out too many, append remaining items
        if (result.size < config.minQueueSize) {
            for (scored in scoredCandidates) {
                if (!result.contains(scored)) {
                    result.add(scored)
                }
            }
        }

        return result
    }
}
