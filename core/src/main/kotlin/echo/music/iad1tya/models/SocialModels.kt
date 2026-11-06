package echo.music.iad1tya.models

import java.time.LocalDateTime

enum class PresenceState {
    OFFLINE, ONLINE, LISTENING, LISTEN_TOGETHER, AWAY, INVISIBLE
}

data class UserPresence(
    val userId: String,
    val state: PresenceState,
    val currentSong: MediaMetadata? = null,
    val lastActive: LocalDateTime
)

data class SocialUser(
    val id: String,
    val username: String,
    val displayName: String,
    val avatarUrl: String?,
    val bio: String?,
    val musicDnaSummary: Map<String, Float> = emptyMap(),
    val presence: UserPresence? = null,
    val isFriend: Boolean = false,
    val friendshipStatus: FriendshipStatus = FriendshipStatus.NONE
)

enum class FriendshipStatus {
    NONE, PENDING_SENT, PENDING_RECEIVED, ACCEPTED, BLOCKED
}

enum class PostType {
    SONG, ALBUM, PLAYLIST, LYRICS, MOMENT, MOOD, REPOST
}

data class SocialPost(
    val id: String,
    val author: SocialUser,
    val type: PostType,
    val content: PostContent,
    val caption: String,
    val likesCount: Int,
    val repostsCount: Int,
    val createdAt: LocalDateTime,
    val isLiked: Boolean = false
)

sealed class PostContent {
    data class Song(val metadata: MediaMetadata) : PostContent()
    data class Album(val id: String, val title: String, val artists: List<String>, val thumbnailUrl: String?) : PostContent()
    data class Playlist(val id: String, val title: String, val thumbnailUrl: String?) : PostContent()
    data class Lyrics(val song: MediaMetadata, val snippet: String) : PostContent()
    data class Moment(val song: MediaMetadata, val timestamp: Long) : PostContent()
    data class Mood(val emoji: String, val label: String) : PostContent()
}

enum class MessageType {
    TEXT, SONG, ALBUM, PLAYLIST, LYRIC, MOMENT, SUGGESTION, LISTEN_INVITE, ROOM_INVITE, GIFT
}

enum class MessageStatus {
    CREATED, SENDING, SENT, DELIVERED, READ, FAILED
}

data class Message(
    val id: String,
    val senderId: String,
    val receiverId: String,
    val type: MessageType,
    val status: MessageStatus,
    val content: String?, // For text or additional info
    val mediaMetadata: MediaMetadata? = null,
    val momentTimestamp: Long? = null,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
