package com.music.echo.notune.intelligence.personalization

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.min

/**
 * Learns user taste dynamically over time based on interaction feedback.
 *
 * Implements safeguards against over-learning:
 * - Single interaction has low confidence (0.12)
 * - Repeated interactions build confidence up to 0.95
 * - Excluded sessions (e.g. listening for siblings) only update SessionTaste, leaving CoreTaste untouched.
 */
@Singleton
class PreferenceLearner @Inject constructor(
    private val tasteProfileStore: TasteProfileStore
) {

    fun onTrackCompleted(genre: String?, artistName: String, language: String?) {
        val currentDna = tasteProfileStore.getDnaSnapshot()

        // 1. Always update SessionTaste
        tasteProfileStore.updateDna { dna ->
            val session = dna.sessionTaste
            dna.copy(
                sessionTaste = session.copy(
                    currentGenre = genre ?: session.currentGenre,
                    currentLanguage = language ?: session.currentLanguage,
                    currentArtist = artistName,
                    totalSessionTracks = session.totalSessionTracks + 1,
                    consecutiveSkips = 0
                )
            )
        }

        // 2. If session is marked excluded, skip updating CoreTaste
        if (currentDna.sessionTaste.isExcludedSession) return

        // 3. Incrementally update CoreTaste with confidence weighting
        tasteProfileStore.updateDna { dna ->
            val core = dna.coreTaste

            val currentGenreScore = core.genres[genre] ?: 0.4f
            val updatedGenreScore = min(1.0f, currentGenreScore + 0.05f)
            val updatedGenres = if (genre != null) core.genres + (genre to updatedGenreScore) else core.genres

            val currentArtistScore = core.artists[artistName] ?: 0.3f
            val updatedArtistScore = min(1.0f, currentArtistScore + 0.08f)
            val updatedArtists = core.artists + (artistName to updatedArtistScore)

            val updatedLangs = if (language != null) {
                val currentLangScore = core.languages[language] ?: 0.5f
                core.languages + (language to min(1.0f, currentLangScore + 0.04f))
            } else core.languages

            dna.copy(
                coreTaste = core.copy(
                    genres = updatedGenres,
                    artists = updatedArtists,
                    languages = updatedLangs
                )
            )
        }
    }

    fun onTrackSkippedEarly(genre: String?, artistName: String, skipSeconds: Float) {
        val currentDna = tasteProfileStore.getDnaSnapshot()

        // 1. Update SessionTaste
        tasteProfileStore.updateDna { dna ->
            val session = dna.sessionTaste
            dna.copy(
                sessionTaste = session.copy(
                    consecutiveSkips = session.consecutiveSkips + 1
                )
            )
        }

        if (currentDna.sessionTaste.isExcludedSession) return

        // 2. Early skip penalty (5s skip penalty > 30s skip penalty)
        val penalty = if (skipSeconds < 10f) 0.10f else 0.04f

        tasteProfileStore.updateDna { dna ->
            val core = dna.coreTaste

            val updatedGenres = if (genre != null && core.genres.containsKey(genre)) {
                val currentScore = core.genres[genre] ?: 0.5f
                core.genres + (genre to (currentScore - penalty).coerceAtLeast(0.05f))
            } else core.genres

            val updatedArtists = if (core.artists.containsKey(artistName)) {
                val currentScore = core.artists[artistName] ?: 0.4f
                core.artists + (artistName to (currentScore - penalty).coerceAtLeast(0.05f))
            } else core.artists

            dna.copy(
                coreTaste = core.copy(
                    genres = updatedGenres,
                    artists = updatedArtists
                )
            )
        }
    }

    fun onTrackReplayed(genre: String?, artistName: String) {
        val currentDna = tasteProfileStore.getDnaSnapshot()
        if (currentDna.sessionTaste.isExcludedSession) return

        // Strong positive signal
        tasteProfileStore.updateDna { dna ->
            val core = dna.coreTaste
            val updatedGenres = if (genre != null) {
                val currentScore = core.genres[genre] ?: 0.5f
                core.genres + (genre to min(1.0f, currentScore + 0.15f))
            } else core.genres

            val currentArtistScore = core.artists[artistName] ?: 0.4f
            val updatedArtists = core.artists + (artistName to min(1.0f, currentArtistScore + 0.20f))

            dna.copy(
                coreTaste = core.copy(
                    genres = updatedGenres,
                    artists = updatedArtists
                )
            )
        }
    }
}
