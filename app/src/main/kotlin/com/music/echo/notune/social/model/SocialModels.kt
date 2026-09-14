package com.music.echo.notune.social.model

enum class SocialPostType {
    SONG,
    ALBUM,
    PLAYLIST,
    LYRICS,
    MOMENT,
    MOOD,
    REPOST
}

enum class PostReactionType(val emoji: String, val label: String) {
    LOVE("❤️", "Love"),
    FIRE("🔥", "Fire"),
    FEELS("🥹", "Feels"),
    MIND_BLOWN("🤯", "Mind Blown"),
    HURT("💔", "Hurt"),
    VIBE("🎧", "Vibe")
}

data class SocialFriend(
    val id: String,
    val username: String,
    val displayName: String = username,
    val avatarUrl: String? = null,
    val isOnline: Boolean = false,
    val presenceStateText: String = "🟢 Online",
    val currentTrackTitle: String? = null,
    val currentTrackArtist: String? = null,
    val currentTrackThumbnail: String? = null,
    val currentTrackId: String? = null,
    val lastActiveText: String = "Recently active",
    val mutualFriendsCount: Int = 3
)

data class DirectSongMessage(
    val id: String,
    val senderId: String,
    val senderName: String,
    val receiverId: String,
    val messageType: String = "SONG", // TEXT, SONG, ALBUM, PLAYLIST, LYRIC, TIMESTAMP, GIFT
    val songId: String? = null,
    val songTitle: String? = null,
    val artistName: String? = null,
    val albumName: String? = null,
    val thumbnailUrl: String? = null,
    val lyricSnippet: String? = null,
    val timestampMoment: String? = null,
    val messageText: String? = null,
    val isGift: Boolean = false,
    val isGiftOpened: Boolean = false,
    val timestampText: String = "Just now"
)

data class SocialComment(
    val id: String,
    val authorName: String,
    val authorAvatarUrl: String? = null,
    val text: String,
    val likesCount: Int = 0,
    val isLikedByMe: Boolean = false,
    val timestampText: String = "Just now"
)

data class SocialPost(
    val id: String,
    val authorId: String,
    val authorName: String,
    val authorAvatarUrl: String? = null,
    val postType: SocialPostType = SocialPostType.SONG,
    val songId: String? = null,
    val songTitle: String = "",
    val artistName: String = "",
    val albumName: String? = null,
    val thumbnailUrl: String? = null,
    val caption: String = "",
    val lyricsSnippet: String? = null,
    val momentTimestamp: String? = null,
    val moodEmoji: String? = null,
    val repostedByName: String? = null,
    val likesCount: Int = 0,
    val repostsCount: Int = 0,
    val activeReaction: PostReactionType? = null,
    val isLikedByMe: Boolean = false,
    val isSavedByMe: Boolean = false,
    val comments: List<SocialComment> = emptyList(),
    val timestampText: String = "Just now"
)

data class MusicStory(
    val id: String,
    val authorName: String,
    val authorAvatarUrl: String? = null,
    val songTitle: String,
    val artistName: String,
    val thumbnailUrl: String? = null,
    val caption: String? = null,
    val lyricsSnippet: String? = null,
    val timestampText: String = "2h ago"
)

data class FriendCircle(
    val id: String,
    val name: String,
    val emoji: String = "🎵",
    val membersCount: Int = 5,
    val sharedPlaylistName: String = "Squad Top Hits",
    val activeRoomCode: String? = null
)

data class MusicPoll(
    val id: String,
    val question: String,
    val creatorName: String,
    val options: List<MusicPollOption>,
    val totalVotes: Int = 0,
    val isVoted: Boolean = false
)

data class MusicPollOption(
    val id: String,
    val text: String,
    val votes: Int = 0
)

data class MusicChallenge(
    val dayNumber: Int,
    val title: String,
    val prompt: String,
    val completedSongTitle: String? = null
)
