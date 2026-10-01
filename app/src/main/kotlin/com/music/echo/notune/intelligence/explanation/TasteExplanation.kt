package com.music.echo.notune.intelligence.explanation

import com.music.echo.notune.intelligence.personalization.NotuneUserDNA
import javax.inject.Inject
import javax.inject.Singleton

data class DnaControlCenterSummary(
    val topGenres: List<Pair<String, Int>>,     // Genre to percentage (0..100)
    val topLanguages: List<Pair<String, Int>>,  // Language to percentage (0..100)
    val discoveryScorePct: Int,                 // 0..100
    val noveltyScorePct: Int,                   // 0..100
    val familiarityScorePct: Int,               // 0..100
    val currentMood: String,
    val currentEnergyLabel: String,
    val isSessionExcluded: Boolean,
    val activeRulesCount: Int,
    val learnedInsights: List<String>
)

@Singleton
class TasteExplanation @Inject constructor() {

    fun generateControlCenterSummary(userDna: NotuneUserDNA): DnaControlCenterSummary {
        val topGenres = userDna.coreTaste.genres.entries
            .sortedByDescending { it.value }
            .take(5)
            .map { it.key to (it.value * 100).toInt() }

        val topLangs = userDna.coreTaste.languages.entries
            .sortedByDescending { it.value }
            .take(4)
            .map { it.key to (it.value * 100).toInt() }

        val discoveryPct = (userDna.discoveryProfile.explorationRate * 100).toInt()
        val noveltyPct = (userDna.discoveryProfile.noveltyTolerance * 100).toInt()
        val familiarityPct = (userDna.discoveryProfile.familiarMusicPreference * 100).toInt()

        val energy = userDna.sessionTaste.currentEnergy
        val energyLabel = when {
            energy > 0.8f -> "High Energy"
            energy > 0.5f -> "Moderate"
            else -> "Low / Calm"
        }

        val insights = mutableListOf<String>()
        if (topGenres.isNotEmpty()) {
            insights.add("↑ Primary taste affinity: ${topGenres.first().first}")
        }
        if (topLangs.isNotEmpty()) {
            insights.add("↑ Preferred language: ${topLangs.first().first}")
        }
        if (userDna.sessionTaste.consecutiveSkips >= 2) {
            insights.add("↓ Temporary skip adjustment active (fallback to familiar music)")
        }
        if (userDna.sessionTaste.isExcludedSession) {
            insights.add("🛡️ Session excluded from long-term taste profile")
        }

        return DnaControlCenterSummary(
            topGenres = topGenres,
            topLanguages = topLangs,
            discoveryScorePct = discoveryPct,
            noveltyScorePct = noveltyPct,
            familiarityScorePct = familiarityPct,
            currentMood = userDna.sessionTaste.currentMood,
            currentEnergyLabel = energyLabel,
            isSessionExcluded = userDna.sessionTaste.isExcludedSession,
            activeRulesCount = userDna.activeRules.size,
            learnedInsights = insights
        )
    }
}
