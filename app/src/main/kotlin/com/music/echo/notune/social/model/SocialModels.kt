package com.music.echo.notune.social.model

data class SocialFriend(
    val id: String,
    val username: String,
    val avatarUrl: String? = null,
    val isOnline: Boolean = false,
    val currentTrackTitle: String? = null,
    val currentTrackArtist: String? = null,
    val currentTrackThumbnail: String? = null,
    val currentTrackId: String? = null,
    val lastActiveText: String = "Recently active"
)

data class DirectSongMessage(
    val id: String,
    val senderId: String,
    val senderName: String,
    val receiverId: String,
    val songId: String,
    val songTitle: String,
    val artistName: String,
    val albumName: String? = null,
    val thumbnailUrl: String? = null,
    val messageText: String? = null,
    val timestampText: String = "Just now"
)

data class SocialComment(
    val id: String,
    val authorName: String,
    val authorAvatarUrl: String? = null,
    val text: String,
    val timestampText: String = "Just now"
)

data class SocialPost(
    val id: String,
    val authorId: String,
    val authorName: String,
    val authorAvatarUrl: String? = null,
    val songId: String,
    val songTitle: String,
    val artistName: String,
    val albumName: String? = null,
    val thumbnailUrl: String? = null,
    val caption: String = "",
    val likesCount: Int = 0,
    val isLikedByMe: Boolean = false,
    val comments: List<SocialComment> = emptyList(),
    val timestampText: String = "Just now"
)
