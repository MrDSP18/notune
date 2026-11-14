package com.music.echo.localmedia

import echo.music.iad1tya.db.MusicDatabase
import echo.music.iad1tya.db.entities.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

enum class SmartPlaylistType {
    RECENTLY_ADDED,
    MOST_PLAYED,
    FAVORITES,
    NEVER_PLAYED
}

object SmartPlaylistEngine {

    suspend fun getSmartPlaylistSongs(
        type: SmartPlaylistType,
        database: MusicDatabase,
        limit: Int = 100
    ): List<Song> = withContext(Dispatchers.IO) {
        if (limit <= 0) return@withContext emptyList()
        selectSongs(database.getLocalSongsSync(), type, limit)
    }

    internal fun selectSongs(
        songs: List<Song>,
        type: SmartPlaylistType,
        limit: Int
    ): List<Song> {
        if (limit <= 0 || songs.isEmpty()) return emptyList()

        return when (type) {
            SmartPlaylistType.RECENTLY_ADDED -> songs
                .sortedByDescending {
                    it.song.inLibrary ?: it.song.dateDownload ?: it.song.dateModified ?: LocalDateTime.MIN
                }
                .take(limit)

            SmartPlaylistType.MOST_PLAYED -> songs
                .filter { it.song.totalPlayTime > 0L }
                .sortedWith(
                    compareByDescending<Song> { it.song.totalPlayTime }
                        .thenBy { it.song.title.lowercase() }
                )
                .take(limit)

            SmartPlaylistType.FAVORITES -> songs
                .filter { it.song.liked }
                .sortedByDescending { it.song.likedDate ?: it.song.inLibrary ?: LocalDateTime.MIN }
                .take(limit)

            SmartPlaylistType.NEVER_PLAYED -> songs
                .filter { it.song.totalPlayTime <= 0L }
                .sortedByDescending { it.song.dateModified ?: LocalDateTime.MIN }
                .take(limit)
        }
    }
}
