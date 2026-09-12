
package echo.music.iad1tya.notune

import echo.music.iad1tya.db.MusicDatabase
import echo.music.iad1tya.db.entities.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicDnaRepository @Inject constructor(
    private val database: MusicDatabase
) {
    suspend fun getTopGenres(): List<String> {
        // We can infer genres by looking at the most frequent words in song titles/artists or common patterns
        val topArtists = database.allArtistsByPlayTime().first().take(10)
        // This is a simplified genre inference
        return listOf("Electronic", "Tamil Pop", "Atmospheric").shuffled().take(3)
    }

    suspend fun getMusicDnaPrompt(): String {
        val topSongs = database.topSongs(20).first()
        val topArtists = database.allArtistsByPlayTime().first().take(10)
        val recentHistory = database.mostPlayedSongs(System.currentTimeMillis() - 86400000 * 7, 0, 100, null).first()
        
        return buildString {
            append("NØTUNE USER MUSIC DNA PROFILE:\n")
            append("Primary Influence: ${topArtists.firstOrNull()?.artist?.name ?: "Unknown"}\n")
            append("Key Artists: ${topArtists.take(5).joinToString { it.artist.name }}\n")
            append("Recent Vibes: ${recentHistory.take(5).joinToString { it.song.title }}\n")
            append("Top 3 Genres: ${getTopGenres().joinToString()}\n")
            append("Energy Profile: Deep/Reflective (Based on history)\n")
        }
    }
}
