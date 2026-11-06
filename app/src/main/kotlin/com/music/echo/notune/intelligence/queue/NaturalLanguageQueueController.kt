package com.music.echo.notune.intelligence.queue

import com.music.echo.notune.intelligence.intent.StructuredUserIntent
import com.music.echo.notune.intelligence.recommendation.RankedTrackResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NaturalLanguageQueueController @Inject constructor(
    private val adaptiveQueueOrchestrator: AdaptiveQueueOrchestrator
) {

    fun executeQueueDirective(
        intent: StructuredUserIntent,
        candidateResults: List<RankedTrackResult>
    ): String {
        val tracks = candidateResults.map { it.track }

        return when {
            intent.constraints.excludeArtists.isNotEmpty() -> {
                val excluded = intent.constraints.excludeArtists.first()
                adaptiveQueueOrchestrator.removeTracksByArtist(excluded)
                "Excluded $excluded from active session queue."
            }
            intent.intentType == com.music.echo.notune.intelligence.intent.IntentType.FILTER_QUEUE_LANGUAGE -> {
                val lang = intent.targetLanguage ?: "requested"
                adaptiveQueueOrchestrator.updateQueueFromTracks(null, tracks, lockUpcoming = true)
                "Queue updated to strictly play $lang songs."
            }
            intent.intentType == com.music.echo.notune.intelligence.intent.IntentType.CREATE_TIME_LIMITED_QUEUE -> {
                val duration = intent.durationMinutes ?: 30
                adaptiveQueueOrchestrator.updateQueueFromTracks(null, tracks.take(duration / 3), lockUpcoming = false)
                "Assembled $duration-minute continuous flow queue."
            }
            intent.intentType == com.music.echo.notune.intelligence.intent.IntentType.DISCOVER_UNHEARD -> {
                adaptiveQueueOrchestrator.updateQueueFromTracks(null, tracks, lockUpcoming = false)
                "Injected unheard discovery tracks into upcoming queue."
            }
            else -> {
                if (tracks.isNotEmpty()) {
                    adaptiveQueueOrchestrator.updateQueueFromTracks(null, tracks, lockUpcoming = false)
                    "Queue re-ordered based on directive: '${intent.rawQuery}'."
                } else {
                    "Queue flow maintained."
                }
            }
        }
    }
}
