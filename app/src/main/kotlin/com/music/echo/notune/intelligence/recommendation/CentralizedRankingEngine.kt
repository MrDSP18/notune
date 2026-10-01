package com.music.echo.notune.intelligence.recommendation

import com.music.echo.notune.intelligence.context.NotuneContextSnapshot
import com.music.echo.notune.intelligence.intent.StructuredUserIntent
import com.music.echo.notune.intelligence.musicbrain.MusicClassifierEngine
import com.music.echo.notune.intelligence.personalization.TasteProfileStore
import echo.music.iad1tya.notune.provider.UnifiedTrack
import javax.inject.Inject
import javax.inject.Singleton

data class RankedTrackResult(
    val track: UnifiedTrack,
    val totalScore: Float,
    val explanation: String
)

@Singleton
class CentralizedRankingEngine @Inject constructor(
    private val musicClassifierEngine: MusicClassifierEngine,
    private val tasteProfileStore: TasteProfileStore
) {

    fun rankCandidates(
        candidates: List<UnifiedTrack>,
        intent: StructuredUserIntent,
        contextSnapshot: NotuneContextSnapshot
    ): List<RankedTrackResult> {
        val userDna = tasteProfileStore.getDnaSnapshot()

        return candidates.map { track ->
            val features = musicClassifierEngine.extractFeatures(track.title, track.artist)

            // Positive affinity components
            var score = 0.50f

            // Language match
            if (intent.targetLanguage != null && features.language.equals(intent.targetLanguage, ignoreCase = true)) {
                score += 0.25f
            } else if (userDna.sessionTaste.currentLanguage?.equals(features.language, ignoreCase = true) == true) {
                score += 0.15f
            }

            // Energy match
            if (intent.constraints.minEnergy != null && features.energy >= intent.constraints.minEnergy!!) {
                score += 0.20f
            } else if (intent.constraints.maxEnergy != null && features.energy <= intent.constraints.maxEnergy!!) {
                score += 0.20f
            }

            // Artist match
            if (intent.targetArtist != null && track.artist.contains(intent.targetArtist!!, ignoreCase = true)) {
                score += 0.30f
            }

            // Penalties
            val isExcluded = intent.constraints.excludeArtists.any { track.artist.contains(it, ignoreCase = true) }
            if (isExcluded) {
                score -= 0.80f
            }

            val finalScore = score.coerceIn(0.01f, 0.99f)

            val explanation = buildString {
                if (intent.targetLanguage != null && features.language.equals(intent.targetLanguage, ignoreCase = true)) {
                    append("Matches requested ${intent.targetLanguage} language preference. ")
                }
                if (intent.targetArtist != null && track.artist.contains(intent.targetArtist!!, ignoreCase = true)) {
                    append("Matches requested artist ${intent.targetArtist}. ")
                }
                if (isEmpty()) append("Matches your continuous listening DNA and session energy profile.")
            }.trim()

            RankedTrackResult(
                track = track,
                totalScore = finalScore,
                explanation = explanation
            )
        }.sortedByDescending { it.totalScore }
    }
}
