package com.music.echo.localmedia

import echo.music.iad1tya.db.MusicDatabase
import echo.music.iad1tya.db.entities.ArtistEntity
import echo.music.iad1tya.db.entities.SongArtistMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.util.UUID

data class SongMetadataUpdate(
    val songId: String,
    val newTitle: String,
    val newArtistName: String,
    val newAlbumName: String?
)

data class MetadataUpdateResult(
    val success: Boolean,
    val updatedCount: Int,
    val isDatabaseSynced: Boolean,
    val isPhysicalFileUpdated: Boolean,
    val message: String? = null
)

object LocalMetadataEditor {

    suspend fun updateSongMetadata(
        songId: String,
        newTitle: String,
        newArtistName: String,
        newAlbumName: String?,
        database: MusicDatabase
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            var updated = false
            database.withTransaction {
                val existingSong = database.getSongsByIds(listOf(songId)).firstOrNull()
                if (existingSong == null) return@withTransaction

                val cleanTitle = newTitle.trim().ifBlank { existingSong.song.title }
                val cleanArtist = newArtistName.trim().ifBlank { "Unknown Artist" }
                val cleanAlbum = newAlbumName?.trim()?.takeIf { it.isNotBlank() }

                val artistId = "LOCAL_ARTIST_${stableHash(cleanArtist)}"

                database.insert(
                    ArtistEntity(
                        id = artistId,
                        name = cleanArtist,
                        lastUpdateTime = LocalDateTime.now(),
                        isLocal = true
                    )
                )

                database.update(
                    existingSong.song.copy(
                        title = cleanTitle,
                        albumName = cleanAlbum ?: existingSong.song.albumName
                    )
                )

                database.deleteSongArtistMaps(songId)
                database.insert(
                    SongArtistMap(
                        songId = songId,
                        artistId = artistId,
                        position = 0
                    )
                )

                updated = true
            }
            updated
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Updates song metadata and returns explicit status distinguishing database sync vs physical file tag state.
     * Prevents false reporting of physical file modification when only the Room database was updated.
     */
    suspend fun updateSongMetadataDetailed(
        songId: String,
        newTitle: String,
        newArtistName: String,
        newAlbumName: String?,
        database: MusicDatabase
    ): MetadataUpdateResult = withContext(Dispatchers.IO) {
        val success = updateSongMetadata(songId, newTitle, newArtistName, newAlbumName, database)
        if (success) {
            MetadataUpdateResult(
                success = true,
                updatedCount = 1,
                isDatabaseSynced = true,
                isPhysicalFileUpdated = false, // Direct disk tag write requires Scoped Storage write permission on Android 10+
                message = "Database metadata updated successfully. Physical audio tags preserved."
            )
        } else {
            MetadataUpdateResult(
                success = false,
                updatedCount = 0,
                isDatabaseSynced = false,
                isPhysicalFileUpdated = false,
                message = "Failed to update song metadata in local database."
            )
        }
    }

    /**
     * Atomically executes bulk updates across multiple songs. If an exception occurs,
     * the entire transaction is rolled back to protect database consistency.
     */
    suspend fun bulkUpdateSongMetadata(
        updates: List<SongMetadataUpdate>,
        database: MusicDatabase
    ): MetadataUpdateResult = withContext(Dispatchers.IO) {
        if (updates.isEmpty()) {
            return@withContext MetadataUpdateResult(
                success = true,
                updatedCount = 0,
                isDatabaseSynced = true,
                isPhysicalFileUpdated = false,
                message = "No updates provided."
            )
        }
        var count = 0
        try {
            database.withTransaction {
                for (update in updates) {
                    val existingSong = database.getSongsByIds(listOf(update.songId)).firstOrNull() ?: continue
                    val cleanTitle = update.newTitle.trim().ifBlank { existingSong.song.title }
                    val cleanArtist = update.newArtistName.trim().ifBlank { "Unknown Artist" }
                    val cleanAlbum = update.newAlbumName?.trim()?.takeIf { it.isNotBlank() }

                    val artistId = "LOCAL_ARTIST_${stableHash(cleanArtist)}"
                    database.insert(
                        ArtistEntity(
                            id = artistId,
                            name = cleanArtist,
                            lastUpdateTime = LocalDateTime.now(),
                            isLocal = true
                        )
                    )

                    database.update(
                        existingSong.song.copy(
                            title = cleanTitle,
                            albumName = cleanAlbum ?: existingSong.song.albumName
                        )
                    )

                    database.deleteSongArtistMaps(update.songId)
                    database.insert(
                        SongArtistMap(
                            songId = update.songId,
                            artistId = artistId,
                            position = 0
                        )
                    )
                    count++
                }
            }
            MetadataUpdateResult(
                success = true,
                updatedCount = count,
                isDatabaseSynced = true,
                isPhysicalFileUpdated = false,
                message = "Successfully updated $count tracks in local database."
            )
        } catch (e: Exception) {
            MetadataUpdateResult(
                success = false,
                updatedCount = 0,
                isDatabaseSynced = false,
                isPhysicalFileUpdated = false,
                message = "Transaction aborted during bulk update: ${e.message}"
            )
        }
    }

    private fun stableHash(source: String): String {
        return UUID.nameUUIDFromBytes(source.toByteArray(StandardCharsets.UTF_8))
            .toString()
            .replace("-", "")
    }
}
