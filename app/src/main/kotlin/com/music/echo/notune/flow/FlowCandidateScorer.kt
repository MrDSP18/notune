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
        sessionProfile: LanguageSessionProfile = LanguageSessionProfile(),
        topArtistIds: List<String> = emptyList(),
        favoriteTrackIds: Set<String> = emptySet(),
        recentlySkippedTrackIds: Map<String, Long> = emptyMap(),
        recommendationHistory: Map<String, RecommendationHistoryRecord> = emptyMap()
    ): List<FlowScore> {
        val currentArtistId = currentTrack?.artists?.firstOrNull()?.id

        return candidates.map { candidate ->
            val metadata = candidate.mediaMetadata
            var artistScore = 0f
            var genreScore = 0f
            var languageScore = 0f
            var tasteScore = 0f
            var discoveryBonus = 0f
            var skipPenalty = 0f
            var recPenalty = 0f

            // 1. Language Continuity Scoring
            val candidateLang = candidate.language ?: "UNKNOWN"
            if (sessionProfile.primaryLanguage != "UNKNOWN" && candidateLang != "UNKNOWN") {
                if (candidateLang.equals(sessionProfile.primaryLanguage, ignoreCase = true)) {
                    languageScore += 25f * sessionProfile.confidence.coerceAtLeast(0.6f)
                } else if (sessionProfile.secondaryLanguage != null && candidateLang.equals(sessionProfile.secondaryLanguage, ignoreCase = true)) {
                    languageScore += 12f * sessionProfile.confidence
                } else if (sessionProfile.confidence > 0.6f) {
                    // Strong penalty for unprompted language jump when session confidence is high
                    languageScore -= 18f
                }
            }

            // 2. Artist Affinity & Current Similarity
            val candidateArtistId = metadata.artists.firstOrNull()?.id
            if (candidateArtistId != null && candidateArtistId == currentArtistId) {
                artistScore += 22f
            } else if (candidateArtistId != null && topArtistIds.contains(candidateArtistId)) {
                artistScore += 16f
            }

            // 3. Personal Taste & Favorites
            if (favoriteTrackIds.contains(metadata.id)) {
                tasteScore += 18f
            }

            // Candidate Source weights
            when (candidate.candidateSource) {
                "Related Track" -> artistScore += 15f
                "Favorite Track" -> tasteScore += 15f
                "Listening History" -> tasteScore += 10f
                "Favorite Artist" -> artistScore += 12f
            }

            // 4. Discovery Adjustment
            if (candidate.candidateSource == "Discovery" || !favoriteTrackIds.contains(metadata.id)) {
                discoveryBonus += (discoveryRatio * 15f)
            }

            // 5. Mode Adjustments
            when (mode) {
                FlowMode.FAVORITES_FLOW -> tasteScore *= 1.5f
                FlowMode.DISCOVERY_FLOW -> discoveryBonus *= 2.0f
                FlowMode.ARTIST_RADIO -> if (candidateArtistId == currentArtistId) artistScore *= 1.8f
                else -> {}
            }

            // 6. Context Mode Adjustments
            when (contextMode) {
                FlowContextMode.WORKOUT -> {
                    if (metadata.title.contains("workout", ignoreCase = true) || metadata.title.contains("run", ignoreCase = true)) {
                        tasteScore += 10f
                    }
                }
                FlowContextMode.FOCUS, FlowContextMode.RELAX, FlowContextMode.SLEEP -> {
                    if (metadata.title.contains("chill", ignoreCase = true) || metadata.title.contains("ambient", ignoreCase = true)) {
                        genreScore += 12f
                    }
                }
                else -> {}
            }

            // 7. Skip Penalties & Recommendation History Cooldowns
            if (recentlySkippedTrackIds.containsKey(metadata.id)) {
                skipPenalty += 25f
            }

            recommendationHistory[metadata.id]?.let { record ->
                if (record.timesRecommended > 1) {
                    recPenalty += (record.timesRecommended * 6f)
                }
            }

            val totalScore = (artistScore + genreScore + languageScore + tasteScore + discoveryBonus - skipPenalty - recPenalty).coerceAtLeast(0f)

            // Primary Reason Determination
            val primaryReason = when {
                languageScore > 15f -> FlowReason(FlowReasonType.LANGUAGE_MATCH, "Matches your current session language (${sessionProfile.primaryLanguage})")
                candidateArtistId == currentArtistId -> FlowReason(FlowReasonType.SIMILAR_ARTIST, "Similar to current artist")
                favoriteTrackIds.contains(metadata.id) -> FlowReason(FlowReasonType.FAVORITE_ARTIST, "From your favorites")
                candidate.candidateSource == "Related Track" -> FlowReason(FlowReasonType.GENRE_MATCH, "Matches current vibe")
                discoveryBonus > 8f -> FlowReason(FlowReasonType.DISCOVERY, "Fresh discovery for you")
                else -> FlowReason(FlowReasonType.PERSONAL_TASTE, "Based on your taste profile")
            }

            FlowScore(
                candidate = candidate,
                totalScore = totalScore,
                artistAffinity = artistScore,
                genreAffinity = genreScore,
                languageMatch = languageScore,
                discoveryBonus = discoveryBonus,
                skipPenalty = skipPenalty,
                repetitionPenalty = recPenalty,
                primaryReason = primaryReason
            )
        }.sortedByDescending { it.totalScore }
    }
}
