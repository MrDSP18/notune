package com.music.echo.notune.ai

import androidx.compose.runtime.Immutable
import com.music.echo.notune.personalization.repository.TasteProfileRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Immutable
data class MusicContext(
    val currentTrackId: String? = null,
    val currentTrackTitle: String? = null,
    val artistName: String? = null,
    val albumName: String? = null,
    val genre: String? = null,
    val language: String? = null,
    val releaseYear: Int? = null,
    val energyLevel: Float = 0.5f,
    val userPlayCount: Int = 0,
    val userSkipCount: Int = 0,
    val isFavorite: Boolean = false,
    val preferredLanguages: List<String> = emptyList(),
    val favoriteGenres: List<String> = emptyList()
)

@Singleton
class MusicContextEngine @Inject constructor(
    private val tasteProfileRepository: TasteProfileRepository
) {
    suspend fun getCurrentContext(
        trackId: String? = null,
        title: String? = null,
        artist: String? = null,
        genre: String? = null,
        language: String? = null
    ): MusicContext {
        val profile = tasteProfileRepository.tasteProfile.first()

        return MusicContext(
            currentTrackId = trackId,
            currentTrackTitle = title,
            artistName = artist,
            genre = genre,
            language = language,
            preferredLanguages = profile.musicLanguages.toList(),
            favoriteGenres = profile.favoriteGenres.toList()
        )
    }
}
