package com.music.echo.localmedia

import echo.music.iad1tya.db.MusicDatabase
import echo.music.iad1tya.db.entities.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
        val allLocal = database.getLocalSongsSync()

        when (type) {
            SmartPlaylistType.RECENTLY_ADDED -> {
                allLocal.sortedByDescending { it.song.dateModified ?: java.time.LocalDateTime.MIN }
                    .take(limit)
            }
            SmartPlaylistType.MOST_PLAYED -> {
                allLocal.sortedByDescending { it.song.totalPlayTime }
                    .filter { it.song.totalPlayTime > 0 }
                    .take(limit)
            }
            SmartPlaylistType.FAVORITES -> {
                allLocal.filter { it.song.liked }
            }
            SmartPlaylistType.NEVER_PLAYED -> {
                allLocal.filter { it.song.totalPlayTime == 0L }
            }
        }
    }
}
