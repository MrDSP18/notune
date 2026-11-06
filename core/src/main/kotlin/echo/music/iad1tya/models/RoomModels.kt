package echo.music.iad1tya.models

import java.time.LocalDateTime

enum class RoomType {
    PUBLIC, FRIENDS, PRIVATE, COUPLE
}

enum class RoomRole {
    HOST, MEMBER
}

enum class RoomConnectionState {
    IDLE, CREATING, CONNECTING, AUTHENTICATING, JOINING, SYNCING, CONNECTED, RECONNECTING, DISCONNECTED, LEAVING, ENDED, ERROR
}

data class RoomMember(
    val userId: String,
    val username: String,
    val role: RoomRole,
    val isConnected: Boolean = true,
    val presence: PresenceState = PresenceState.ONLINE
)

data class RoomPlaybackState(
    val currentTrackId: String? = null,
    val trackMetadata: MediaMetadata? = null,
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    val lastUpdateServerTime: Long = 0L,
    val playbackVersion: Int = 0
)

data class RoomQueueItem(
    val id: String,
    val metadata: MediaMetadata,
    val addedBy: String,
    val version: Int = 0
)

data class Room(
    val roomId: String,
    val name: String,
    val type: RoomType,
    val hostId: String,
    val members: List<RoomMember> = emptyList(),
    val playbackState: RoomPlaybackState = RoomPlaybackState(),
    val queue: List<RoomQueueItem> = emptyList(),
    val sessionToken: String? = null
)

data class RoomSyncState(
    val driftMs: Long = 0L,
    val latencyMs: Long = 0L,
    val serverTimeOffsetMs: Long = 0L
)
