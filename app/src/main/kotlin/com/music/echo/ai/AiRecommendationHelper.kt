
package echo.music.iad1tya.ai

import android.content.Context
import com.music.innertube.YouTube
import com.music.innertube.models.SongItem
import echo.music.iad1tya.db.InternalDatabase
import echo.music.iad1tya.db.entities.PlaylistEntity
import echo.music.iad1tya.db.entities.PlaylistSongMap
import echo.music.iad1tya.db.entities.Song
import echo.music.iad1tya.db.entities.SongEntity
import echo.music.iad1tya.notune.ai.AiEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import org.json.JSONArray
import javax.inject.Inject
import javax.inject.Singleton

private const val PLAYLIST_NAME = "Recommended by AI"

@Singleton
class AiRecommendationHelper @Inject constructor(
    private val aiEngine: AiEngine,
    private val tasteProfileRepository: com.music.echo.notune.personalization.repository.TasteProfileRepository
) {

    suspend fun generateRecommendations(
        context: Context,
        onLog: (suspend (String) -> Unit)? = null
    ) = withContext(Dispatchers.IO) {
        val database = InternalDatabase.newInstance(context)
        onLog?.invoke("NØTUNE Engine: Scanning musical DNA...")

        val topSongs: List<Song> = database.topSongs(20).firstOrNull() ?: emptyList()
        val tasteProfile = tasteProfileRepository.getTasteProfileOnce()
        
        val tasteList = mutableListOf<String>()
        if (topSongs.isNotEmpty()) {
            tasteList.addAll(topSongs.map { "${it.song.title} by ${it.artists.joinToString { a -> a.name }}" })
        }
        
        // Add taste profile info if available
        val profileArtists = tasteProfile.favoriteArtists.map { it.name }
        val profileGenres = tasteProfile.favoriteGenres
        val profileLangs = tasteProfile.musicLanguages
        
        val prompt = if (tasteList.isEmpty() && profileArtists.isEmpty()) {
            "Recommend 20 popular futuristic songs. Respond ONLY as a JSON array: [{\"title\": \"Song\", \"artist\": \"Artist\"}]"
        } else {
            """
                USER_MUSICAL_DNA:
                - Recent Favorites: ${tasteList.take(10).joinToString(", ")}
                - Favorite Artists: ${profileArtists.joinToString(", ")}
                - Preferred Genres: ${profileGenres.joinToString(", ")}
                - Languages: ${profileLangs.joinToString(", ")}
                
                Based on this DNA, recommend 20 proper songs. 
                Focus on the selected artists and languages.
                Respond ONLY as a JSON array: [{"title": "Song", "artist": "Artist"}]
            """.trimIndent()
        }

        onLog?.invoke("Resolving neural predictions...")
        val result = aiEngine.generateResponse(prompt)
        val jsonOutput = result.getOrNull()?.text ?: return@withContext
        val jsonMatch = Regex("""\[.*\]""", RegexOption.DOT_MATCHES_ALL).find(jsonOutput)?.value
            ?: jsonOutput.replace("```json", "").replace("```", "").trim()
        
        val jsonArray = runCatching { JSONArray(jsonMatch) }.getOrNull() ?: return@withContext

        val resolvedSongs = mutableListOf<SongItem>()
        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.optJSONObject(i) ?: continue
            val title = item.optString("title")
            val artist = item.optString("artist")
            onLog?.invoke("NØTUNE Resolver: [${i + 1}/${jsonArray.length()}] $title")
            YouTube.search("$title $artist", YouTube.SearchFilter.FILTER_SONG).getOrNull()?.items?.filterIsInstance<SongItem>()?.firstOrNull()?.let {
                resolvedSongs.add(it)
            }
        }

        if (resolvedSongs.isEmpty()) return@withContext

        var playlist = database.searchPlaylists(PLAYLIST_NAME).firstOrNull()?.find { it.playlist.name == PLAYLIST_NAME }?.playlist
        if (playlist == null) {
            playlist = PlaylistEntity(name = PLAYLIST_NAME, isLocal = true, isEditable = true)
            database.insert(playlist)
        } else {
            database.clearPlaylist(playlist.id)
        }

        resolvedSongs.forEachIndexed { index, songItem ->
            database.insert(SongEntity(id = songItem.id, title = songItem.title, duration = songItem.duration ?: 0, thumbnailUrl = songItem.thumbnail))
            database.insert(PlaylistSongMap(playlistId = playlist.id, songId = songItem.id, position = index))
        }
        onLog?.invoke("Intelligence update complete.")
    }
}
