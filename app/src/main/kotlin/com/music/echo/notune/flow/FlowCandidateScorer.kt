package echo.music.iad1tya.notune.flow

import echo.music.iad1tya.models.MediaMetadata
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlowCandidateScorer @Inject constructor() {

    fun scoreCandidates(
        candidates: List<FlowCandidate>,
        currentTrack: MediaMetadata?,
        mode: FlowMode,
        contextMode: FlowContextMode,
        discoveryRatio: Float,
        topArtistIds: List<String> = emptyList(),
        favoriteTrackIds: Set<String> = emptySet(),
        recentlySkippedTrackIds: Map<String, Long> = emptyMap()
    ): List<FlowScore> {
        val currentArtistId = currentTrack?.artists?.firstOrNull()?.id

        return candidates.map { candidate ->
            val metadata = candidate.mediaMetadata
            var artistScore = 0f
            var genreScore = 0f
            var tasteScore = 0f
            var discoveryBonus = 0f
            var skipPenalty = 0f

            // 1. Artist Affinity & Current Similarity
            val candidateArtistId = metadata.artists.firstOrNull()?.id
            if (candidateArtistId != null && candidateArtistId == currentArtistId) {
                artistScore += 25f
            } else if (candidateArtistId != null && topArtistIds.contains(candidateArtistId)) {
                artistScore += 18f
            }

            // 2. Personal Taste & Favorites
            if (favoriteTrackIds.contains(metadata.id)) {
                tasteScore += 20f
            }

            // Candidate Source weights
            when (candidate.candidateSource) {
                "Related Track" -> artistScore += 15f
                "Favorite Track" -> tasteScore += 15f
                "Listening History" -> tasteScore += 10f
                "Favorite Artist" -> artistScore += 12f
            }

            // 3. Discovery Adjustment
            if (candidate.candidateSource == "Discovery" || !favoriteTrackIds.contains(metadata.id)) {
                discoveryBonus += (discoveryRatio * 15f)
            }

            // 4. Mode Adjustments
            when (mode) {
                FlowMode.FAVORITES_FLOW -> tasteScore *= 1.5f
                FlowMode.DISCOVERY_FLOW -> discoveryBonus *= 2.0f
                FlowMode.ARTIST_RADIO -> if (candidateArtistId == currentArtistId) artistScore *= 1.8f
                else -> {}
            }

            // 5. Context Mode Adjustments
            when (contextMode) {
                FlowContextMode.WORKOUT -> {
                    // Boost energetic/high tempo tracks
                    if (metadata.title.contains("workout", ignoreCase = true) || metadata.title.contains("run", ignoreCase = true)) {
                        tasteScore += 10f
                    }
                }
                FlowContextMode.FOCUS, FlowContextMode.RELAX, FlowContextMode.SLEEP -> {
                    // Prefer chill/instrumental/ambient items
                    if (metadata.title.contains("chill", ignoreCase = true) || metadata.title.contains("ambient", ignoreCase = true)) {
                        genreScore += 12f
                    }
                }
                else -> {}
            }

            // 6. Skip Penalties
            if (recentlySkippedTrackIds.containsKey(metadata.id)) {
                skipPenalty += 20f
            }

            val totalScore = (artistScore + genreScore + tasteScore + discoveryBonus - skipPenalty).coerceAtLeast(0f)

            // Primary Reason Determination
            val primaryReason = when {
                candidateArtistId == currentArtistId -> FlowReason(FlowReasonType.SIMILAR_ARTIST, "Similar to current artist")
                favoriteTrackIds.contains(metadata.id) -> FlowReason(FlowReasonType.FAVORITE_ARTIST, "From your favorites")
                candidate.candidateSource == "Related Track" -> FlowReason(FlowReasonType.GENRE_MATCH, "Matches current vibe")
                discoveryBonus > 8f -> FlowReason(FlowReasonType.DISCOVERY, "Fresh discovery for you")
                else -> FlowReason(FlowReasonType.PERSONAL_TASTE, "Based on your taste")
            }

            FlowScore(
                candidate = candidate,
                totalScore = totalScore,
                artistAffinity = artistScore,
                genreAffinity = genreScore,
                discoveryBonus = discoveryBonus,
                skipPenalty = skipPenalty,
                primaryReason = primaryReason
            )
        }.sortedByDescending { it.totalScore }
    }
}
