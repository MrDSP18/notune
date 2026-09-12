
package echo.music.iad1tya.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * A time-locked "memory capsule" containing a song, voice note, and/or photo URI
 * that can only be opened after a specific unlock date.
 */
@Entity(tableName = "memory_capsule")
data class MemoryCapsuleEntity(
    @PrimaryKey val id: String = java.util.UUID.randomUUID().toString(),
    val sessionId: String,
    /** Song associated with this memory. */
    val songId: String? = null,
    val songTitle: String? = null,
    val artistName: String? = null,
    /** A short message / dedication text. */
    val message: String? = null,
    /** URI to a local voice note audio file (nullable). */
    val voiceNoteUri: String? = null,
    /** URI to a local photo (nullable). */
    val photoUri: String? = null,
    /** When the capsule was created. */
    val createdAt: LocalDateTime = LocalDateTime.now(),
    /** The date after which the capsule can be opened (nullable = always openable). */
    val unlockAt: LocalDateTime? = null,
    /** Whether the capsule has been opened. */
    val isOpened: Boolean = false,
    val openedAt: LocalDateTime? = null,
)
