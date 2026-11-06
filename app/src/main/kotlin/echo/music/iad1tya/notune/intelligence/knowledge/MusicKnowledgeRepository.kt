package echo.music.iad1tya.notune.intelligence.knowledge

import echo.music.iad1tya.notune.ai.adaptive.AdaptiveTrackContext
import echo.music.iad1tya.notune.intelligence.enrichment.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Provider-agnostic knowledge repository & metadata resolver.
 * Resolves local, backend, and provider metadata into structured models.
 */
@Singleton
class MusicKnowledgeRepository @Inject constructor(
    private val metadataResolver: MusicMetadataResolver,
    private val cacheManager: MetadataCacheManager
) {

    private val _knowledgeState = MutableStateFlow(MusicKnowledgeState())
    val knowledgeState: StateFlow<MusicKnowledgeState> = _knowledgeState.asStateFlow()

    private val songCache = mutableMapOf<String, SongDetails>()
    private val artistCache = mutableMapOf<String, ArtistDetails>()
    private val albumCache = mutableMapOf<String, AlbumDetails>()
    private val movieCache = mutableMapOf<String, MovieDetails>()

    /**
     * Resolve deep metadata for a given track context.
     * Operates 100% offline using available local tags and database fields.
     */
    fun resolveTrackKnowledge(
        track: AdaptiveTrackContext,
        aiExplanationReason: String? = null
    ): MusicKnowledgeState {
        val cached = songCache[track.trackId]
        if (cached != null) {
            val state = MusicKnowledgeState(
                currentSongDetails = cached,
                currentArtistDetails = cached.artists.firstOrNull()?.let { artistCache[it.personId] },
                currentAlbumDetails = cached.albumId?.let { albumCache[it] },
                currentMovieDetails = cached.movieId?.let { movieCache[it] },
                aiExplanation = aiExplanationReason ?: "Playing from your local library.",
                source = cached.source,
                isLoading = false
            )
            _knowledgeState.value = state
            return state
        }

        // Resolve primary artist
        val artistPerson = PersonDetails(
            personId = "artist_${track.artist.lowercase().replace(" ", "_")}",
            name = track.artist,
            source = MetadataSource.LOCAL
        )

        // Resolve credits (fallback cleanly if unknown)
        val credits = SongCredits(
            singers = listOf(artistPerson),
            composers = emptyList(),
            lyricists = emptyList(),
            producers = emptyList(),
            label = null
        )

        val resolvedSong = SongDetails(
            songId = track.trackId,
            title = track.title,
            originalTitle = null,
            artists = listOf(artistPerson),
            credits = credits,
            albumId = track.album?.let { "album_${it.lowercase().replace(" ", "_")}" },
            albumTitle = track.album,
            movieId = null,
            movieTitle = null,
            language = track.language,
            languages = listOfNotNull(track.language),
            genre = track.genre,
            mood = null,
            energy = track.energy,
            bpm = track.bpm,
            durationMs = track.durationMs,
            artwork = MediaArtwork(role = ArtworkRole.SONG, source = MetadataSource.LOCAL),
            lyricsAvailable = false,
            source = MetadataSource.LOCAL,
            relatedSongs = emptyList()
        )

        songCache[track.trackId] = resolvedSong

        val state = MusicKnowledgeState(
            currentSongDetails = resolvedSong,
            currentArtistDetails = null,
            currentAlbumDetails = null,
            currentMovieDetails = null,
            aiExplanation = aiExplanationReason ?: "Selected based on your session context.",
            source = MetadataSource.LOCAL,
            isLoading = false
        )

        _knowledgeState.value = state
        return state
    }

    /**
     * Cache custom SongDetails (e.g. populated from DB or Edge API).
     */
    fun cacheSongDetails(songDetails: SongDetails) {
        songCache[songDetails.songId] = songDetails
    }

    /**
     * Cache custom MovieDetails.
     */
    fun cacheMovieDetails(movieDetails: MovieDetails) {
        movieCache[movieDetails.movieId] = movieDetails
    }

    /**
     * Cache custom ArtistDetails.
     */
    fun cacheArtistDetails(artistDetails: ArtistDetails) {
        artistCache[artistDetails.artistId] = artistDetails
    }

    /**
     * Cache custom AlbumDetails.
     */
    fun cacheAlbumDetails(albumDetails: AlbumDetails) {
        albumCache[albumDetails.albumId] = albumDetails
    }

    /**
     * Resolve SongDetails by songId with caching and graceful offline fallback.
     */
    fun getSongDetails(songId: String): SongDetails {
        val cached = songCache[songId]
        if (cached != null) return cached

        val fallbackArtist = PersonDetails(
            personId = "artist_unknown",
            name = "Unknown Artist",
            source = MetadataSource.UNKNOWN
        )

        return SongDetails(
            songId = songId,
            title = if (songId.isBlank()) "Information unavailable" else "Track $songId",
            artists = listOf(fallbackArtist),
            credits = SongCredits(singers = listOf(fallbackArtist)),
            source = MetadataSource.UNKNOWN
        )
    }

    /**
     * Resolve MovieDetails by movieId with caching and graceful offline fallback.
     */
    fun getMovieDetails(movieId: String): MovieDetails {
        val cached = movieCache[movieId]
        if (cached != null) return cached

        return MovieDetails(
            movieId = movieId,
            title = if (movieId.isBlank()) "Information unavailable" else "Movie $movieId",
            source = MetadataSource.UNKNOWN
        )
    }

    /**
     * Resolve PersonDetails by personId with caching and graceful offline fallback.
     */
    fun getPersonDetails(personId: String): PersonDetails {
        // Search in cached songs / movies / artists for person matching personId
        val songPerson = songCache.values.flatMap { song ->
            song.artists + song.featuredArtists + song.credits.singers + song.credits.composers + song.credits.lyricists + song.credits.producers
        }.firstOrNull { it.personId == personId }

        if (songPerson != null) return songPerson

        val moviePerson = movieCache.values.flatMap { movie ->
            movie.directors + movie.producers + movie.musicDirectors + movie.cinematographers + movie.editors + movie.writers + movie.leadActors + movie.leadActresses + movie.cast.map { it.person } + movie.crew.map { it.person }
        }.firstOrNull { it.personId == personId }

        if (moviePerson != null) return moviePerson

        return PersonDetails(
            personId = personId,
            name = if (personId.isBlank()) "Information unavailable" else "Person $personId",
            source = MetadataSource.UNKNOWN
        )
    }

    /**
     * Resolve ArtistDetails by artistId with caching and graceful offline fallback.
     */
    fun getArtistDetails(artistId: String): ArtistDetails {
        val cached = artistCache[artistId]
        if (cached != null) return cached

        return ArtistDetails(
            artistId = artistId,
            name = if (artistId.isBlank()) "Information unavailable" else "Artist $artistId",
            source = MetadataSource.UNKNOWN
        )
    }

    /**
     * Resolve AlbumDetails by albumId with caching and graceful offline fallback.
     */
    fun getAlbumDetails(albumId: String): AlbumDetails {
        val cached = albumCache[albumId]
        if (cached != null) return cached

        return AlbumDetails(
            albumId = albumId,
            title = if (albumId.isBlank()) "Information unavailable" else "Album $albumId",
            source = MetadataSource.UNKNOWN
        )
    }
}
