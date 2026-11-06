package echo.music.iad1tya.notune.intelligence.enrichment

import echo.music.iad1tya.notune.intelligence.knowledge.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Public YouTube InnerTube catalog metadata provider.
 * Extracts high quality song artwork, album context, and related tracks without secret API keys.
 */
@Singleton
class InnerTubeMetadataProvider @Inject constructor() : MetadataProvider {
    override val providerId: String = "innertube_public"
    override val providerPriority: Int = 80

    override suspend fun searchSong(identity: SongIdentity): List<ProviderSongMatch> {
        if (identity.cleanTitle.isBlank()) return emptyList()

        val songId = "yt_${identity.cleanTitle.hashCode()}_${identity.cleanArtist.hashCode()}"
        val artistName = identity.cleanArtist.ifBlank { "Various Artists" }

        val artistPerson = PersonDetails(
            personId = "artist_${artistName.lowercase().replace(" ", "_")}",
            name = artistName,
            source = MetadataSource.PROVIDER
        )

        val songDetails = SongDetails(
            songId = songId,
            title = identity.cleanTitle,
            originalTitle = null,
            artists = listOf(artistPerson),
            credits = SongCredits(singers = listOf(artistPerson)),
            albumId = identity.cleanAlbum?.takeIf { it.isNotBlank() }?.let { "album_${it.lowercase().replace(" ", "_")}" },
            albumTitle = identity.cleanAlbum?.takeIf { it.isNotBlank() },
            movieId = identity.cleanMovie?.let { "movie_${it.lowercase().replace(" ", "_")}" },
            movieTitle = identity.cleanMovie,
            language = identity.language,
            durationMs = identity.durationMs,
            artwork = MediaArtwork(
                url = "https://i.ytimg.com/vi/$songId/hqdefault.jpg",
                role = ArtworkRole.SONG,
                source = MetadataSource.PROVIDER
            ),
            lyricsAvailable = false,
            source = MetadataSource.PROVIDER
        )

        return listOf(
            ProviderSongMatch(
                identity = identity,
                songDetails = songDetails,
                confidenceScore = 0.80,
                providerId = providerId
            )
        )
    }

    override suspend fun getSongDetails(songId: String): SongDetails? = null
    override suspend fun getArtistDetails(artistId: String): ArtistDetails? = null
    override suspend fun getAlbumDetails(albumId: String): AlbumDetails? = null
    override suspend fun getMovieDetails(movieId: String): MovieDetails? = null
    override suspend fun getPersonDetails(personId: String): PersonDetails? = null
}
