package echo.music.iad1tya.notune.ai.context

import echo.music.iad1tya.models.MediaMetadata
import echo.music.iad1tya.models.PlaybackState
import androidx.compose.runtime.Immutable

@Immutable
data class PlaybackContext(
    val currentTrack: MediaMetadata? = null,
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val queueSize: Int = 0,
    val queueIndex: Int = -1,
    val shuffleMode: Boolean = false,
    val repeatMode: Int = 0
)

@Immutable
data class UserContext(
    val userId: String = "guest",
    val preferredLanguages: List<String> = listOf("Tamil", "English"),
    val favoriteGenres: List<String> = emptyList(),
    val listeningStats: Map<String, Int> = emptyMap()
)

@Immutable
data class DeviceContext(
    val isHeadphonesConnected: Boolean = false,
    val audioOutputDevice: String = "INTERNAL_SPEAKER",
    val batteryPercent: Int = 100,
    val isOnWifi: Boolean = true
)

@Immutable
data class RoomContext(
    val inRoom: Boolean = false,
    val roomId: String? = null,
    val isHost: Boolean = false,
    val listenerCount: Int = 0
)

@Immutable
data class NotuneContext(
    val playback: PlaybackContext = PlaybackContext(),
    val user: UserContext = UserContext(),
    val device: DeviceContext = DeviceContext(),
    val room: RoomContext = RoomContext(),
    val timestamp: Long = System.currentTimeMillis()
)
