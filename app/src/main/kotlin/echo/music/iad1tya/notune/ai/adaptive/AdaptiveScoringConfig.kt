package echo.music.iad1tya.notune.ai.adaptive

/**
 * Configurable parameters governing candidate scoring and queue ranking.
 * Centralized without hardcoding values throughout the playback engine.
 */
data class AdaptiveScoringConfig(
    val skipPenalty: Float = -0.15f,
    val replayBoost: Float = 0.20f,
    val likeBoost: Float = 0.25f,
    val dislikePenalty: Float = -0.40f,
    val maxBpmTransitionDelta: Float = 0.15f, // 15% BPM delta limit
    val artistRepetitionLimit: Int = 2,
    val artistFatiguePenalty: Float = -0.20f,
    val genreFatiguePenalty: Float = -0.10f,
    val recentTrackRepetitionPenalty: Float = -0.50f,
    val albumRepetitionPenalty: Float = -0.15f,
    val similarityWeight: Float = 0.30f,
    val valenceMatchWeight: Float = 0.20f,
    val energyMatchWeight: Float = 0.20f,
    val tempoMatchWeight: Float = 0.15f,
    val userPreferenceWeight: Float = 0.15f,
    val composerMatchWeight: Float = 0.15f,
    val movieMatchWeight: Float = 0.20f,
    val singerMatchWeight: Float = 0.10f,
    val lyricistMatchWeight: Float = 0.05f
)
