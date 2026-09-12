package com.music.echo.notune.cleaner

import echo.music.iad1tya.db.DatabaseDao
import echo.music.iad1tya.db.entities.Song
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

data class LibraryCleanSummary(
    val totalSongs: Int,
    val duplicateSongs: List<Song>,
    val missingArtworkCount: Int,
    val unplayedDownloadsCount: Int
)

@Singleton
class SmartLibraryCleaner @Inject constructor(
    private val databaseDao: DatabaseDao
) {
    suspend fun analyzeLibrary(): LibraryCleanSummary {
        val songs = databaseDao.events().first().map { it.song }.distinctBy { it.song.id }

        val duplicates = songs.groupBy { "${it.song.title.lowercase()}_${it.artists.joinToString { a -> a.name }.lowercase()}" }
            .filter { it.value.size > 1 }
            .flatMap { it.value.drop(1) }

        val missingArtwork = songs.count { it.thumbnailUrl.isNullOrBlank() }

        return LibraryCleanSummary(
            totalSongs = songs.size,
            duplicateSongs = duplicates,
            missingArtworkCount = missingArtwork,
            unplayedDownloadsCount = 0
        )
    }
}
