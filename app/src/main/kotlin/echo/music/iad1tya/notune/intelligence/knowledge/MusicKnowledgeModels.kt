package echo.music.iad1tya.notune.intelligence.knowledge

/**
 * Detailed music credits for a song.
 */
data class SongCredits(
    val singers: List<PersonDetails> = emptyList(),
    val composers: List<PersonDetails> = emptyList(),
    val lyricists: List<PersonDetails> = emptyList(),
    val producers: List<PersonDetails> = emptyList(),
    val label: String? = null
)

/**
 * Related song context item with similarity score and explanation reason.
 */
data class RelatedSong(
    val songId: String,
    val title: String,
    val artist: String,
    val relationshipReason: String,
    val similarityScore: Float
)

/**
 * Deep metadata model for a song.
 */
data class SongDetails(
    val songId: String,
    val title: String,
    val originalTitle: String? = null,
    val artists: List<PersonDetails> = emptyList(),
    val featuredArtists: List<PersonDetails> = emptyList(),
    val credits: SongCredits = SongCredits(),
    val albumId: String? = null,
    val albumTitle: String? = null,
    val movieId: String? = null,
    val movieTitle: String? = null,
    val language: String? = null,
    val languages: List<String> = emptyList(),
    val genre: String? = null,
    val subGenre: String? = null,
    val mood: String? = null,
    val energy: Float? = null,
    val bpm: Float? = null,
    val durationMs: Long = 0L,
    val releaseDate: String? = null,
    val year: Int? = null,
    val trackNumber: Int? = null,
    val discNumber: Int? = null,
    val isExplicit: Boolean = false,
    val artwork: MediaArtwork? = null,
    val lyricsAvailable: Boolean = false,
    val audioQuality: String? = null,
    val source: MetadataSource = MetadataSource.UNKNOWN,
    val popularity: Int = 0,
    val localPlayCount: Int = 0,
    val relatedSongs: List<RelatedSong> = emptyList()
)

/**
 * Deep metadata model for an artist.
 */
data class ArtistDetails(
    val artistId: String,
    val name: String,
    val stageName: String? = null,
    val biography: String? = null,
    val profileArtwork: MediaArtwork? = null,
    val heroArtwork: MediaArtwork? = null,
    val languages: List<String> = emptyList(),
    val genres: List<String> = emptyList(),
    val roles: List<String> = emptyList(),
    val albumIds: List<String> = emptyList(),
    val popularSongIds: List<String> = emptyList(),
    val relatedArtistIds: List<String> = emptyList(),
    val movieIds: List<String> = emptyList(),
    val source: MetadataSource = MetadataSource.UNKNOWN
)

/**
 * Deep metadata model for an album.
 */
data class AlbumDetails(
    val albumId: String,
    val title: String,
    val artwork: MediaArtwork? = null,
    val releaseDate: String? = null,
    val year: Int? = null,
    val language: String? = null,
    val artistIds: List<String> = emptyList(),
    val label: String? = null,
    val genre: String? = null,
    val totalTracks: Int = 0,
    val trackIds: List<String> = emptyList(),
    val movieId: String? = null,
    val description: String? = null,
    val relatedAlbumIds: List<String> = emptyList(),
    val source: MetadataSource = MetadataSource.UNKNOWN
)

/**
 * Deep metadata model for a movie / soundtrack.
 */
data class MovieDetails(
    val movieId: String,
    val title: String,
    val originalTitle: String? = null,
    val posterArtwork: MediaArtwork? = null,
    val heroBackdropArtwork: MediaArtwork? = null,
    val releaseDate: String? = null,
    val year: Int? = null,
    val language: String? = null,
    val genres: List<String> = emptyList(),
    val directors: List<PersonDetails> = emptyList(),
    val producers: List<PersonDetails> = emptyList(),
    val musicDirectors: List<PersonDetails> = emptyList(),
    val cinematographers: List<PersonDetails> = emptyList(),
    val editors: List<PersonDetails> = emptyList(),
    val writers: List<PersonDetails> = emptyList(),
    val leadActors: List<PersonDetails> = emptyList(),
    val leadActresses: List<PersonDetails> = emptyList(),
    val cast: List<CastMember> = emptyList(),
    val crew: List<CrewMember> = emptyList(),
    val soundtrackAlbumId: String? = null,
    val description: String? = null,
    val relatedMovieIds: List<String> = emptyList(),
    val source: MetadataSource = MetadataSource.UNKNOWN
)
