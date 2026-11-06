package echo.music.iad1tya.models

import echo.music.iad1tya.db.entities.Song
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

data class LocalFolderNode(
    val path: String,
    val name: String,
    val songCount: Int,
    val directSongs: List<Song> = emptyList()
)

data class LocalDuplicateGroup(
    val titleKey: String,
    val primaryArtist: String,
    val duplicates: List<Song>
)

enum class LocalSmartPlaylistType {
    RECENTLY_ADDED,
    MOST_PLAYED,
    FAVORITES,
    NEVER_PLAYED
}
