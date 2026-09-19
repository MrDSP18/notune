package echo.music.iad1tya.notune.flow

import echo.music.iad1tya.models.MediaMetadata

data class LanguageSessionProfile(
    val primaryLanguage: String = "UNKNOWN",
    val secondaryLanguage: String? = null,
    val recentLanguageCounts: Map<String, Int> = emptyMap(),
    val confidence: Float = 0f
)

data class RecommendationHistoryRecord(
    val songId: String,
    val artistId: String? = null,
    val albumId: String? = null,
    val recommendedAtMs: Long = System.currentTimeMillis(),
    val timesRecommended: Int = 1,
    val skippedCount: Int = 0,
    val playedCount: Int = 0
)

data class AdaptiveQueueContext(
    val currentTrack: MediaMetadata? = null,
    val recentTracks: List<MediaMetadata> = emptyList(),
    val recentArtistIds: List<String> = emptyList(),
    val recentAlbumIds: List<String> = emptyList(),
    val recentLanguages: List<String> = emptyList(),
    val recentGenres: List<String> = emptyList(),
    val likedTrackIds: Set<String> = emptySet(),
    val skippedTrackIds: Map<String, Long> = emptyMap(),
    val completedTrackCounts: Map<String, Int> = emptyMap(),
    val replayedTrackCounts: Map<String, Int> = emptyMap(),
    val activeQueueTrackIds: List<String> = emptyList(),
    val recommendationHistory: Map<String, RecommendationHistoryRecord> = emptyMap(),
    val sessionLanguageProfile: LanguageSessionProfile = LanguageSessionProfile(),
    val recentSearchQueries: List<String> = emptyList()
)

data class AdaptiveQueueConfig(
    val targetQueueSize: Int = 15,
    val minQueueThreshold: Int = 5,
    val trackCooldownWindow: Int = 20,
    val artistCooldownWindow: Int = 4,
    val albumCooldownWindow: Int = 3,
    val languageCoherencyWeight: Float = 25f,
    val artistAffinityWeight: Float = 20f,
    val genreAffinityWeight: Float = 15f,
    val audioFeatureMatchWeight: Float = 15f,
    val maxArtistConsecutive: Int = 2,
    val maxAlbumConsecutive: Int = 1
)
