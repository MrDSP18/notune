
package echo.music.iad1tya.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/** Stores the active Couple Mode pairing between two users. */
@Entity(tableName = "couple_session")
data class CoupleSessionEntity(
    @PrimaryKey val id: String,
    /** Local user's display name shown to partner. */
    val localAlias: String,
    /** Partner's display name. */
    val partnerAlias: String,
    /** Relationship start date (used for Anniversary Mode). */
    val relationshipStartDate: LocalDateTime? = null,
    /** Date when the Couple Mode pairing was created. */
    val pairedAt: LocalDateTime = LocalDateTime.now(),
    /** Whether the session is currently active. Only one session can be active at a time. */
    val isActive: Boolean = true,
    /** Shared secret/code used to pair devices (derived from QR or manual entry). */
    val pairingCode: String? = null,
)
