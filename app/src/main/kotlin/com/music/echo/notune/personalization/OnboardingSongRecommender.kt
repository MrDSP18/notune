package com.music.echo.notune.personalization

import com.music.echo.notune.personalization.model.TasteProfile
import echo.music.iad1tya.models.MediaMetadata
import javax.inject.Inject
import javax.inject.Singleton

data class OnboardingRecommendedTrack(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val matchReason: String,
    val matchedGenre: String,
    val matchedLanguage: String,
    val matchedEra: String
)

@Singleton
class OnboardingSongRecommender @Inject constructor() {

    fun generateOnboardingMix(profile: TasteProfile): List<OnboardingRecommendedTrack> {
        val selectedGenres = if (profile.favoriteGenres.isNotEmpty()) profile.favoriteGenres.toList() else listOf("Pop", "Electronic")
        val selectedLanguages = if (profile.musicLanguages.isNotEmpty()) profile.musicLanguages.toList() else listOf("English")
        val selectedArtists = if (profile.favoriteArtists.isNotEmpty()) profile.favoriteArtists.map { it.name } else listOf("Top Artist")
        val selectedEras = if (profile.favoriteEras.isNotEmpty()) profile.favoriteEras.toList() else listOf("2020s")
        val selectedMoods = if (profile.preferredMoods.isNotEmpty()) profile.preferredMoods.toList() else listOf("Chill")

        val recommendations = mutableListOf<OnboardingRecommendedTrack>()

        var trackCounter = 1
        for (artist in selectedArtists.take(4)) {
            for (genre in selectedGenres.take(3)) {
                val lang = selectedLanguages[(trackCounter - 1) % selectedLanguages.size]
                val era = selectedEras[(trackCounter - 1) % selectedEras.size]
                val mood = selectedMoods[(trackCounter - 1) % selectedMoods.size]

                recommendations.add(
                    OnboardingRecommendedTrack(
                        id = "onboarding_rec_$trackCounter",
                        title = "$artist's $mood $genre Anthem",
                        artist = artist,
                        album = "$artist Onboarding Essentials",
                        matchReason = "Matched from your onboarding selections ($artist • $genre • $lang • $era)",
                        matchedGenre = genre,
                        matchedLanguage = lang,
                        matchedEra = era
                    )
                )
                trackCounter++
                if (recommendations.size >= 20) break
            }
            if (recommendations.size >= 20) break
        }

        // Fillers if needed
        while (recommendations.size < 12) {
            val genre = selectedGenres.first()
            val lang = selectedLanguages.first()
            val era = selectedEras.first()
            recommendations.add(
                OnboardingRecommendedTrack(
                    id = "onboarding_rec_$trackCounter",
                    title = "Essential $genre Mix #$trackCounter",
                    artist = "Vibe Curator",
                    album = "$genre $lang Top Hits",
                    matchReason = "Matched from your onboarding $genre & $lang choices",
                    matchedGenre = genre,
                    matchedLanguage = lang,
                    matchedEra = era
                )
            )
            trackCounter++
        }

        return recommendations
    }
}
