package echo.music.iad1tya.notune.flow

import echo.music.iad1tya.models.MediaMetadata
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlowFeedbackProcessor @Inject constructor() {

    private val recentSkips = ConcurrentHashMap<String, Long>()
    private val favoriteIds = ConcurrentHashMap.newKeySet<String>()
    private val boostedArtistIds = ConcurrentHashMap.newKeySet<String>()

    fun recordPlaybackFeedback(
        mediaMetadata: MediaMetadata,
        playedDurationMs: Long,
        totalDurationMs: Long
    ) {
        if (totalDurationMs <= 0) return
        val ratio = playedDurationMs.toFloat() / totalDurationMs.toFloat()

        if (ratio < 0.15f) {
            // Early skip signal (<15% played)
            recentSkips[mediaMetadata.id] = System.currentTimeMillis()
        } else if (ratio >= 0.85f) {
            // Completion signal (>=85% played)
            recentSkips.remove(mediaMetadata.id)
            mediaMetadata.artists.firstOrNull()?.id?.let { artistId ->
                boostedArtistIds.add(artistId)
            }
        }
    }

    fun recordMoreLikeThis(mediaMetadata: MediaMetadata) {
        mediaMetadata.artists.firstOrNull()?.id?.let { artistId ->
            boostedArtistIds.add(artistId)
        }
    }

    fun recordLessLikeThis(mediaMetadata: MediaMetadata) {
        recentSkips[mediaMetadata.id] = System.currentTimeMillis()
    }

    fun recordFavorite(trackId: String, isFavorite: Boolean) {
        if (isFavorite) {
            favoriteIds.add(trackId)
        } else {
            favoriteIds.remove(trackId)
        }
    }

    fun getRecentlySkippedTrackIds(): Map<String, Long> {
        val now = System.currentTimeMillis()
        // Decay skips older than 1 hour (3600_000 ms)
        recentSkips.entries.removeIf { now - it.value > 3600_000L }
        return recentSkips.toMap()
    }

    fun getFavoriteTrackIds(): Set<String> {
        return favoriteIds.toSet()
    }

    fun getBoostedArtistIds(): List<String> {
        return boostedArtistIds.toList()
    }
}
