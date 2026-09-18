package echo.music.iad1tya.models

sealed class AppEvent {
    data class PlaybackStarted(val song: MediaMetadata) : AppEvent()
    data class PlaybackPaused(val song: MediaMetadata, val position: Long) : AppEvent()
    data class PlaybackStopped(val song: MediaMetadata, val position: Long) : AppEvent()
    data class PlaybackCompleted(val song: MediaMetadata, val playTimeMs: Long) : AppEvent()
    data class SongSkipped(val song: MediaMetadata, val position: Long) : AppEvent()
    data class SongLiked(val song: MediaMetadata) : AppEvent()
    data class SongUnliked(val song: MediaMetadata) : AppEvent()
    data class SongDownloaded(val song: MediaMetadata) : AppEvent()
    data class SearchPerformed(val query: String) : AppEvent()
    data class Discovery(val song: MediaMetadata) : AppEvent()
}
