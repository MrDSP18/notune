package com.music.echo.notune.intelligence.musicbrain

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs
import kotlin.math.sqrt

@Singleton
class SimilarityEngine @Inject constructor() {

    fun cosineSimilarity(v1: FloatArray, v2: FloatArray): Float {
        if (v1.size != v2.size || v1.isEmpty()) return 0f
        var dotProduct = 0f
        var normA = 0f
        var normB = 0f
        for (i in v1.indices) {
            dotProduct += v1[i] * v2[i]
            normA += v1[i] * v1[i]
            normB += v2[i] * v2[i]
        }
        if (normA == 0f || normB == 0f) return 0f
        return (dotProduct / (sqrt(normA.toDouble()) * sqrt(normB.toDouble()))).toFloat().coerceIn(0f, 1f)
    }

    fun vectorDistance(v1: FloatArray, v2: FloatArray): Float {
        if (v1.size != v2.size) return 1f
        var sum = 0f
        for (i in v1.indices) {
            val diff = v1[i] - v2[i]
            sum += diff * diff
        }
        return sqrt(sum.toDouble()).toFloat()
    }
}

@Singleton
class MoodEngine @Inject constructor() {
    fun scoreMoodMatch(targetMood: String, trackMoodValence: Float): Float {
        val expectedValence = when (targetMood.lowercase()) {
            "happy", "energetic", "upbeat", "party" -> 0.85f
            "calm", "relax", "peaceful", "study" -> 0.50f
            "sad", "melancholic", "reflective" -> 0.25f
            else -> 0.60f
        }
        return (1.0f - abs(expectedValence - trackMoodValence)).coerceIn(0f, 1f)
    }
}

@Singleton
class EnergyEngine @Inject constructor() {
    fun scoreEnergyMatch(targetEnergy: Float, trackEnergy: Float): Float {
        val diff = abs(targetEnergy - trackEnergy)
        return (1.0f - diff).coerceIn(0f, 1f)
    }
}

@Singleton
class LanguageEngine @Inject constructor() {
    fun scoreLanguageMatch(userPreferredLangs: Map<String, Float>, trackLang: String): Float {
        val matchedWeight = userPreferredLangs[trackLang]
        if (matchedWeight != null) return matchedWeight.coerceIn(0f, 1f)

        // Case insensitive search
        for ((lang, weight) in userPreferredLangs) {
            if (lang.equals(trackLang, ignoreCase = true)) return weight.coerceIn(0f, 1f)
        }
        return 0.2f
    }
}
