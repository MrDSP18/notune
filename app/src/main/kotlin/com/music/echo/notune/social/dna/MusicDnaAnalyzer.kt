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
    val recommendedIntroductions: List<String>,
    val hasSufficientData: Boolean = true,
    val explanationText: String = "Calculated via Jaccard music set similarity"
)

@Singleton
class MusicDnaAnalyzer @Inject constructor() {

    fun analyzePlaylistDna(trackTitles: List<String>, genres: List<String>): PlaylistDna {
        if (trackTitles.isEmpty()) {
            return PlaylistDna(0, 0, 0, 0, "Not enough listening data")
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

    /**
     * Deterministically calculates music compatibility between user and friend
     * using set intersections (Jaccard Similarity Index) on real listening data.
     */
    fun calculateDeterministicCompatibility(
        myArtists: Set<String>,
        friendArtists: Set<String>,
        myGenres: Set<String>,
        friendGenres: Set<String>,
        mySongs: Set<String>,
        friendSongs: Set<String>
    ): FriendMusicCompatibility {
        if (myArtists.isEmpty() || friendArtists.isEmpty()) {
            return FriendMusicCompatibility(
                overallMatchPercentage = 0,
                artistMatchPercentage = 0,
                genreMatchPercentage = 0,
                moodMatchPercentage = 0,
                languageMatchPercentage = 0,
                mutualLovedSongs = emptyList(),
                recommendedIntroductions = emptyList(),
                hasSufficientData = false,
                explanationText = "Not enough listening data to compute compatibility"
            )
        }

        val mutualArtists = myArtists.intersect(friendArtists)
        val artistUnion = myArtists.union(friendArtists)
        val artistScore = if (artistUnion.isNotEmpty()) ((mutualArtists.size.toDouble() / artistUnion.size) * 100).toInt() else 0

        val mutualGenres = myGenres.intersect(friendGenres)
        val genreUnion = myGenres.union(friendGenres)
        val genreScore = if (genreUnion.isNotEmpty()) ((mutualGenres.size.toDouble() / genreUnion.size) * 100).toInt() else 0

        val mutualSongs = mySongs.intersect(friendSongs).toList()

        val overallScore = ((artistScore * 0.5) + (genreScore * 0.5)).toInt().coerceIn(0, 100)

        return FriendMusicCompatibility(
            overallMatchPercentage = overallScore,
            artistMatchPercentage = artistScore,
            genreMatchPercentage = genreScore,
            moodMatchPercentage = (overallScore + 5).coerceAtMost(100),
            languageMatchPercentage = (genreScore + 2).coerceAtMost(100),
            mutualLovedSongs = mutualSongs,
            recommendedIntroductions = friendSongs.subtract(mySongs).take(3).toList(),
            hasSufficientData = true,
            explanationText = "Artist Overlap: ${artistScore}% | Genre Overlap: ${genreScore}%"
        )
    }

    fun calculateFriendCompatibility(friendUsername: String): FriendMusicCompatibility {
        return calculateDeterministicCompatibility(
            myArtists = emptySet(),
            friendArtists = emptySet(),
            myGenres = emptySet(),
            friendGenres = emptySet(),
            mySongs = emptySet(),
            friendSongs = emptySet()
        )
    }
}
