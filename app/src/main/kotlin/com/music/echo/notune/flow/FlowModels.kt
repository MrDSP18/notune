package echo.music.iad1tya.notune.flow

import echo.music.iad1tya.models.MediaMetadata

enum class FlowMode {
    AUTO_FLOW,
    SONG_RADIO,
    ARTIST_RADIO,
    MOOD_FLOW,
    DISCOVERY_FLOW,
    FAVORITES_FLOW,
    AI_FLOW,
    COUPLE_FLOW,
    ROOM_FLOW
}

enum class FlowContextMode {
    NORMAL,
    FOCUS,
    WORKOUT,
    RELAX,
    DRIVING,
    PARTY,
    SLEEP,
    DISCOVERY
}

enum class ItemSource {
    MANUAL,
    FLOW,
    PLAYLIST,
    ALBUM,
    ARTIST,
    ROOM,
    COUPLE,
    SYSTEM
}

enum class FlowReasonType {
    SIMILAR_ARTIST,
    GENRE_MATCH,
    FAVORITE_ARTIST,
    DISCOVERY,
    MOOD_MATCH,
    ENERGY_MATCH,
    PERSONAL_TASTE,
    REPLAY_AFFINITY,
    AI_RECOMMENDATION
}

data class FlowReason(
    val type: FlowReasonType,
    val description: String
)

data class FlowCandidate(
    val mediaMetadata: MediaMetadata,
    val candidateSource: String,
    val isLocal: Boolean = true,
    val isDownloaded: Boolean = false
)

data class FlowScore(
    val candidate: FlowCandidate,
    val totalScore: Float,
    val artistAffinity: Float = 0f,
    val genreAffinity: Float = 0f,
    val energyMatch: Float = 0f,
    val discoveryBonus: Float = 0f,
    val recentPlayPenalty: Float = 0f,
    val skipPenalty: Float = 0f,
    val repetitionPenalty: Float = 0f,
    val primaryReason: FlowReason
)

data class FlowQueueItem(
    val mediaMetadata: MediaMetadata,
    val source: ItemSource = ItemSource.FLOW,
    val isLocked: Boolean = false,
    val reason: FlowReason? = null,
    val generationId: Long = 0L
)

enum class GenerationState {
    IDLE,
    GENERATING,
    READY,
    DEGRADED,
    FAILED,
    OFFLINE
}

data class FlowState(
    val enabled: Boolean = true,
    val mode: FlowMode = FlowMode.AUTO_FLOW,
    val contextMode: FlowContextMode = FlowContextMode.NORMAL,
    val discoveryRatio: Float = 0.3f,
    val queueTarget: Int = 15,
    val queueMinimum: Int = 5,
    val currentTrackId: String? = null,
    val generationState: GenerationState = GenerationState.IDLE,
    val lastGenerationTime: Long = 0L,
    val generationId: Long = 0L
)

data class FlowConfig(
    val targetQueueSize: Int = 15,
    val minQueueSize: Int = 5,
    val artistRepetitionWindow: Int = 4,
    val trackRepetitionWindow: Int = 20,
    val albumRepetitionWindow: Int = 5,
    val defaultDiscoveryRatio: Float = 0.3f
)
