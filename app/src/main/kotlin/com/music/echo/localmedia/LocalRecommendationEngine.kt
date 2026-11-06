package com.music.echo.localmedia

import echo.music.iad1tya.db.MusicDatabase
import echo.music.iad1tya.db.entities.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object LocalRecommendationEngine {

    suspend fun getLocalRecommendations(
        database: MusicDatabase,
        limit: Int = 30
    ): List<Song> = withContext(Dispatchers.IO) {
        val allLocal = database.getLocalSongsSync()
        if (allLocal.isEmpty()) return@withContext emptyList()

        // Identify top artists by total play time
        val topArtistNames = allLocal
            .flatMap { song -> song.artists.map { it.name to song.song.totalPlayTime } }
            .groupBy({ it.first }, { it.second })
            .mapValues { (_, times) -> times.sum() }
            .entries
            .sortedByDescending { it.value }
            .take(5)
            .map { it.key }
            .toSet()

        // Score songs based on top artist match and low play count (for discovery/rediscovery)
        val scoredSongs = allLocal.map { song ->
            var score = 0.0
            val isTopArtist = song.artists.any { it.name in topArtistNames }
            if (isTopArtist) score += 50.0
            if (song.song.liked) score += 30.0

            // Discovery bonus for songs with lower play time
            if (song.song.totalPlayTime == 0L) score += 20.0

            song to score
        }

        scoredSongs
            .sortedByDescending { it.second }
            .take(limit)
            .map { it.first }
    }
}
