package com.music.echo.localmedia

import echo.music.iad1tya.db.MusicDatabase
import echo.music.iad1tya.db.entities.ArtistEntity
import echo.music.iad1tya.db.entities.SongArtistMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.util.UUID

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

    private fun stableHash(source: String): String {
        return UUID.nameUUIDFromBytes(source.toByteArray(StandardCharsets.UTF_8))
            .toString()
            .replace("-", "")
    }
}
