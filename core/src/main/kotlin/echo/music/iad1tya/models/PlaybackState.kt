package echo.music.iad1tya.models

import androidx.compose.runtime.Immutable
import echo.music.iad1tya.db.entities.Song

@Immutable
data class PlaybackState(
    val currentSong: MediaMetadata? = null,
    val isPlaying: Boolean = false,
    val position: Long = 0L,
    val duration: Long = 0L,
    val bufferedPosition: Long = 0L,
    val queue: List<MediaMetadata> = emptyList(),
    val queueIndex: Int = -1,
    val shuffleModeEnabled: Boolean = false,
    val repeatMode: Int = 0, // Player.REPEAT_MODE_OFF
    val volume: Float = 1f,
    val audioDevice: String? = null,
    val sampleRate: Int = 0,
    val bitDepth: Int = 0,
    val codec: String? = null,
    val bitrate: Int = 0,
    val isDownloaded: Boolean = false,
    val isFavorite: Boolean = false,
    val lyricsAvailable: Boolean = false
)
