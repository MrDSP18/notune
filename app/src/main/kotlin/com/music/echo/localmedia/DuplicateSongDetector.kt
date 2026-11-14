package com.music.echo.localmedia

import echo.music.iad1tya.db.MusicDatabase
import echo.music.iad1tya.db.entities.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.Normalizer
import kotlin.math.abs

data class DuplicateGroup(
    val titleKey: String,
    val primaryArtist: String,
    val duplicates: List<Song>
)

object DuplicateSongDetector {

    /**
     * Normalizes a metadata string preserving all Unicode characters (e.g. Tamil, Hindi, Cyrillic, CJK, etc.)
     * including base letters (\p{L}), combining marks/matras/accents (\p{M}), and digits (\p{N})
     * while stripping irrelevant punctuation, symbols, and whitespace.
     */
    fun normalizeMetadata(input: String): String {
        if (input.isBlank()) return ""
        val normalized = Normalizer.normalize(input, Normalizer.Form.NFC).lowercase().trim()
        // Retain Unicode letters, combining marks (vowel signs, viramas), and numbers across all languages
        return normalized.replace(Regex("[^\\p{L}\\p{M}\\p{N}]"), "")
    }

    suspend fun findDuplicateSongs(database: MusicDatabase): List<DuplicateGroup> = withContext(Dispatchers.IO) {
        val songs = database.getLocalSongsSync()
        
        // Filter out entries with invalid/empty titles
        val validSongs = songs.filter { it.song.title.isNotBlank() }

        val grouped = validSongs.groupBy { song ->
            val titleNorm = normalizeMetadata(song.song.title)
            val artistNorm = song.artists.firstOrNull()?.name?.let { normalizeMetadata(it) } ?: ""
            
            // If the normalized title is completely stripped of letters/numbers, fall back to exact raw trimmed title
            val effectiveTitle = if (titleNorm.isNotBlank()) titleNorm else song.song.title.trim().lowercase()
            "$effectiveTitle|$artistNorm"
        }

        grouped.filter { (key, list) ->
            // Ensure key is meaningful and not just a delimiter or blank
            val parts = key.split("|")
            val titlePart = parts.getOrNull(0) ?: ""
            titlePart.isNotBlank() && list.size > 1
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
        }.sortedBy { it.titleKey.lowercase() }
    }
}
