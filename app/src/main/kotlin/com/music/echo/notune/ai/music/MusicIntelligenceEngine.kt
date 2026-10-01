package echo.music.iad1tya.notune.ai.music

import echo.music.iad1tya.models.MediaMetadata
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

data class CandidateScore(
    val track: MediaMetadata,
    val totalScore: Double,
    val tasteScore: Double,
    val energyScore: Double,
    val repetitionPenalty: Double
)

@Singleton
class MusicIntelligenceEngine @Inject constructor() {

    fun scoreCandidates(
        candidates: List<MediaMetadata>,
        history: List<MediaMetadata>,
        targetEnergy: Double = 0.7,
        preferredLanguages: List<String> = listOf("Tamil", "English")
    ): List<CandidateScore> {
        val historySet = history.map { it.id }.toSet()

        return candidates.map { track ->
            val isRecentlyPlayed = historySet.contains(track.id)
            val repPenalty = if (isRecentlyPlayed) 2.5 else 0.0

            val languageMatch = preferredLanguages.any { lang ->
                track.title.contains(lang, ignoreCase = true) || track.artists.any { a -> a.name.contains(lang, ignoreCase = true) }
            }
            val taste = if (languageMatch) 3.0 else 1.0
            val energy = 2.0 // Baseline energy score

            val finalScore = max(0.0, taste + energy - repPenalty)

            CandidateScore(
                track = track,
                totalScore = finalScore,
                tasteScore = taste,
                energyScore = energy,
                repetitionPenalty = repPenalty
            )
        }.sortedByDescending { it.totalScore }
    }
}
