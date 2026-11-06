package com.music.echo.notune.intelligence.recommendation

import com.music.echo.notune.intelligence.personalization.NotuneUserDNA
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Exploration vs. Exploitation Engine
 *
 * Default baseline:
 * 70% → Exploitation (familiar music high-confidence match)
 * 20% → Similar Discovery (new tracks from familiar genres/artists)
 * 10% → Experimental Discovery (unknown artists/genres)
 *
 * Dynamic adjustments:
 * - High consecutive skips → familiarity ↑, exploration ↓
 * - Long continuous listening → exploration ↑
 * - Explicit prompt ("give me something new") → novelty ↑↑↑
 */
@Singleton
class ExplorationEngine @Inject constructor() {

    fun getDynamicRatio(
        userDna: NotuneUserDNA,
        explicitNoveltyRequested: Boolean = false
    ): ExplorationRatio {
        val consecutiveSkips = userDna.sessionTaste.consecutiveSkips

        if (explicitNoveltyRequested) {
            return ExplorationRatio(
                exploitationRate = 0.20f,
                similarDiscoveryRate = 0.50f,
                experimentalDiscoveryRate = 0.30f
            )
        }

        val learnedExploration = userDna.discoveryProfile.explorationRate.coerceIn(0.05f, 0.60f)
        val baseExploit = (1.0f - learnedExploration).coerceIn(0.40f, 0.95f)

        return when {
            consecutiveSkips >= 3 -> {
                // User is skipping everything: fall back to safe favorites
                ExplorationRatio(
                    exploitationRate = 0.90f,
                    similarDiscoveryRate = 0.10f,
                    experimentalDiscoveryRate = 0.00f
                )
            }
            consecutiveSkips == 2 -> {
                ExplorationRatio(
                    exploitationRate = 0.82f,
                    similarDiscoveryRate = 0.13f,
                    experimentalDiscoveryRate = 0.05f
                )
            }
            userDna.sessionTaste.totalSessionTracks >= 10 -> {
                // Session is going great, increase discovery based on learned profile
                val exploit = (baseExploit - 0.10f).coerceAtLeast(0.40f)
                val similar = (learnedExploration * 0.65f)
                val exp = 1.0f - exploit - similar
                ExplorationRatio(
                    exploitationRate = exploit,
                    similarDiscoveryRate = similar,
                    experimentalDiscoveryRate = exp.coerceAtLeast(0.05f)
                )
            }
            else -> {
                val similar = (learnedExploration * 0.60f)
                val exp = 1.0f - baseExploit - similar
                ExplorationRatio(
                    exploitationRate = baseExploit,
                    similarDiscoveryRate = similar,
                    experimentalDiscoveryRate = exp.coerceAtLeast(0.05f)
                )
            }
        }
    }
}
