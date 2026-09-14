package echo.music.iad1tya.db.entities

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.LocalDateTime

@Immutable
@Entity(tableName = "social_user")
data class SocialUserEntity(
    @PrimaryKey val id: String,
    val username: String,
    val displayName: String,
    val avatarUrl: String? = null,
    val bio: String? = null,
    val friendsCount: Int = 0,
    val playlistsCount: Int = 0,
    val isPrivate: Boolean = false
) : Serializable

@Immutable
@Entity(tableName = "friendship")
data class FriendshipEntity(
    @PrimaryKey val id: String,
    val user1Id: String,
    val user2Id: String,
    val status: String = "ACCEPTED", // PENDING, ACCEPTED, BLOCKED
    val createdAt: LocalDateTime = LocalDateTime.now()
) : Serializable

@Immutable
@Entity(tableName = "user_presence")
data class UserPresenceEntity(
    @PrimaryKey val userId: String,
    val presenceState: String = "ONLINE", // ONLINE, LISTENING, LISTEN_TOGETHER, AWAY, OFFLINE, INVISIBLE
    val currentSongId: String? = null,
    val currentSongTitle: String? = null,
    val currentArtistName: String? = null,
    val currentThumbnailUrl: String? = null,
    val lastActiveTimestamp: LocalDateTime = LocalDateTime.now()
) : Serializable

@Immutable
@Entity(tableName = "social_post_v2")
data class SocialPostEntity(
    @PrimaryKey val id: String,
    val authorId: String,
    val authorName: String,
    val authorAvatarUrl: String? = null,
    val postType: String = "SONG", // SONG, ALBUM, PLAYLIST, LYRICS, MOMENT, MOOD, REPOST
    val songId: String? = null,
    val songTitle: String? = null,
    val artistName: String? = null,
    val albumName: String? = null,
    val thumbnailUrl: String? = null,
    val caption: String = "",
    val lyricsSnippet: String? = null,
    val momentTimestamp: String? = null,
    val moodEmoji: String? = null,
    val likesCount: Int = 0,
    val repostsCount: Int = 0,
    val createdAt: LocalDateTime = LocalDateTime.now()
) : Serializable

@Immutable
@Entity(tableName = "music_message")
data class MusicMessageEntity(
    @PrimaryKey val id: String,
    val senderId: String,
    val receiverId: String,
    val messageType: String = "TEXT", // TEXT, SONG, ALBUM, PLAYLIST, LYRIC, TIMESTAMP, GIFT
    val textContent: String? = null,
    val songId: String? = null,
    val songTitle: String? = null,
    val artistName: String? = null,
    val thumbnailUrl: String? = null,
    val timestampMoment: String? = null,
    val isScheduled: Boolean = false,
    val scheduledAt: LocalDateTime? = null,
    val createdAt: LocalDateTime = LocalDateTime.now()
) : Serializable

@Immutable
@Entity(tableName = "playlist_dna")
data class PlaylistDnaEntity(
    @PrimaryKey val playlistId: String,
    val heartbreakPercentage: Int = 0,
    val nightPercentage: Int = 0,
    val energyPercentage: Int = 0,
    val indiePercentage: Int = 0,
    val primaryMood: String = "Vibe"
) : Serializable
