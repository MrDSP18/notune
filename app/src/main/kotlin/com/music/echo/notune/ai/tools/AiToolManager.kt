
package echo.music.iad1tya.notune.ai.tools

import android.content.Context
import com.music.innertube.YouTube
import com.music.innertube.models.SongItem
import echo.music.iad1tya.db.MusicDatabase
import echo.music.iad1tya.notune.ai.ToolCall
import echo.music.iad1tya.playback.PlayerConnection
import echo.music.iad1tya.extensions.toMediaItem
import echo.music.iad1tya.models.toMediaMetadata
import echo.music.iad1tya.playback.queues.ListQueue
import echo.music.iad1tya.playback.queues.YouTubeQueue
import kotlinx.coroutines.flow.first
import timber.log.Timber
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiToolManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: MusicDatabase
) {
    var playerConnection: PlayerConnection? = null

    suspend fun executeTool(toolCall: ToolCall): String {
        Timber.d("AI_TOOL: Executing ${toolCall.functionName} with ${toolCall.arguments}")
        return when (toolCall.functionName) {
            "search_songs" -> {
                val query = toolCall.arguments["query"] ?: return "Error: Missing query"
                val results = YouTube.search(query, YouTube.SearchFilter.FILTER_SONG).getOrNull()
                val songs = results?.items?.filterIsInstance<SongItem>()?.take(5)
                if (songs.isNullOrEmpty()) "I couldn't find any songs matching '$query'."
                else "I found these songs: " + songs.joinToString { "${it.title} by ${it.artists.joinToString { a -> a.name }} (ID: ${it.id})" }
            }
            "play_song" -> {
                val player = playerConnection ?: return "Error: Player not connected."
                val songId = toolCall.arguments["song_id"] ?: return "Error: Missing song_id"
                // Resolve metadata first if possible
                val song = YouTube.search(songId, YouTube.SearchFilter.FILTER_SONG).getOrNull()?.items?.firstOrNull() as? SongItem
                if (song != null) {
                    player.playQueue(YouTubeQueue(song.id.let { com.music.innertube.models.WatchEndpoint(videoId = it) }, song.toMediaMetadata()))
                    "Playing ${song.title}."
                } else {
                    "Error: Could not resolve song metadata for ID $songId."
                }
            }
            "get_user_history" -> {
                val history = database.topSongs(10).first()
                if (history.isEmpty()) "The user has no listening history yet."
                else "The user's top songs are: " + history.joinToString { "${it.song.title} by ${it.artists.joinToString { a -> a.name }}" }
            }
            "create_playlist" -> {
                val player = playerConnection ?: return "Error: Player not connected."
                val name = toolCall.arguments["name"] ?: "AI Playlist"
                val songsStr = toolCall.arguments["songs"] ?: ""
                val resolvedSongs = mutableListOf<SongItem>()
                
                songsStr.split(",").forEach { s ->
                    YouTube.search(s.trim(), YouTube.SearchFilter.FILTER_SONG).getOrNull()?.items?.filterIsInstance<SongItem>()?.firstOrNull()?.let {
                        resolvedSongs.add(it)
                    }
                }
                
                if (resolvedSongs.isNotEmpty()) {
                    player.playQueue(ListQueue(name, resolvedSongs.map { it.toMediaItem() }, 0))
                    "I've created and started the playlist '$name' with ${resolvedSongs.size} songs."
                } else {
                    "I suggested some songs but couldn't find them to play."
                }
            }
            "pause_music" -> {
                val player = playerConnection ?: return "Error: Player not connected."
                player.pause()
                "Music paused."
            }
            "resume_music" -> {
                val player = playerConnection ?: return "Error: Player not connected."
                player.play()
                "Music resumed."
            }
            "skip_music" -> {
                val player = playerConnection ?: return "Error: Player not connected."
                player.seekToNext()
                "Skipped to the next track."
            }
            "previous_music" -> {
                val player = playerConnection ?: return "Error: Player not connected."
                player.seekToPrevious()
                "Gone back to the previous track."
            }
            "get_current_track" -> {
                val player = playerConnection ?: return "No music is currently playing (player disconnected)."
                val meta = player.mediaMetadata.value
                if (meta == null) "No music is currently playing."
                else "Currently playing: ${meta.title} by ${meta.artists.joinToString { it.name }}."
            }
            "explain_song" -> {
                val query = toolCall.arguments["query"] ?: return "Error: Missing query"
                // The AI will handle the explanation, this tool just acknowledges the context
                "NØTUNE Engine: Fetching lyrical insights for $query..."
            }
            else -> "Unknown tool: ${toolCall.functionName}"
        }
    }
}
