package com.music.echo.notune.intelligence.personalization

import echo.music.iad1tya.db.MusicDatabase
import com.music.echo.notune.personalization.repository.TasteProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

/**
 * Initializes User DNA directly from existing Room database history.
 * Ensures existing NØTUNE users start with an instant, rich taste DNA profile
 * without requiring any manual setup or questionnaires.
 */
@Singleton
open class MusicDnaInitializer @Inject constructor(
    private val database: MusicDatabase?,
    private val tasteProfileRepository: TasteProfileRepository?,
    private val tasteProfileStore: TasteProfileStore?
) {

    open suspend fun initializeDnaFromExistingHistory() = withContext(Dispatchers.IO) {
        val db = database ?: return@withContext
        val repo = tasteProfileRepository ?: return@withContext
        val store = tasteProfileStore ?: return@withContext
        try {
            val topArtists = db.allArtistsByPlayTime().first().take(15)
            val topSongs = db.topSongs(30).first()
            val savedTasteProfile = repo.getTasteProfileOnce()

            val artistMap = mutableMapOf<String, Float>()
            topArtists.forEachIndexed { index, artistWithPlayTime ->
                val weight = (1.0f - (index * 0.05f)).coerceIn(0.2f, 1.0f)
                artistMap[artistWithPlayTime.artist.name] = weight
            }
            savedTasteProfile.favoriteArtists.forEach { artist ->
                artistMap[artist.name] = 1.0f
            }

            val genreMap = mutableMapOf<String, Float>()
            savedTasteProfile.favoriteGenres.forEach { genre ->
                genreMap[genre] = 0.9f
            }

            val languageMap = mutableMapOf<String, Float>()
            savedTasteProfile.musicLanguages.forEach { lang ->
                languageMap[lang] = 0.95f
            }
            if (languageMap.isEmpty()) {
                languageMap["Tamil"] = 0.90f
                languageMap["English"] = 0.75f
            }

            val eraMap = mutableMapOf<String, Float>()
            savedTasteProfile.preferredEras.forEach { era ->
                eraMap[era] = 0.85f
            }

            store.updateDna { currentDna ->
                currentDna.copy(
                    coreTaste = currentDna.coreTaste.copy(
                        artists = if (artistMap.isNotEmpty()) artistMap else currentDna.coreTaste.artists,
                        genres = if (genreMap.isNotEmpty()) genreMap else currentDna.coreTaste.genres,
                        languages = if (languageMap.isNotEmpty()) languageMap else currentDna.coreTaste.languages,
                        eras = if (eraMap.isNotEmpty()) eraMap else currentDna.coreTaste.eras
                    ),
                    discoveryProfile = currentDna.discoveryProfile.copy(
                        familiarMusicPreference = savedTasteProfile.discoveryPreference.familiarRatio
                    )
                )
            }

            Timber.d("Successfully initialized User DNA from existing database history with ${artistMap.size} artists and ${genreMap.size} genres.")
        } catch (e: Exception) {
            Timber.w("Could not initialize User DNA from existing history: ${e.message}")
        }
    }
}
