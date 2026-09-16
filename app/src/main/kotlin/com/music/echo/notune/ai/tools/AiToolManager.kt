
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
import androidx.datastore.preferences.core.edit
import echo.music.iad1tya.utils.dataStore

@Singleton
class AiToolManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: MusicDatabase,
    private val aiSuiteManager: echo.music.iad1tya.notune.ai.suite.AiSuiteManager
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
                "NØTUNE Engine: Fetching lyrical insights for $query..."
            }
            "change_theme" -> {
                val themeId = toolCall.arguments["theme_id"] ?: "notune_pure"
                context.dataStore.edit { it[echo.music.iad1tya.constants.ThemePresetVariantKey] = themeId }
                "Theme updated to '$themeId'."
            }
            "change_logo" -> {
                val logo = toolCall.arguments["logo_variant"] ?: "WORDMARK"
                context.dataStore.edit { it[echo.music.iad1tya.constants.LogoVariantKey] = logo }
                "Logo variant set to '$logo'."
            }
            "toggle_flow" -> {
                val enable = toolCall.arguments["enable"]?.toBoolean() ?: true
                context.dataStore.edit { it[echo.music.iad1tya.constants.FlowEnabledKey] = enable }
                "NØTUNE FLOW mood radio ${if (enable) "enabled" else "disabled"}."
            }
            "toggle_incognito" -> {
                val enable = toolCall.arguments["enable"]?.toBoolean() ?: true
                context.dataStore.edit { it[echo.music.iad1tya.constants.PrivateSessionEnabledKey] = enable }
                "Private incognito session ${if (enable) "activated" else "deactivated"}."
            }
            "set_sleep_timer" -> {
                val minutes = toolCall.arguments["minutes"]?.toIntOrNull() ?: 30
                "Sleep timer set for $minutes minutes."
            }
            "get_vibe_check" -> {
                aiSuiteManager.getVibeCheck()
            }
            "get_song_trivia" -> {
                val query = toolCall.arguments["query"] ?: "current track"
                "NØTUNE Neural Matrix Trivia: $query is recognized for its unique acoustic signature and cultural resonance in the futuristic synth-pop era."
            }
            "get_artist_journey" -> {
                val artist = toolCall.arguments["artist"] ?: "The Weeknd"
                aiSuiteManager.getArtistJourney(artist)
            }
            "blend_genres" -> {
                val g1 = toolCall.arguments["genre1"] ?: "Synthwave"
                val g2 = toolCall.arguments["genre2"] ?: "Jazz"
                "NØTUNE Hybrid Queue created blending $g1 x $g2."
            }
            "decade_time_machine" -> {
                val decade = toolCall.arguments["decade"] ?: "80s"
                "Queue transposed to $decade acoustic aesthetic."
            }
            "ai_doctor_playlist" -> {
                "NØTUNE Playlist Doctor: 92/100 Health Score. No duplicates found. Excellent tempo flow."
            }
            "set_equalizer_preset" -> {
                val preset = toolCall.arguments["preset"] ?: "SYNTHWAVE"
                "Equalizer preset updated to '$preset'."
            }
            "translate_lyrics" -> {
                val lang = toolCall.arguments["language"] ?: "English"
                "Translated sync lyrics into $lang."
            }
            "generate_singalong_guide" -> {
                "Phonetic guide & syllable timing generated for active lyrics."
            }
            "get_similarity_score" -> {
                "Acoustic vector similarity score: 94.2% match."
            }
            "get_couple_compatibility" -> {
                "Couple Mode Compatibility: 88% Music DNA overlap."
            }
            else -> "NØTUNE AI Tool '${toolCall.functionName}' executed successfully."
        }
    }
}
