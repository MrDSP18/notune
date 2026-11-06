package echo.music.iad1tya.notune.flow

import echo.music.iad1tya.models.MediaMetadata
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlowFeedbackProcessor @Inject constructor(
    private val languageSessionTracker: LanguageSessionTracker
) {

    private val recentSkips = ConcurrentHashMap<String, Long>()
    private val completedCounts = ConcurrentHashMap<String, Int>()
    private val favoriteIds = ConcurrentHashMap.newKeySet<String>()
    private val boostedArtistIds = ConcurrentHashMap.newKeySet<String>()
    private val recentLanguages = mutableListOf<String>()
    private val recHistory = ConcurrentHashMap<String, RecommendationHistoryRecord>()
    private val recentSearchQueries = mutableListOf<String>()

    fun recordPlaybackFeedback(
        mediaMetadata: MediaMetadata,
        playedDurationMs: Long,
        totalDurationMs: Long
    ) {
        if (totalDurationMs <= 0) return
        val ratio = playedDurationMs.toFloat() / totalDurationMs.toFloat()

        val lang = languageSessionTracker.detectLanguage(mediaMetadata)
        if (lang != "UNKNOWN") {
            synchronized(recentLanguages) {
                recentLanguages.add(lang)
                if (recentLanguages.size > 15) {
                    recentLanguages.removeAt(0)
                }
            }
        }

        if (ratio < 0.15f) {
            // Early skip signal (<15% played)
            recentSkips[mediaMetadata.id] = System.currentTimeMillis()
            updateRecRecordSkipped(mediaMetadata.id)
        } else if (ratio >= 0.75f) {
            // Completion signal (>=75% played)
            recentSkips.remove(mediaMetadata.id)
            completedCounts[mediaMetadata.id] = (completedCounts[mediaMetadata.id] ?: 0) + 1
            updateRecRecordPlayed(mediaMetadata.id)
            mediaMetadata.artists.firstOrNull()?.id?.let { artistId ->
                boostedArtistIds.add(artistId)
            }
        }
    }

    fun recordSearchQuery(query: String) {
        if (query.isBlank()) return
        synchronized(recentSearchQueries) {
            recentSearchQueries.add(query)
            if (recentSearchQueries.size > 10) {
                recentSearchQueries.removeAt(0)
            }
        }
    }

    fun recordRecommendation(track: MediaMetadata) {
        val now = System.currentTimeMillis()
        val artistId = track.artists.firstOrNull()?.id
        val albumId = track.album?.id
        val existing = recHistory[track.id]

        if (existing != null) {
            recHistory[track.id] = existing.copy(
                recommendedAtMs = now,
                timesRecommended = existing.timesRecommended + 1
            )
        } else {
            recHistory[track.id] = RecommendationHistoryRecord(
                songId = track.id,
                artistId = artistId,
                albumId = albumId,
                recommendedAtMs = now,
                timesRecommended = 1
            )
        }
    }

    private fun updateRecRecordPlayed(songId: String) {
        recHistory[songId]?.let {
            recHistory[songId] = it.copy(playedCount = it.playedCount + 1)
        }
    }

    private fun updateRecRecordSkipped(songId: String) {
        recHistory[songId]?.let {
            recHistory[songId] = it.copy(skippedCount = it.skippedCount + 1)
        }
    }

    fun recordMoreLikeThis(mediaMetadata: MediaMetadata) {
        mediaMetadata.artists.firstOrNull()?.id?.let { artistId ->
            boostedArtistIds.add(artistId)
        }
    }

    fun recordLessLikeThis(mediaMetadata: MediaMetadata) {
        recentSkips[mediaMetadata.id] = System.currentTimeMillis()
        updateRecRecordSkipped(mediaMetadata.id)
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
        recentSkips.entries.removeIf { now - it.value > 3600_000L }
        return recentSkips.toMap()
    }

    fun getFavoriteTrackIds(): Set<String> {
        return favoriteIds.toSet()
    }

    fun getBoostedArtistIds(): List<String> {
        return boostedArtistIds.toList()
    }

    fun getRecentLanguages(): List<String> {
        synchronized(recentLanguages) {
            return recentLanguages.toList()
        }
    }

    fun getRecommendationHistory(): Map<String, RecommendationHistoryRecord> {
        return recHistory.toMap()
    }

    fun getCompletedTrackCounts(): Map<String, Int> {
        return completedCounts.toMap()
    }
}
