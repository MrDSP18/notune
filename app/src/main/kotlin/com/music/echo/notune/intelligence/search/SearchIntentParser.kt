package com.music.echo.notune.intelligence.search

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchIntentParser @Inject constructor() {

    fun parseIntent(query: String): SearchIntent {
        val q = query.trim()
        val lower = q.lowercase()

        var mood: String? = null
        var genre: String? = null
        var language: String? = null
        var era: String? = null
        var artist: String? = null
        var minEnergy: Float? = null
        var maxEnergy: Float? = null
        var isUnderrated = false
        var isDiscovery = false

        // 1. Language detection
        when {
            lower.contains("tamil") -> language = "Tamil"
            lower.contains("hindi") || lower.contains("bollywood") -> language = "Hindi"
            lower.contains("malayalam") -> language = "Malayalam"
            lower.contains("telugu") -> language = "Telugu"
            lower.contains("korean") || lower.contains("k-pop") || lower.contains("kpop") -> language = "Korean"
            lower.contains("english") -> language = "English"
        }

        // 2. Era detection
        when {
            lower.contains("2000s") || lower.contains("2000's") -> era = "2000s"
            lower.contains("90s") || lower.contains("1990s") -> era = "90s"
            lower.contains("80s") || lower.contains("1980s") -> era = "80s"
            lower.contains("2010s") -> era = "2010s"
        }

        // 3. Mood & Energy detection
        when {
            lower.contains("peaceful") || lower.contains("relax") || lower.contains("chill") || lower.contains("studying") || lower.contains("study") -> {
                mood = "Calm"
                maxEnergy = 0.45f
            }
            lower.contains("energetic") || lower.contains("workout") || lower.contains("gym") || lower.contains("upbeat") -> {
                mood = "Energetic"
                minEnergy = 0.75f
            }
            lower.contains("sad") || lower.contains("melancholic") || lower.contains("lonely") -> {
                mood = "Sad"
                maxEnergy = 0.40f
            }
            lower.contains("night drive") || lower.contains("driving") -> {
                mood = "Night Drive"
                minEnergy = 0.50f
                maxEnergy = 0.80f
            }
            lower.contains("happier") || lower.contains("happy") -> {
                mood = "Happy"
                minEnergy = 0.60f
            }
        }

        // 4. Underrated / Discovery
        if (lower.contains("underrated") || lower.contains("hidden gem")) {
            isUnderrated = true
        }
        if (lower.contains("haven't heard") || lower.contains("something new") || lower.contains("discovery")) {
            isDiscovery = true
        }

        // 5. Artist extraction heuristics
        when {
            lower.contains("ar rahman") || lower.contains("a.r. rahman") || lower.contains("rahman") -> artist = "A.R. Rahman"
            lower.contains("anirudh") -> artist = "Anirudh Ravichander"
            lower.contains("ilaiyaraaja") || lower.contains("ilayaraja") -> artist = "Ilaiyaraaja"
            lower.contains("taylor swift") -> artist = "Taylor Swift"
            lower.contains("kendrick") -> artist = "Kendrick Lamar"
        }

        val description = buildString {
            append("Parsed Intent: ")
            mood?.let { append("Mood=$it ") }
            language?.let { append("Lang=$it ") }
            era?.let { append("Era=$it ") }
            artist?.let { append("Artist=$it ") }
            if (isUnderrated) append("(Underrated) ")
            if (isDiscovery) append("(Discovery) ")
            if (isEmpty()) append("Direct Keyword Match")
        }

        return SearchIntent(
            rawQuery = q,
            targetMood = mood,
            targetGenre = genre,
            targetLanguage = language,
            targetEra = era,
            targetArtist = artist,
            minEnergy = minEnergy,
            maxEnergy = maxEnergy,
            isUnderratedRequest = isUnderrated,
            isDiscoveryRequest = isDiscovery,
            intentDescription = description.trim()
        )
    }
}
