package echo.music.iad1tya.notune.intelligence.enrichment

import echo.music.iad1tya.notune.intelligence.knowledge.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Field-level merger that combines metadata from multiple providers.
 * Merges title, artists, credits, movie, cast, crew, artwork, and related tracks without overwriting verified fields.
 */
@Singleton
class FieldMerger @Inject constructor() {

    /**
     * Merge secondary provider song details into primary song details.
     */
    fun mergeSongDetails(primary: SongDetails, secondary: SongDetails): SongDetails {
        val mergedTitle = if (isFieldValid(primary.title)) primary.title else secondary.title
        val mergedOriginalTitle = primary.originalTitle ?: secondary.originalTitle

        val mergedArtists = mergePeople(primary.artists, secondary.artists)
        val mergedFeatured = mergePeople(primary.featuredArtists, secondary.featuredArtists)

        val mergedCredits = SongCredits(
            singers = mergePeople(primary.credits.singers, secondary.credits.singers),
            composers = mergePeople(primary.credits.composers, secondary.credits.composers),
            lyricists = mergePeople(primary.credits.lyricists, secondary.credits.lyricists),
            producers = mergePeople(primary.credits.producers, secondary.credits.producers),
            label = primary.credits.label ?: secondary.credits.label
        )

        val mergedAlbumId = primary.albumId ?: secondary.albumId
        val mergedAlbumTitle = primary.albumTitle ?: secondary.albumTitle
        val mergedMovieId = primary.movieId ?: secondary.movieId
        val mergedMovieTitle = primary.movieTitle ?: secondary.movieTitle

        val mergedLanguage = primary.language ?: secondary.language
        val mergedLanguages = (primary.languages + secondary.languages).distinct()
        val mergedGenre = primary.genre ?: secondary.genre
        val mergedSubGenre = primary.subGenre ?: secondary.subGenre
        val mergedMood = primary.mood ?: secondary.mood

        val mergedEnergy = primary.energy ?: secondary.energy
        val mergedBpm = primary.bpm ?: secondary.bpm
        val mergedDuration = if (primary.durationMs > 0) primary.durationMs else secondary.durationMs

        val mergedReleaseDate = primary.releaseDate ?: secondary.releaseDate
        val mergedYear = primary.year ?: secondary.year
        val mergedArtwork = primary.artwork ?: secondary.artwork
        val mergedLyricsAvailable = primary.lyricsAvailable || secondary.lyricsAvailable
        val mergedAudioQuality = primary.audioQuality ?: secondary.audioQuality

        val mergedRelated = (primary.relatedSongs + secondary.relatedSongs).distinctBy { it.songId }

        val highestSource = if (primary.source.ordinal <= secondary.source.ordinal) primary.source else secondary.source

        return primary.copy(
            title = mergedTitle,
            originalTitle = mergedOriginalTitle,
            artists = mergedArtists,
            featuredArtists = mergedFeatured,
            credits = mergedCredits,
            albumId = mergedAlbumId,
            albumTitle = mergedAlbumTitle,
            movieId = mergedMovieId,
            movieTitle = mergedMovieTitle,
            language = mergedLanguage,
            languages = mergedLanguages,
            genre = mergedGenre,
            subGenre = mergedSubGenre,
            mood = mergedMood,
            energy = mergedEnergy,
            bpm = mergedBpm,
            durationMs = mergedDuration,
            releaseDate = mergedReleaseDate,
            year = mergedYear,
            artwork = mergedArtwork,
            lyricsAvailable = mergedLyricsAvailable,
            audioQuality = mergedAudioQuality,
            source = highestSource,
            relatedSongs = mergedRelated
        )
    }

    /**
     * Merge movie details from primary and secondary sources.
     */
    fun mergeMovieDetails(primary: MovieDetails, secondary: MovieDetails): MovieDetails {
        val mergedDirectors = mergePeople(primary.directors, secondary.directors)
        val mergedMusicDirectors = mergePeople(primary.musicDirectors, secondary.musicDirectors)
        val mergedProducers = mergePeople(primary.producers, secondary.producers)

        val mergedLeadActors = mergePeople(primary.leadActors, secondary.leadActors)
        val mergedLeadActresses = mergePeople(primary.leadActresses, secondary.leadActresses)
        val mergedCast = (primary.cast + secondary.cast).distinctBy { it.person.personId }
        val mergedCrew = (primary.crew + secondary.crew).distinctBy { it.person.personId }

        val mergedGenres = (primary.genres + secondary.genres).distinct()
        val mergedPoster = primary.posterArtwork ?: secondary.posterArtwork
        val mergedHero = primary.heroBackdropArtwork ?: secondary.heroBackdropArtwork

        return primary.copy(
            directors = mergedDirectors,
            musicDirectors = mergedMusicDirectors,
            producers = mergedProducers,
            leadActors = mergedLeadActors,
            leadActresses = mergedLeadActresses,
            cast = mergedCast,
            crew = mergedCrew,
            genres = mergedGenres,
            posterArtwork = mergedPoster,
            heroBackdropArtwork = mergedHero,
            description = primary.description ?: secondary.description
        )
    }

    private fun mergePeople(primary: List<PersonDetails>, secondary: List<PersonDetails>): List<PersonDetails> {
        val combined = primary + secondary
        return combined.distinctBy { it.personId.ifBlank { it.name.lowercase() } }
    }

    private fun isFieldValid(field: String?): Boolean {
        return !field.isNull_or_blank() && !field.equals("Information unavailable", ignoreCase = true)
    }

    private fun String?.isNull_or_blank(): Boolean = this == null || this.isBlank()
}
