package echo.music.iad1tya.notune.rooms.models

enum class RoomType {
    PUBLIC,
    PRIVATE,
    FRIENDS,
    PARTY,
    COUPLE;

    /**
     * P0-5: Strict room capacity limits enforced on server and client.
     */
    fun getMaxCapacity(): Int = when (this) {
        PUBLIC -> 500
        PRIVATE -> 50
        FRIENDS -> 6
        PARTY -> 20
        COUPLE -> 2
    }

    /**
     * P0-1: Determines if E2E key exchange and AES-256-GCM payload encryption are required.
     */
    fun isE2EEncryptedByDefault(): Boolean = this == PRIVATE || this == COUPLE
}


enum class MemberRole {
    OWNER,
    ADMIN,
    MODERATOR,
    MEMBER,
    GUEST
}

data class RoomModel(
    val id: String,
    val name: String,
    val description: String,
    val type: RoomType,
    val isEncrypted: Boolean = false,
    val memberLimit: Int = 100,
    val currentMemberCount: Int = 1,
    val hostId: String,
    val hostName: String,
    val currentSongTitle: String? = null,
    val currentSongArtist: String? = null,
    val currentSongId: String? = null,
    val genreTag: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

data class RoomMember(
    val userId: String,
    val username: String,
    val avatarUrl: String? = null,
    val role: MemberRole = MemberRole.MEMBER,
    val isListening: Boolean = true,
    val joinedAt: Long = System.currentTimeMillis()
)

data class RoomMessage(
    val id: String,
    val roomId: String,
    val senderId: String,
    val senderName: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isEncrypted: Boolean = false,
    val attachedSongId: String? = null,
    val attachedSongTitle: String? = null
)

data class QueueVoteItem(
    val songId: String,
    val title: String,
    val artist: String,
    val addedByUserId: String,
    val addedByUsername: String,
    val votes: Int = 0,
    val userVoted: Boolean = false
)
