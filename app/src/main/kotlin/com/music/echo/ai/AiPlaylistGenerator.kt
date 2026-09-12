
package echo.music.iad1tya.ai

import android.content.Context
import com.music.innertube.YouTube
import com.music.innertube.models.SongItem
import echo.music.iad1tya.ai.weather.toSnapshotString
import echo.music.iad1tya.db.InternalDatabase
import echo.music.iad1tya.db.entities.PlaylistEntity
import echo.music.iad1tya.db.entities.PlaylistSongMap
import echo.music.iad1tya.db.entities.SongEntity
import echo.music.iad1tya.notune.ai.AiEngine
import echo.music.iad1tya.notune.ai.ToolCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiPlaylistGenerator @Inject constructor(
    private val aiEngine: AiEngine
) {
    suspend fun generatePlaylist(
        context: Context,
        userPrompt: String,
        numberOfSongs: Int = 15,
        weatherInfo: echo.music.iad1tya.ai.weather.WeatherInfo? = null,
        onLog: suspend (String) -> Unit
    ): String? = withContext(Dispatchers.IO) {
        val database = InternalDatabase.newInstance(context)
        onLog("NØTUNE Engine: Calculating acoustic parameters...")

        val systemPrompt = """
            You are NØTUNE AI Music Curator. 
            Generate exactly $numberOfSongs songs.
            Respond ONLY with a JSON object: {"name": "Playlist Name", "songs": [{"title": "Song", "artist": "Artist"}]}
        """.trimIndent()

        val fullPrompt = "$userPrompt (Current Weather: ${weatherInfo?.condition ?: "Unknown"})"
        
        onLog("Contacting AI providers...")
        val result = aiEngine.generateResponse(fullPrompt, systemInstruction = systemPrompt)
        
        val jsonOutput = result.getOrNull()?.text ?: return@withContext null
        val cleanJson = jsonOutput.replace("```json", "").replace("```", "").trim()
        
        val parsedJson = runCatching { JSONObject(cleanJson) }.getOrNull() ?: return@withContext null
        val playlistName = parsedJson.optString("name", "NØTUNE AI Mix")
        val songsArray = parsedJson.optJSONArray("songs") ?: return@withContext null

        val resolvedSongs = mutableListOf<SongItem>()
        for (i in 0 until songsArray.length()) {
            val item = songsArray.optJSONObject(i) ?: continue
            val title = item.optString("title")
            val artist = item.optString("artist")
            onLog("NØTUNE Resolver: [${i + 1}/${songsArray.length()}] $title")
            YouTube.search("$title $artist", YouTube.SearchFilter.FILTER_SONG).getOrNull()?.items?.filterIsInstance<SongItem>()?.firstOrNull()?.let {
                resolvedSongs.add(it)
            }
        }

        if (resolvedSongs.isEmpty()) return@withContext null

        val playlistEntity = PlaylistEntity(
            name = playlistName,
            radioEndpointParams = weatherInfo?.toSnapshotString(),
            bookmarkedAt = java.time.LocalDateTime.now(),
            isLocal = true,
            isEditable = true
        )
        database.insert(playlistEntity)
        resolvedSongs.forEachIndexed { index, songItem ->
            database.insert(SongEntity(id = songItem.id, title = songItem.title, duration = songItem.duration ?: 0, thumbnailUrl = songItem.thumbnail))
            database.insert(PlaylistSongMap(playlistId = playlistEntity.id, songId = songItem.id, position = index))
        }

        onLog("Playlist synced successfully.")
        return@withContext playlistEntity.id
    }
}
