package echo.music.iad1tya.notune.intelligence.enrichment

import echo.music.iad1tya.notune.intelligence.knowledge.*

/**
 * Result returned by a [MetadataProvider] during song search.
 */
data class ProviderSongMatch(
    val identity: SongIdentity,
    val songDetails: SongDetails,
    val confidenceScore: Double = 0.0,
    val providerId: String,
    val fetchedAt: Long = System.currentTimeMillis()
)

/**
 * Interface defining metadata provider capabilities.
 * Concrete implementations fetch metadata from local storage, NØTUNE backend, or public internet APIs.
 */
interface MetadataProvider {
    val providerId: String
    val providerPriority: Int

    suspend fun searchSong(identity: SongIdentity): List<ProviderSongMatch>
    suspend fun getSongDetails(songId: String): SongDetails?
    suspend fun getArtistDetails(artistId: String): ArtistDetails?
    suspend fun getAlbumDetails(albumId: String): AlbumDetails?
    suspend fun getMovieDetails(movieId: String): MovieDetails?
    suspend fun getPersonDetails(personId: String): PersonDetails?
}
