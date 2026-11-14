package com.music.echo.localmedia

import echo.music.iad1tya.db.MusicDatabase
import echo.music.iad1tya.db.entities.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.Normalizer
import java.util.Locale

data class DuplicateGroup(
    val titleKey: String,
    val primaryArtist: String,
    val duplicates: List<Song>
)

object DuplicateSongDetector {
    suspend fun findDuplicateSongs(database: MusicDatabase): List<DuplicateGroup> = withContext(Dispatchers.IO) {
        val songs = database.getLocalSongsSync()
        val grouped = songs.groupBy { song ->
            val title = normalize(song.song.title)
            val artist = normalize(song.artists.firstOrNull()?.name.orEmpty())
            val fallback = if (artist.isNotEmpty()) artist else {
                song.song.duration.takeIf { it > 0 }?.let { "duration:$it" } ?: "id:${song.song.id}"
            }
            "$title|$fallback"
        }
        grouped.filter { (key, list) ->
            key.substringBefore('|').isNotBlank() && list.size > 1
        }.map { (_, duplicateList) ->
            val first = duplicateList.first()
            DuplicateGroup(
                titleKey = first.song.title,
                primaryArtist = first.artists.firstOrNull()?.name ?: "Unknown Artist",
                duplicates = duplicateList
            )
        }.sortedBy { normalize(it.titleKey) }
    }

    internal fun normalize(value: String): String {
        val normalized = Normalizer.normalize(value, Normalizer.Form.NFKC).lowercase(Locale.ROOT)
        return buildString(normalized.length) {
            normalized.forEach { char ->
                val type = Character.getType(char)
                if (char.isLetterOrDigit() ||
                    type == Character.NON_SPACING_MARK.toInt() ||
                    type == Character.COMBINING_SPACING_MARK.toInt() ||
                    type == Character.ENCLOSING_MARK.toInt()
                ) append(char)
            }
        }
    }
}
