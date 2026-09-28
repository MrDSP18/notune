package com.music.echo.notune.intelligence.personalization

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguageIntelligence @Inject constructor() {
    private val languageAffinities = mutableMapOf<String, Float>(
        "Tamil" to 0.85f,
        "English" to 0.75f,
        "Hindi" to 0.60f,
        "Telugu" to 0.50f,
        "Malayalam" to 0.50f
    )

    fun getLanguageAffinity(language: String): Float {
        return languageAffinities[language] ?: 0.30f
    }

    fun updateLanguageAffinity(language: String, delta: Float) {
        val current = getLanguageAffinity(language)
        val updated = (current + delta).coerceIn(0.0f, 1.0f)
        languageAffinities[language] = updated
    }

    fun getTopLanguages(limit: Int = 3): List<Pair<String, Float>> {
        return languageAffinities.entries
            .sortedByDescending { it.value }
            .take(limit)
            .map { Pair(it.key, it.value) }
    }

    fun isLanguageTransitionSmooth(currentLanguage: String, nextLanguage: String): Boolean {
        if (currentLanguage.equals(nextLanguage, ignoreCase = true)) return true
        val currentAffinity = getLanguageAffinity(currentLanguage)
        val nextAffinity = getLanguageAffinity(nextLanguage)
        // Smooth if candidate is also a highly preferred language for user (affinity >= 0.5)
        return nextAffinity >= 0.50f || (currentAffinity < 0.40f && nextAffinity >= 0.40f)
    }
}
