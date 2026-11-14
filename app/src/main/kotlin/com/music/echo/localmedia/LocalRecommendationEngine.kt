package com.music.echo.localmedia

import echo.music.iad1tya.db.MusicDatabase
import echo.music.iad1tya.db.entities.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.ln1p

object LocalRecommendationEngine {
    private const val DEFAULT_LIMIT = 30
    private const val ARTIST_DIVERSITY_PENALTY = 0.22
    private const val ALBUM_DIVERSITY_PENALTY = 0.12

    suspend fun getLocalRecommendations(database: MusicDatabase, limit: Int = DEFAULT_LIMIT): List<Song> =
        withContext(Dispatchers.IO) {
            if (limit <= 0) return@withContext emptyList()
            val songs = database.getLocalSongsSync().filterNot { it.song.hideFromQuickPicks }
            if (songs.isEmpty()) return@withContext emptyList()
            rankSongs(songs, limit)
        }

    internal fun rankSongs(songs: List<Song>, limit: Int): List<Song> {
        if (limit <= 0 || songs.isEmpty()) return emptyList()

        val artistTotals = mutableMapOf<String, Long>()
        val albumTotals = mutableMapOf<String, Long>()
        songs.forEach { item ->
            val playTime = item.song.totalPlayTime.coerceAtLeast(0L)
            item.artists.forEach { artist ->
                val key = artist.name.trim().lowercase()
                if (key.isNotEmpty()) artistTotals[key] = (artistTotals[key] ?: 0L) + playTime
            }
            val album = item.song.albumName?.trim()?.lowercase().orEmpty()
            if (album.isNotEmpty()) albumTotals[album] = (albumTotals[album] ?: 0L) + playTime
        }

        val maxArtist = artistTotals.values.maxOrNull()?.let { ln1p(it.toDouble()) } ?: 1.0
        val maxAlbum = albumTotals.values.maxOrNull()?.let { ln1p(it.toDouble()) } ?: 1.0
        val maxTrack = songs.maxOfOrNull { ln1p(it.song.totalPlayTime.coerceAtLeast(0L).toDouble()) } ?: 1.0

        val candidates = songs.map { item ->
            val artistAffinity = item.artists.mapNotNull { artistTotals[it.name.trim().lowercase()] }
                .maxOrNull()?.let { (ln1p(it.toDouble()) / maxArtist).coerceIn(0.0, 1.0) } ?: 0.0
            val albumKey = item.song.albumName?.trim()?.lowercase().orEmpty()
            val albumAffinity = albumTotals[albumKey]?.let {
                (ln1p(it.toDouble()) / maxAlbum).coerceIn(0.0, 1.0)
            } ?: 0.0
            val familiarity = (ln1p(item.song.totalPlayTime.coerceAtLeast(0L).toDouble()) / maxTrack)
                .coerceIn(0.0, 1.0)
            val discovery = 1.0 - familiarity
            val score = artistAffinity * 0.38 +
                albumAffinity * 0.14 +
                (if (item.song.liked) 0.22 else 0.0) +
                familiarity * 0.08 +
                discovery * 0.18
            item to score
        }.sortedWith(
            compareByDescending<Pair<Song, Double>> { it.second }
                .thenBy { it.first.song.title.lowercase() }
                .thenBy { it.first.song.id }
        )

        val remaining = candidates.toMutableList()
        val result = ArrayList<Song>(minOf(limit, remaining.size))
        val artistCounts = mutableMapOf<String, Int>()
        val albumCounts = mutableMapOf<String, Int>()

        while (remaining.isNotEmpty() && result.size < limit) {
            val bestIndex = remaining.indices.maxWithOrNull(
                compareBy<Int> { index ->
                    val item = remaining[index].first
                    val artist = item.artists.firstOrNull()?.name?.trim()?.lowercase().orEmpty()
                    val album = item.song.albumName?.trim()?.lowercase().orEmpty()
                    remaining[index].second -
                        (artistCounts[artist] ?: 0) * ARTIST_DIVERSITY_PENALTY -
                        (albumCounts[album] ?: 0) * ALBUM_DIVERSITY_PENALTY
                }.thenBy { -it }
            ) ?: break

            val selected = remaining.removeAt(bestIndex).first
            result += selected
            val artist = selected.artists.firstOrNull()?.name?.trim()?.lowercase().orEmpty()
            val album = selected.song.albumName?.trim()?.lowercase().orEmpty()
            if (artist.isNotEmpty()) artistCounts[artist] = (artistCounts[artist] ?: 0) + 1
            if (album.isNotEmpty()) albumCounts[album] = (albumCounts[album] ?: 0) + 1
        }
        return result
    }
}
