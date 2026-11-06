package com.music.echo.playback

import echo.music.iad1tya.db.MusicDatabase
import echo.music.iad1tya.db.entities.Song
import echo.music.iad1tya.utils.isLocalMediaId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

object OfflinePlaybackGuard {

    private const val TAG = "OfflinePlaybackGuard"

    fun isLocalOrDownloadedMedia(mediaId: String?): Boolean {
        if (mediaId.isNullOrBlank()) return false
        return mediaId.isLocalMediaId() ||
                mediaId.startsWith("content://") ||
                mediaId.startsWith("file://") ||
                mediaId.startsWith("/")
    }

    suspend fun findLocalFallback(
        title: String?,
        artistName: String?,
        database: MusicDatabase
    ): Song? = withContext(Dispatchers.IO) {
        if (title.isNullOrBlank()) return@withContext null
        try {
            val localSongs = database.getLocalSongsSync()
            val cleanTitle = title.trim().lowercase()
            val cleanArtist = artistName?.trim()?.lowercase()

            // Exact title & artist match
            val exactMatch = localSongs.find { song ->
                val songTitle = song.song.title.lowercase()
                val artistMatch = cleanArtist == null || song.artists.any { it.name.lowercase().contains(cleanArtist) }
                songTitle == cleanTitle && artistMatch
            }
            if (exactMatch != null) {
                Timber.tag(TAG).d("Found local fallback for '$title': ${exactMatch.song.id}")
                return@withContext exactMatch
            }

            // Title match fallback
            val titleMatch = localSongs.find { song ->
                song.song.title.lowercase() == cleanTitle
            }
            if (titleMatch != null) {
                Timber.tag(TAG).d("Found title match local fallback for '$title': ${titleMatch.song.id}")
                return@withContext titleMatch
            }

            null
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error searching local fallback for '$title'")
            null
        }
    }
}
