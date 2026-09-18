package echo.music.iad1tya.models

import java.time.LocalDateTime

data class LocalSong(
    val id: String,
    val title: String,
    val artists: List<String>,
    val albumTitle: String?,
    val durationSeconds: Int,
    val thumbnailUrl: String?,
    val mimeType: String,
    val sizeBytes: Long,
    val dateModified: LocalDateTime?
)

data class LocalAlbum(
    val id: String,
    val title: String,
    val artists: List<String>,
    val songCount: Int,
    val durationSeconds: Int,
    val thumbnailUrl: String?
)

data class LocalArtist(
    val id: String,
    val name: String,
    val songCount: Int,
    val thumbnailUrl: String?
)
