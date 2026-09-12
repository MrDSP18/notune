package echo.music.iad1tya.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "room_table")
data class RoomEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val roomType: String,
    val isEncrypted: Boolean,
    val memberLimit: Int,
    val hostId: String,
    val hostName: String,
    val genreTag: String?,
    val createdAt: Long
)

@Entity(tableName = "room_message_table")
data class RoomMessageEntity(
    @PrimaryKey val id: String,
    val roomId: String,
    val senderId: String,
    val senderName: String,
    val encryptedContent: String,
    val timestamp: Long,
    val isEncrypted: Boolean,
    val attachedSongId: String?,
    val attachedSongTitle: String?
)

@Entity(tableName = "room_member_table")
data class RoomMemberEntity(
    @PrimaryKey val id: String, // roomId_userId
    val roomId: String,
    val userId: String,
    val username: String,
    val role: String,
    val joinedAt: Long
)
