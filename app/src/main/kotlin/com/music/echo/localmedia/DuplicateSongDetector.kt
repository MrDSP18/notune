package com.music.echo.localmedia

import echo.music.iad1tya.db.MusicDatabase
import echo.music.iad1tya.db.entities.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class DuplicateGroup(
    val titleKey: String,
    val primaryArtist: String,
    val duplicates: List<Song>
)

object DuplicateSongDetector {

    suspend fun findDuplicateSongs(database: MusicDatabase): List<DuplicateGroup> = withContext(Dispatchers.IO) {
        val songs = database.getLocalSongsSync()
        
        val grouped = songs.groupBy { song ->
            val titleNorm = song.song.title.lowercase().trim().replace(Regex("[^a-z0-9]"), "")
            val artistNorm = song.artists.firstOrNull()?.name?.lowercase()?.trim()?.replace(Regex("[^a-z0-9]"), "") ?: ""
            "$titleNorm|$artistNorm"
        }

        grouped.filter { (key, list) ->
            key.isNotBlank() && list.size > 1
        }.map { (_, duplicateList) ->
            val first = duplicateList.first()
            DuplicateGroup(
                titleKey = first.song.title,
                primaryArtist = first.artists.firstOrNull()?.name ?: "Unknown Artist",
                duplicates = duplicateList
            )
        }.sortedBy { it.titleKey.lowercase() }
    }
}
