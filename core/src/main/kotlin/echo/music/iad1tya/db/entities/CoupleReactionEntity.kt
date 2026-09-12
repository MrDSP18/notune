
package echo.music.iad1tya.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/** An emoji/text reaction left on a song by either user in a Couple Mode session. */
@Entity(tableName = "couple_reaction")
data class CoupleReactionEntity(
    @PrimaryKey val id: String = java.util.UUID.randomUUID().toString(),
    val sessionId: String,
    val songId: String,
    /** Emoji or short reaction text (max 4 chars). */
    val reaction: String,
    /** true = left by the local user, false = received from partner (synced). */
    val isLocal: Boolean,
    val reactedAt: LocalDateTime = LocalDateTime.now(),
)
