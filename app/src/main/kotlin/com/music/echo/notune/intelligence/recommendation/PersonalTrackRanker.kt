package com.music.echo.notune.intelligence.recommendation

import com.music.echo.notune.intelligence.musicbrain.MusicIdentity
import com.music.echo.notune.intelligence.personalization.LanguageIntelligence
import javax.inject.Inject
import javax.inject.Singleton

data class RankedTrack(
    val track: MusicIdentity,
    val score: Float,                  // 0.0f to 1.0f
    val matchDescription: String,
    val tasteFit: Float,
    val sessionFit: Float,
    val availabilityConfidence: Float
)

@Singleton
class PersonalTrackRanker @Inject constructor(
    private val languageIntelligence: LanguageIntelligence
) {
    fun rankCandidate(
        track: MusicIdentity,
        query: String = "",
        currentLanguage: String = "Tamil",
        recentArtists: List<String> = emptyList(),
        recentTrackIds: List<String> = emptyList()
    ): RankedTrack {
        if (!track.isPlayable) {
            return RankedTrack(
                track = track,
                score = 0.0f,
                matchDescription = "UNAVAILABLE",
                tasteFit = 0.0f,
                sessionFit = 0.0f,
                availabilityConfidence = 0.0f
            )
        }

        // Query Match Score (0.0 - 1.0)
        val queryMatch = if (query.isEmpty()) 0.5f else {
            val q = query.lowercase()
            if (track.canonicalTitle.lowercase().contains(q) || track.canonicalArtists.any { it.lowercase().contains(q) }) 1.0f else 0.2f
        }

        // Language Match Score (0.0 - 1.0)
        val langAffinity = languageIntelligence.getLanguageAffinity(track.primaryLanguage)
        val isCurrentLang = track.primaryLanguage.equals(currentLanguage, ignoreCase = true)
        val languageMatch = if (isCurrentLang) (langAffinity * 0.6f + 0.4f) else langAffinity

        // Artist Affinity (0.0 - 1.0)
        val primaryArtist = track.canonicalArtists.firstOrNull() ?: ""
        val artistOverexposureCount = recentArtists.count { it.equals(primaryArtist, ignoreCase = true) }
        val artistOverexposurePenalty = if (artistOverexposureCount >= 3) 0.35f else if (artistOverexposureCount == 2) 0.15f else 0.0f
        val artistAffinity = 0.70f - artistOverexposurePenalty

        // Genre & Session Match
        val genreAffinity = 0.75f
        val sessionMatch = if (isCurrentLang && artistOverexposureCount < 2) 0.85f else 0.50f
        val longTermTaste = 0.80f

        // Repetition Penalty
        val isRecentlyPlayed = recentTrackIds.contains(track.trackId)
        val repetitionPenalty = if (isRecentlyPlayed) 0.40f else 0.0f

        // Weighted Final Score
        val rawScore = (queryMatch * 0.15f) +
                (languageMatch * 0.25f) +
                (artistAffinity * 0.20f) +
                (genreAffinity * 0.15f) +
                (sessionMatch * 0.15f) +
                (longTermTaste * 0.10f) -
                repetitionPenalty

        val finalScore = rawScore.coerceIn(0.0f, 1.0f)

        val matchDescription = when {
            finalScore >= 0.85f -> "VERY STRONG MATCH"
            finalScore >= 0.70f -> "STRONG MATCH"
            finalScore >= 0.50f -> "GOOD FIT"
            else -> "DISCOVERY CANDIDATE"
        }

        return RankedTrack(
            track = track,
            score = finalScore,
            matchDescription = matchDescription,
            tasteFit = (artistAffinity + genreAffinity) / 2f,
            sessionFit = sessionMatch,
            availabilityConfidence = track.sourceConfidence
        )
    }

    fun rankCandidates(
        candidates: List<MusicIdentity>,
        query: String = "",
        currentLanguage: String = "Tamil",
        recentArtists: List<String> = emptyList(),
        recentTrackIds: List<String> = emptyList()
    ): List<RankedTrack> {
        return candidates
            .map { rankCandidate(it, query, currentLanguage, recentArtists, recentTrackIds) }
            .filter { it.availabilityConfidence > 0.0f }
            .sortedByDescending { it.score }
    }
}
