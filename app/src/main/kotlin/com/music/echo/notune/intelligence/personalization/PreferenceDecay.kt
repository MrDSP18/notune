package com.music.echo.notune.intelligence.personalization

import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.exp

/**
 * Handles recency decay for user taste preferences.
 * Ensures older listening events gradually lose weight, allowing NØTUNE to grow with the user.
 */
@Singleton
class PreferenceDecay @Inject constructor() {

    /**
     * Calculates the decayed weight of a preference based on timestamp age.
     * @param baseWeight Initial weight or score of preference [0.0..1.0]
     * @param eventTimestampMs Timestamp when event occurred
     * @param confidence Confidence score of signal [0.0..1.0]
     * @param consistency Stability measure [0.0..1.0]
     * @param halfLifeDays Time span in days after which weight decays to 50% (default: 30 days)
     */
    fun calculateDecayedWeight(
        baseWeight: Float,
        eventTimestampMs: Long,
        confidence: Float = 1.0f,
        consistency: Float = 1.0f,
        halfLifeDays: Float = 30.0f
    ): Float {
        val nowMs = System.currentTimeMillis()
        val ageMs = (nowMs - eventTimestampMs).coerceAtLeast(0L)
        val ageDays = ageMs.toFloat() / TimeUnit.DAYS.toMillis(1).toFloat()

        // Decay constant lambda = ln(2) / halfLife
        val lambda = 0.69314718f / halfLifeDays
        val recencyFactor = exp(-lambda * ageDays)

        val finalWeight = baseWeight * recencyFactor * confidence.coerceIn(0.1f, 1.0f) * consistency.coerceIn(0.1f, 1.0f)
        return finalWeight.coerceIn(0f, 1f)
    }

    /**
     * Decays a map of preferences (e.g. genre/artist weights) over time.
     */
    fun decayPreferenceMap(
        map: Map<String, Float>,
        lastUpdatedMs: Long,
        decayRatePerDay: Float = 0.02f
    ): Map<String, Float> {
        val nowMs = System.currentTimeMillis()
        val ageDays = (nowMs - lastUpdatedMs).toFloat() / TimeUnit.DAYS.toMillis(1).toFloat()
        if (ageDays <= 0f) return map

        val decayMultiplier = exp(-decayRatePerDay * ageDays)
        return map.mapValues { (_, weight) ->
            (weight * decayMultiplier).coerceIn(0.01f, 1.0f)
        }
    }
}
