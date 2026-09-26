package echo.music.iad1tya.notune.provider

enum class PlaybackType {
    AUTHORIZED_STREAM,
    OPEN_STREAM,
    USER_LIBRARY,
    EXTERNAL_PLAYBACK,
    UNAVAILABLE
}

data class TrackRights(
    val isStreamable: Boolean = true,
    val isDownloadable: Boolean = false,
    val attributionRequired: Boolean = false,
    val licenseType: String = "STANDARD"
)

data class UnifiedTrack(
    val id: String,
    val title: String,
    val artist: String,
    val album: String? = null,
    val thumbnailUri: String? = null,
    val durationSeconds: Int = 0,
    val playbackType: PlaybackType = PlaybackType.AUTHORIZED_STREAM,
    val providerName: String,
    val providerTrackId: String,
    val rights: TrackRights = TrackRights()
)

interface MusicProvider {
    val providerId: String
    val providerName: String
    suspend fun isAvailable(): Boolean
    suspend fun search(query: String): List<UnifiedTrack>
    suspend fun getTrackDetails(trackId: String): UnifiedTrack?
    suspend fun resolvePlaybackUri(trackId: String): Result<String>
}
