package com.music.echo.notune.ai

import echo.music.iad1tya.db.entities.Song
import javax.inject.Inject
import javax.inject.Singleton

data class PlaylistDiagnosis(
    val healthScore: Int,
    val dominantArtist: String?,
    val issues: List<String>,
    val suggestions: List<String>
)

@Singleton
class AiPlaylistDoctor @Inject constructor() {

    fun diagnosePlaylist(playlistName: String, songs: List<Song>): PlaylistDiagnosis {
        if (songs.isEmpty()) {
            return PlaylistDiagnosis(
                healthScore = 100,
                dominantArtist = null,
                issues = emptyList(),
                suggestions = listOf("Add songs to evaluate playlist health.")
            )
        }

        val issues = mutableListOf<String>()
        val suggestions = mutableListOf<String>()
        var score = 100

        // 1. Check artist dominance
        val artistCounts = songs.groupBy { it.artists.joinToString { a -> a.name }.ifEmpty { "Unknown" } }
        val topArtistEntry = artistCounts.maxByOrNull { it.value.size }
        val topArtistCount = topArtistEntry?.value?.size ?: 0
        val topArtistName = topArtistEntry?.key

        if (topArtistCount > songs.size * 0.4 && songs.size > 5) {
            score -= 20
            issues.add("Artist '$topArtistName' dominates ${((topArtistCount.toFloat() / songs.size) * 100).toInt()}% of the playlist.")
            suggestions.add("Consider balancing with 3-5 tracks from complementary artists.")
        }

        // 2. Duplicate song titles check
        val duplicateTitles = songs.groupBy { it.song.title.lowercase() }.filter { it.value.size > 1 }
        if (duplicateTitles.isNotEmpty()) {
            score -= 15
            issues.add("Found ${duplicateTitles.size} duplicate track titles.")
            suggestions.add("Remove duplicate tracks to keep playback fresh.")
        }

        if (songs.size < 8) {
            score -= 10
            issues.add("Playlist is relatively short (${songs.size} tracks).")
            suggestions.add("Use NØTUNE FLOW candidate generator to discover 5 contextually matching tracks.")
        }

        return PlaylistDiagnosis(
            healthScore = score.coerceIn(0, 100),
            dominantArtist = topArtistName,
            issues = issues,
            suggestions = suggestions
        )
    }
}
