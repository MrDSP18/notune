package echo.music.iad1tya.notune.intelligence.enrichment

import echo.music.iad1tya.notune.intelligence.knowledge.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Local metadata provider. Extracts details from local audio files, tags, filenames, and MediaStore.
 */
@Singleton
class LocalMetadataProvider @Inject constructor() : MetadataProvider {
    override val providerId: String = "local_mediastore"
    override val providerPriority: Int = 100 // Highest priority for verified local files

    override suspend fun searchSong(identity: SongIdentity): List<ProviderSongMatch> {
        if (identity.cleanTitle.isBlank() && identity.rawInput.isBlank()) {
            return emptyList()
        }

        val title = identity.cleanTitle.ifBlank { identity.rawInput }
        val artist = identity.cleanArtist.ifBlank { "Unknown Artist" }

        val artistPerson = PersonDetails(
            personId = "artist_${artist.lowercase().replace(" ", "_")}",
            name = artist,
            source = MetadataSource.LOCAL
        )

        val credits = SongCredits(
            singers = listOf(artistPerson),
            composers = emptyList(),
            lyricists = emptyList(),
            producers = emptyList(),
            label = null
        )

        val songDetails = SongDetails(
            songId = identity.isrc ?: "local_${title.hashCode()}_${artist.hashCode()}",
            title = title,
            originalTitle = null,
            artists = listOf(artistPerson),
            credits = credits,
            albumId = identity.cleanAlbum?.takeIf { it.isNotBlank() }?.let { "album_${it.lowercase().replace(" ", "_")}" },
            albumTitle = identity.cleanAlbum?.takeIf { it.isNotBlank() },
            movieId = identity.cleanMovie?.takeIf { it.isNotBlank() }?.let { "movie_${it.lowercase().replace(" ", "_")}" },
            movieTitle = identity.cleanMovie,
            language = identity.language,
            languages = listOfNotNull(identity.language),
            durationMs = identity.durationMs,
            artwork = MediaArtwork(role = ArtworkRole.SONG, source = MetadataSource.LOCAL),
            lyricsAvailable = false,
            source = MetadataSource.LOCAL
        )

        return listOf(
            ProviderSongMatch(
                identity = identity,
                songDetails = songDetails,
                confidenceScore = 1.0,
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
