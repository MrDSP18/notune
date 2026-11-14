package com.music.echo.localmedia

import echo.music.iad1tya.db.MusicDatabase
import echo.music.iad1tya.db.entities.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.Normalizer
import java.util.Locale
import kotlin.math.abs

data class DuplicateGroup(
    val titleKey: String,
    val primaryArtist: String,
    val duplicates: List<Song>
)

object DuplicateSongDetector {

    suspend fun findDuplicateSongs(database: MusicDatabase): List<DuplicateGroup> = withContext(Dispatchers.IO) {
        val songs = database.getLocalSongsSync()
        val validSongs = songs.filter { it.song.title.isNotBlank() }

        val grouped = validSongs.groupBy { song ->
            val titleNorm = normalize(song.song.title)
            val artistNorm = song.artists.firstOrNull()?.name?.let { normalize(it) } ?: ""
            val fallback = if (artistNorm.isNotEmpty()) artistNorm else {
                song.song.duration.takeIf { it > 0 }?.let { "duration:$it" } ?: "id:${song.song.id}"
            }
            val effectiveTitle = if (titleNorm.isNotBlank()) titleNorm else song.song.title.trim().lowercase(Locale.ROOT)
            "$effectiveTitle|$fallback"
        }

        grouped.filter { (key, list) ->
            key.substringBefore('|').isNotBlank() && list.size > 1
        }.mapNotNull { (_, duplicateList) ->
            val first = duplicateList.first()
            // Verify duration tolerance (within 4 seconds) to avoid grouping remixes/extended versions with original tracks
            val validDuplicates = duplicateList.filter { candidate ->
                first.song.duration <= 0 || candidate.song.duration <= 0 ||
                    abs(first.song.duration - candidate.song.duration) <= 4
            }
            if (validDuplicates.size > 1) {
                DuplicateGroup(
                    titleKey = first.song.title,
                    primaryArtist = first.artists.firstOrNull()?.name ?: "Unknown Artist",
                    duplicates = validDuplicates
                )
            } else null
        }.sortedBy { normalize(it.titleKey) }
    }

    /**
     * Normalizes a metadata string preserving all Unicode characters (e.g. Tamil, Hindi, Cyrillic, CJK, etc.)
     * including base letters, combining marks (vowel signs, viramas/pulli), and digits
     * while stripping punctuation, symbols, and whitespace.
     */
    fun normalize(value: String): String {
        if (value.isBlank()) return ""
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

    /**
     * Alias for [normalize] for backward-compatible and explicit metadata normalization calls.
     */
    fun normalizeMetadata(input: String): String = normalize(input)
}
