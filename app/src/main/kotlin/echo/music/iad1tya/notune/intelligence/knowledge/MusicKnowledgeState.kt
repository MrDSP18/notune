package echo.music.iad1tya.notune.intelligence.knowledge

/**
 * Reusable single source of truth for Deep Now Playing & Metadata state.
 */
data class MusicKnowledgeState(
    val currentSongDetails: SongDetails? = null,
    val currentArtistDetails: ArtistDetails? = null,
    val currentAlbumDetails: AlbumDetails? = null,
    val currentMovieDetails: MovieDetails? = null,
    val aiExplanation: String? = null,
    val source: MetadataSource = MetadataSource.UNKNOWN,
    val isLoading: Boolean = false
)
