package com.music.echo.localmedia

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import dagger.hilt.android.qualifiers.ApplicationContext
import echo.music.iad1tya.R
import echo.music.iad1tya.db.MusicDatabase
import echo.music.iad1tya.db.entities.AlbumArtistMap
import echo.music.iad1tya.db.entities.AlbumEntity
import echo.music.iad1tya.db.entities.ArtistEntity
import echo.music.iad1tya.db.entities.FormatEntity
import echo.music.iad1tya.db.entities.SongAlbumMap
import echo.music.iad1tya.db.entities.SongArtistMap
import echo.music.iad1tya.db.entities.SongEntity
import echo.music.iad1tya.localmedia.SupportedLocalAudio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SafFolderScanner @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: MusicDatabase,
) {
    private companion object {
        const val TAG = "SafFolderScanner"
    }

    suspend fun scanFolderUri(treeUri: Uri): Int = withContext(Dispatchers.IO) {
        val rootDir = DocumentFile.fromTreeUri(context, treeUri) ?: return@withContext 0
        val unknownArtist = context.getString(R.string.unknown_artist)
        val unknownTitle = context.getString(R.string.unknown)
        var addedCount = 0

        val audioFiles = mutableListOf<DocumentFile>()
        collectAudioFiles(rootDir, audioFiles)

        Timber.tag(TAG).d("Found ${audioFiles.size} audio files in SAF tree: $treeUri")

        val retriever = MediaMetadataRetriever()
        database.withTransaction {
            audioFiles.forEach { doc ->
                try {
                    val fileUri = doc.uri
                    val displayName = doc.name ?: "Unknown Track"
                    val mimeType = doc.type ?: "audio/*"

                    if (!SupportedLocalAudio.isSupported(displayName, mimeType)) return@forEach

                    retriever.setDataSource(context, fileUri)

                    val rawTitle = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                    val rawArtist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                    val rawAlbum = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
                    val durationMs = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
                    val year = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR)?.toIntOrNull()

                    val title = rawTitle?.trim()?.takeIf { it.isNotBlank() }
                        ?: displayName.substringBeforeLast('.').trim().ifBlank { unknownTitle }
                    val artistName = rawArtist?.trim()?.takeIf { it.isNotBlank() } ?: unknownArtist
                    val albumName = rawAlbum?.trim()?.takeIf { it.isNotBlank() }

                    val artistId = "LOCAL_ARTIST_${stableHash(artistName)}"
                    val albumId = albumName?.let { "LOCAL_ALBUM_${stableHash("$it|$artistId")}" }
                    val songId = fileUri.toString()

                    database.insert(
                        ArtistEntity(
                            id = artistId,
                            name = artistName,
                            lastUpdateTime = LocalDateTime.now(),
                            isLocal = true,
                        )
                    )

                    if (albumId != null) {
                        database.insert(
                            AlbumEntity(
                                id = albumId,
                                title = albumName,
                                year = year,
                                songCount = 1,
                                duration = (durationMs / 1000).toInt(),
                                lastUpdateTime = LocalDateTime.now(),
                                isLocal = true,
                            )
                        )
                        database.deleteAlbumArtistMapsByAlbumIds(listOf(albumId))
                        database.insert(AlbumArtistMap(albumId = albumId, artistId = artistId, order = 0))
                    }

                    database.insert(
                        SongEntity(
                            id = songId,
                            title = title,
                            duration = (durationMs / 1000).toInt(),
                            albumId = albumId,
                            albumName = albumName,
                            year = year,
                            dateModified = LocalDateTime.now(),
                            isLocal = true,
                        )
                    )

                    database.upsert(
                        FormatEntity(
                            id = songId,
                            itag = -1,
                            mimeType = mimeType,
                            codecs = "",
                            bitrate = 0,
                            sampleRate = null,
                            contentLength = doc.length(),
                            loudnessDb = null,
                            perceptualLoudnessDb = null,
                            playbackUrl = null,
                        )
                    )

                    database.deleteSongArtistMaps(songId)
                    database.insert(SongArtistMap(songId = songId, artistId = artistId, position = 0))

                    if (albumId != null) {
                        database.deleteSongAlbumMaps(songId)
                        database.insert(SongAlbumMap(songId = songId, albumId = albumId, index = 0))
                    }

                    addedCount++
                } catch (e: Exception) {
                    Timber.tag(TAG).e(e, "Error reading SAF file ${doc.uri}")
                }
            }
        }
        try { retriever.release() } catch (e: Exception) { /* Ignored */ }

        addedCount
    }

    private fun collectAudioFiles(dir: DocumentFile, result: MutableList<DocumentFile>) {
        val files = dir.listFiles()
        for (file in files) {
            if (file.isDirectory) {
                collectAudioFiles(file, result)
            } else if (file.isFile) {
                if (SupportedLocalAudio.isSupported(file.name, file.type)) {
                    result.add(file)
                }
            }
        }
    }

    private fun stableHash(source: String): String {
        return UUID.nameUUIDFromBytes(source.toByteArray(StandardCharsets.UTF_8))
            .toString()
            .replace("-", "")
    }
}
