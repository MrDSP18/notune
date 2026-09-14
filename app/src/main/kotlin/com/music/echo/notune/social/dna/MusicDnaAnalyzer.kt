package com.music.echo.notune.social.dna

import javax.inject.Inject
import javax.inject.Singleton

data class PlaylistDna(
    val heartbreakPercentage: Int,
    val nightPercentage: Int,
    val energyPercentage: Int,
    val indiePercentage: Int,
    val primaryMood: String
)

data class FriendMusicCompatibility(
    val overallMatchPercentage: Int,
    val artistMatchPercentage: Int,
    val genreMatchPercentage: Int,
    val moodMatchPercentage: Int,
    val languageMatchPercentage: Int,
    val mutualLovedSongs: List<String>,
    val recommendedIntroductions: List<String>
)

@Singleton
class MusicDnaAnalyzer @Inject constructor() {

    fun analyzePlaylistDna(trackTitles: List<String>, genres: List<String>): PlaylistDna {
        if (trackTitles.isEmpty()) {
            return PlaylistDna(25, 40, 60, 30, "Chill Vibe")
        }

        var heartbreak = 20
        var night = 35
        var energy = 50
        var indie = 30

        for (title in trackTitles) {
            val t = title.lowercase()
            if (t.contains("sad") || t.contains("break") || t.contains("lonely") || t.contains("love") || t.contains("heart")) heartbreak += 15
            if (t.contains("night") || t.contains("dark") || t.contains("sleep") || t.contains("midnight") || t.contains("moon")) night += 18
            if (t.contains("dance") || t.contains("party") || t.contains("rock") || t.contains("fire") || t.contains("beat")) energy += 15
            if (t.contains("indie") || t.contains("acoustic") || t.contains("folk") || t.contains("chill")) indie += 15
        }

        val hb = heartbreak.coerceIn(10, 95)
        val nt = night.coerceIn(15, 98)
        val en = energy.coerceIn(10, 99)
        val ind = indie.coerceIn(10, 90)

        val primaryMood = when {
            hb > 70 -> "💔 Heartbreak Melancholy"
            nt > 70 -> "🌙 Midnight Chill"
            en > 70 -> "🔥 High Energy Rush"
            else -> "✨ Curated Audiophile Mix"
        }

        return PlaylistDna(hb, nt, en, ind, primaryMood)
    }

    fun calculateFriendCompatibility(friendUsername: String): FriendMusicCompatibility {
        val hash = friendUsername.hashCode()
        val overall = 75 + (hash % 20).let { if (it < 0) -it else it }
        val artistMatch = (overall + 5).coerceAtMost(98)
        val genreMatch = (overall - 3).coerceAtLeast(60)
        val moodMatch = (overall + 2).coerceAtMost(95)
        val languageMatch = (overall - 8).coerceAtLeast(55)

        return FriendMusicCompatibility(
            overallMatchPercentage = overall,
            artistMatchPercentage = artistMatch,
            genreMatchPercentage = genreMatch,
            moodMatchPercentage = moodMatch,
            languageMatchPercentage = languageMatch,
            mutualLovedSongs = listOf("Until I Found You", "Kesariya", "Starboy", "Blinding Lights"),
            recommendedIntroductions = listOf("Arabic Kuthu", "A Sky Full of Stars", "Levitating")
        )
    }
}
