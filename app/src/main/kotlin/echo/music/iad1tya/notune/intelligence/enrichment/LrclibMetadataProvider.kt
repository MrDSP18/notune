package echo.music.iad1tya.notune.intelligence.enrichment

import echo.music.iad1tya.notune.intelligence.knowledge.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import javax.inject.Inject
import javax.inject.Singleton

/**
 * LRCLIB open lyrics metadata provider.
 * Resolves plain text and synced lyrics without API keys.
 */
@Singleton
class LrclibMetadataProvider @Inject constructor() : MetadataProvider {
    override val providerId: String = "lrclib_public"
    override val providerPriority: Int = 70

    override suspend fun searchSong(identity: SongIdentity): List<ProviderSongMatch> = withContext(Dispatchers.IO) {
        if (identity.cleanTitle.isBlank()) return@withContext emptyList()

        val title = identity.cleanTitle
        val artist = identity.cleanArtist
        var lyricsFound = false

        try {
            val encodedTitle = URLEncoder.encode(title, "UTF-8")
            val encodedArtist = URLEncoder.encode(artist, "UTF-8")
            val apiUrl = "https://lrclib.net/api/get?track_name=$encodedTitle&artist_name=$encodedArtist"

            val connection = (URL(apiUrl).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 3000
                readTimeout = 3000
                setRequestProperty("User-Agent", "NoTuneApp/3.2.0")
            }

            if (connection.responseCode == 200) {
                val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                connection.disconnect()
                val json = JSONObject(responseText)
                if (json.has("syncedLyrics") || json.has("plainLyrics")) {
                    lyricsFound = true
                }
            } else {
                connection.disconnect()
            }
        } catch (_e: Exception) {
            // Fallback gracefully on timeout/error
        }

        val songId = "lrclib_${title.hashCode()}_${artist.hashCode()}"
        val artistName = artist.ifBlank { "Unknown Artist" }

        val artistPerson = PersonDetails(
            personId = "artist_${artistName.lowercase().replace(" ", "_")}",
            name = artistName,
            source = MetadataSource.PROVIDER
        )

        val songDetails = SongDetails(
            songId = songId,
            title = title,
            artists = listOf(artistPerson),
            credits = SongCredits(singers = listOf(artistPerson)),
            lyricsAvailable = lyricsFound,
            source = MetadataSource.PROVIDER
        )

        listOf(
            ProviderSongMatch(
                identity = identity,
                songDetails = songDetails,
                confidenceScore = if (lyricsFound) 0.85 else 0.50,
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
