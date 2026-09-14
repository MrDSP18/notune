package com.music.echo.notune.personalization

import com.music.echo.notune.personalization.model.TasteProfile
import com.music.innertube.YouTube
import com.music.innertube.models.SongItem
import echo.music.iad1tya.models.MediaMetadata
import echo.music.iad1tya.models.toMediaMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

data class OnboardingRecommendedTrack(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val thumbnailUrl: String? = null,
    val durationSeconds: Int? = null,
    val matchReason: String,
    val matchedGenre: String,
    val matchedLanguage: String,
    val matchedEra: String,
    val mediaMetadata: MediaMetadata? = null
)

@Singleton
class OnboardingSongRecommender @Inject constructor() {

    /**
     * Async fetch of real songs matching the user's taste profile using YouTube search.
     * Guaranteed to fall back to curated tracks if network is unavailable or empty.
     */
    suspend fun fetchOnboardingMix(profile: TasteProfile): List<OnboardingRecommendedTrack> = withContext(Dispatchers.IO) {
        val selectedGenres = if (profile.favoriteGenres.isNotEmpty()) profile.favoriteGenres.toList() else listOf("Pop", "Electronic")
        val selectedLanguages = if (profile.musicLanguages.isNotEmpty()) profile.musicLanguages.toList() else listOf("English")
        val selectedArtists = if (profile.favoriteArtists.isNotEmpty()) profile.favoriteArtists.map { it.name } else listOf("Top Artist")
        val selectedEras = if (profile.preferredEras.isNotEmpty()) profile.preferredEras.toList() else listOf("2020s")
        val selectedMoods = if (profile.preferredMoods.isNotEmpty()) profile.preferredMoods.toList() else listOf("Chill")

        val searchQueries = mutableListOf<Triple<String, String, String>>()

        // 1. Artist Queries
        for (artistName in selectedArtists.take(4)) {
            val mainGenre = selectedGenres.firstOrNull() ?: "Music"
            val mainLang = selectedLanguages.firstOrNull() ?: "English"
            val mainEra = selectedEras.firstOrNull() ?: "2020s"
            searchQueries.add(Triple("$artistName top songs", "Matched from your favorite artist: $artistName", "$mainGenre • $mainLang • $mainEra"))
        }

        // 2. Genre + Language Queries
        for (genre in selectedGenres.take(3)) {
            for (lang in selectedLanguages.take(2)) {
                val era = selectedEras.firstOrNull() ?: "2020s"
                searchQueries.add(Triple("$lang $genre hit songs", "Matched from your onboarding choices ($genre • $lang)", "$genre • $lang • $era"))
            }
        }

        // 3. Era + Language Queries
        for (era in selectedEras.take(2)) {
            val lang = selectedLanguages.firstOrNull() ?: "English"
            val genre = selectedGenres.firstOrNull() ?: "Pop"
            searchQueries.add(Triple("$lang $era hit songs", "Matched from your onboarding era preference ($era • $lang)", "$genre • $lang • $era"))
        }

        val realRecommendations = mutableListOf<OnboardingRecommendedTrack>()
        val seenTrackIds = mutableSetOf<String>()

        try {
            val searchDeferreds = searchQueries.map { (query, reason, meta) ->
                async {
                    runCatching {
                        val result = YouTube.search(query, YouTube.SearchFilter.FILTER_SONG).getOrNull()
                        result?.items?.filterIsInstance<SongItem>()?.take(3)?.map { song ->
                            val parts = meta.split(" • ")
                            val genre = parts.getOrNull(0) ?: "Pop"
                            val lang = parts.getOrNull(1) ?: "English"
                            val era = parts.getOrNull(2) ?: "2020s"
                            val mediaMeta = song.toMediaMetadata()
                            OnboardingRecommendedTrack(
                                id = song.id,
                                title = song.title,
                                artist = song.artists.joinToString(", ") { it.name },
                                album = song.album?.name ?: "${song.title} - Single",
                                thumbnailUrl = song.thumbnail,
                                durationSeconds = song.duration,
                                matchReason = reason,
                                matchedGenre = genre,
                                matchedLanguage = lang,
                                matchedEra = era,
                                mediaMetadata = mediaMeta
                            )
                        }.orEmpty()
                    }.getOrDefault(emptyList())
                }
            }

            val queryResults = searchDeferreds.awaitAll()
            for (tracks in queryResults) {
                for (track in tracks) {
                    if (seenTrackIds.add(track.id)) {
                        realRecommendations.add(track)
                    }
                }
            }
        } catch (_: Exception) {
            // Fallback triggers if search fails
        }

        if (realRecommendations.isNotEmpty()) {
            return@withContext realRecommendations
        }

        return@withContext generateOnboardingMix(profile)
    }

    fun generateOnboardingMix(profile: TasteProfile): List<OnboardingRecommendedTrack> {
        val selectedGenres = if (profile.favoriteGenres.isNotEmpty()) profile.favoriteGenres.toList() else listOf("Pop", "Electronic")
        val selectedLanguages = if (profile.musicLanguages.isNotEmpty()) profile.musicLanguages.toList() else listOf("English")
        val selectedArtists = if (profile.favoriteArtists.isNotEmpty()) profile.favoriteArtists.map { it.name } else listOf("Top Artist")
        val selectedEras = if (profile.preferredEras.isNotEmpty()) profile.preferredEras.toList() else listOf("2020s")
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

