package echo.music.iad1tya.notune.ai.adaptive

/**
 * Metadata for a track evaluated by the NØ AI recommendation engine.
 */
data class AdaptiveTrackContext(
    val trackId: String,
    val title: String,
    val artist: String,
    val album: String? = null,
    val genre: String? = null,
    val language: String? = null,
    val bpm: Float? = null,
    val valence: Float? = null,    // 0.0 (Sad/Melancholic) to 1.0 (Happy/Upbeat)
    val energy: Float? = null,     // 0.0 (Calm) to 1.0 (Energetic)
    val durationMs: Long = 0L
)

/**
 * Listen Together room synchronization context for multi-user consensus scoring.
 */
data class AdaptiveRoomContext(
    val roomId: String,
    val participantIds: List<String> = emptyList(),
    val isHost: Boolean = false,
    val roomGenres: List<String> = emptyList()
)

/**
 * Unified request payload passed to NØ AI recommendation engine.
 */
data class RecommendationRequest(
    val userId: String? = null,
    val sessionId: String = "",
    val currentTrack: AdaptiveTrackContext? = null,
    val currentQueue: List<AdaptiveTrackContext> = emptyList(),
    val history: List<AdaptiveTrackContext> = emptyList(),
    val skippedTracks: List<String> = emptyList(),
    val replayedTracks: List<String> = emptyList(),
    val likedTrackIds: Set<String> = emptySet(),
    val dislikedTrackIds: Set<String> = emptySet(),
    val playlistContextId: String? = null,
    val mood: String? = null,
    val energy: Float? = null,
    val tempoBpm: Float? = null,
    val genre: String? = null,
    val language: String? = null,
    val artistHistory: List<String> = emptyList(),
    val roomContext: AdaptiveRoomContext? = null,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * A track scored and ranked by the adaptive recommendation engine.
 */
data class ScoredTrack(
    val track: AdaptiveTrackContext,
    val score: Float,
    val reason: String,
    val similarity: Float,
    val confidence: Float = 1.0f
)

/**
 * Unified response contract returned by the NØ AI recommendation engine.
 */
data class RecommendationResponse(
    val recommendedTracks: List<ScoredTrack>,
    val scores: Map<String, Float>,
    val reasons: Map<String, String>,
    val confidence: Float,
    val model: ModelSource,
    val generatedAt: Long = System.currentTimeMillis()
)
