
package echo.music.iad1tya.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * Records a song designated as "Our Song" by a Couple Mode pair.
 * Multiple songs can be starred; the most recently starred is the primary one.
 */
@Entity(tableName = "our_song")
data class OurSongEntity(
    @PrimaryKey val id: String = java.util.UUID.randomUUID().toString(),
    val sessionId: String,
    val songId: String,
    val songTitle: String,
    val artistName: String,
    val thumbnailUrl: String? = null,
    /** Optional personal note attached to this song by the user. */
    val note: String? = null,
    val addedAt: LocalDateTime = LocalDateTime.now(),
    /** True if this is the primary "Our Song" for the session. */
    val isPrimary: Boolean = false,
)
