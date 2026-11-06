package echo.music.iad1tya.notune.intelligence.knowledge

/**
 * Roles for media visual assets across UI views.
 */
enum class ArtworkRole {
    ALBUM,
    SONG,
    ARTIST_PROFILE,
    ARTIST_HERO,
    MOVIE_POSTER,
    MOVIE_BACKDROP,
    PERSON_PROFILE,
    PLAYLIST
}

/**
 * Multi-resolution, cached artwork reference.
 */
data class MediaArtwork(
    val url: String? = null,
    val localUri: String? = null,
    val width: Int = 0,
    val height: Int = 0,
    val role: ArtworkRole = ArtworkRole.SONG,
    val source: MetadataSource = MetadataSource.UNKNOWN
)
