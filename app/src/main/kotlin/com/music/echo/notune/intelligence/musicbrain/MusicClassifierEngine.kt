package com.music.echo.notune.intelligence.musicbrain

import javax.inject.Inject
import javax.inject.Singleton

data class MusicFeatureVector(
    val language: String = "English",
    val genre: String = "Pop",
    val subgenre: String? = null,
    val era: String = "2020s",
    val energy: Float = 0.6f,
    val tempoBpm: Float = 110f,
    val danceability: Float = 0.65f,
    val acousticness: Float = 0.35f,
    val instrumentalness: Float = 0.10f,
    val vocalType: String = "Mixed",
    val familiarityScore: Float = 0.70f,
    val noveltyScore: Float = 0.30f,
    val popularityScore: Float = 0.75f,
    val isLive: Boolean = false,
    val isRemix: Boolean = false,
    val isExplicit: Boolean = false,
    val confidence: Float = 0.88f
)

@Singleton
class MusicClassifierEngine @Inject constructor() {

    fun extractFeatures(title: String, artist: String): MusicFeatureVector {
        val combined = "$title $artist".lowercase()

        val language = when {
            combined.contains("tamil") -> "Tamil"
            combined.contains("hindi") || combined.contains("bollywood") -> "Hindi"
            combined.contains("malayalam") -> "Malayalam"
            combined.contains("telugu") -> "Telugu"
            combined.contains("korean") || combined.contains("k-pop") -> "Korean"
            else -> "English"
        }

        val genre = when {
            combined.contains("hip hop") || combined.contains("rap") || combined.contains("trap") -> "Hip-Hop"
            combined.contains("rock") || combined.contains("metal") -> "Rock"
            combined.contains("edm") || combined.contains("techno") || combined.contains("house") -> "Electronic"
            combined.contains("melody") || combined.contains("love") || combined.contains("romantic") -> "Melody"
            combined.contains("classical") || combined.contains("carnatic") -> "Classical"
            else -> "Pop"
        }

        val era = when {
            combined.contains("90s") || combined.contains("1990") -> "90s"
            combined.contains("2000s") || combined.contains("2000") -> "2000s"
            combined.contains("2010s") || combined.contains("2010") -> "2010s"
            combined.contains("80s") || combined.contains("1980") -> "80s"
            else -> "2020s"
        }

        val energy = when {
            combined.contains("fast") || combined.contains("upbeat") || combined.contains("workout") || combined.contains("gym") || combined.contains("techno") -> 0.85f
            combined.contains("slow") || combined.contains("sad") || combined.contains("relax") || combined.contains("sleep") -> 0.30f
            else -> 0.60f
        }

        val isLive = combined.contains("live") || combined.contains("concert")
        val isRemix = combined.contains("remix") || combined.contains("mix")

        return MusicFeatureVector(
            language = language,
            genre = genre,
            era = era,
            energy = energy,
            tempoBpm = if (energy > 0.7f) 130f else 95f,
            isLive = isLive,
            isRemix = isRemix,
            confidence = 0.90f
        )
    }
}
