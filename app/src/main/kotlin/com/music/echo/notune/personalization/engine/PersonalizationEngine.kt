package com.music.echo.notune.personalization.engine

import com.music.echo.notune.personalization.model.TasteProfile
import com.music.echo.notune.personalization.repository.TasteProfileRepository
import echo.music.iad1tya.db.MusicDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

data class PersonalizationSignals(
    val topLanguages: List<String>,
    val favoriteArtistNames: List<String>,
    val favoriteGenres: List<String>,
    val preferredEras: List<String>,
    val preferredMoods: List<String>,
    val familiarRatio: Float,
    val isNewUser: Boolean
)

@Singleton
class PersonalizationEngine @Inject constructor(
    private val tasteProfileRepository: TasteProfileRepository,
    private val database: MusicDatabase
) {

    suspend fun getPersonalizationSignals(): PersonalizationSignals = withContext(Dispatchers.IO) {
        val profile = tasteProfileRepository.getTasteProfileOnce()
        val topSongsCount = try { database.topSongs(1).first().size } catch (_: Exception) { 0 }
        val isNewUser = topSongsCount == 0

        PersonalizationSignals(
            topLanguages = profile.musicLanguages.toList(),
            favoriteArtistNames = profile.favoriteArtists.map { it.name },
            favoriteGenres = profile.favoriteGenres.toList(),
            preferredEras = profile.preferredEras.toList(),
            preferredMoods = profile.preferredMoods.toList(),
            familiarRatio = profile.discoveryPreference.familiarRatio,
            isNewUser = isNewUser
        )
    }

    suspend fun getInitialCandidateQueries(limit: Int = 10): List<String> = withContext(Dispatchers.IO) {
        val profile = tasteProfileRepository.getTasteProfileOnce()
        val queries = mutableListOf<String>()

        // 1. Favorite artists
        for (artist in profile.favoriteArtists.take(4)) {
            queries.add(artist.name)
        }

        // 2. Favorite genres combined with languages
        val lang = profile.musicLanguages.firstOrNull() ?: ""
        for (genre in profile.favoriteGenres.take(4)) {
            val query = if (lang.isNotEmpty() && !genre.contains(lang, ignoreCase = true)) {
                "$lang $genre"
            } else {
                genre
            }
            queries.add(query)
        }

        // 3. Preferred eras
        for (era in profile.preferredEras.take(2)) {
            if (lang.isNotEmpty()) {
                queries.add("$lang $era Hits")
            } else {
                queries.add("$era Hits")
            }
        }

        queries.distinct().take(limit)
    }

    suspend fun calculateArtistAffinity(artistName: String): Float = withContext(Dispatchers.IO) {
        val profile = tasteProfileRepository.getTasteProfileOnce()
        val isExplicitFavorite = profile.favoriteArtists.any { it.name.equals(artistName, ignoreCase = true) }
        if (isExplicitFavorite) return@withContext 1.0f

        // Check if artist matches preferred genres/languages
        var score = 0.2f
        for (lang in profile.musicLanguages) {
            if (artistName.contains(lang, ignoreCase = true)) score += 0.3f
        }
        score.coerceIn(0f, 1f)
    }

    suspend fun explainRecommendation(songTitle: String, artistName: String, genre: String? = null): List<String> = withContext(Dispatchers.IO) {
        val profile = tasteProfileRepository.getTasteProfileOnce()
        val reasons = mutableListOf<String>()

        if (profile.favoriteArtists.any { it.name.equals(artistName, ignoreCase = true) }) {
            reasons.add("You selected $artistName as a favorite artist")
        }

        if (genre != null && profile.favoriteGenres.any { it.equals(genre, ignoreCase = true) }) {
            reasons.add("Matches your interest in $genre music")
        }

        for (lang in profile.musicLanguages) {
            if (songTitle.contains(lang, ignoreCase = true) || artistName.contains(lang, ignoreCase = true)) {
                reasons.add("Matches your preferred $lang music language")
            }
        }

        if (reasons.isEmpty()) {
            reasons.add("Based on your ${profile.discoveryPreference.label.lowercase()} discovery preference")
        }

        reasons
    }
}
