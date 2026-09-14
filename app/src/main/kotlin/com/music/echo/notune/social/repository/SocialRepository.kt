package com.music.echo.notune.social.repository

import com.music.echo.notune.social.model.DirectSongMessage
import com.music.echo.notune.social.model.SocialComment
import com.music.echo.notune.social.model.SocialFriend
import com.music.echo.notune.social.model.SocialPost
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocialRepository @Inject constructor() {

    private val _friends = MutableStateFlow<List<SocialFriend>>(
        listOf(
            SocialFriend(
                id = "friend_1",
                username = "Aarav Sharma",
                isOnline = true,
                currentTrackTitle = "Kesariya",
                currentTrackArtist = "Arijit Singh",
                currentTrackThumbnail = "https://lh3.googleusercontent.com/wP01Z9pC-x2y",
                currentTrackId = "BddP6PYo2gs",
                lastActiveText = "Active Now"
            ),
            SocialFriend(
                id = "friend_2",
                username = "Elena Rostova",
                isOnline = true,
                currentTrackTitle = "Starboy",
                currentTrackArtist = "The Weeknd",
                currentTrackThumbnail = "https://lh3.googleusercontent.com/starboy",
                currentTrackId = "34Na4j8AVgA",
                lastActiveText = "Active Now"
            ),
            SocialFriend(
                id = "friend_3",
                username = "Rohan Verma",
                isOnline = false,
                currentTrackTitle = null,
                currentTrackArtist = null,
                lastActiveText = "Active 15m ago"
            ),
            SocialFriend(
                id = "friend_4",
                username = "Priya Ananth",
                isOnline = true,
                currentTrackTitle = "Arabic Kuthu",
                currentTrackArtist = "Anirudh Ravichander",
                currentTrackThumbnail = "https://lh3.googleusercontent.com/arabickuthu",
                currentTrackId = "K9vJd1f8Nsw",
                lastActiveText = "Active Now"
            ),
            SocialFriend(
                id = "friend_5",
                username = "Lucas Vance",
                isOnline = false,
                lastActiveText = "Active 2h ago"
            )
        )
    )
    val friends: StateFlow<List<SocialFriend>> = _friends.asStateFlow()

    private val _posts = MutableStateFlow<List<SocialPost>>(
        listOf(
            SocialPost(
                id = "post_101",
                authorId = "friend_1",
                authorName = "Aarav Sharma",
                songId = "BddP6PYo2gs",
                songTitle = "Kesariya",
                artistName = "Arijit Singh • Pritam",
                albumName = "Brahmastra OST",
                thumbnailUrl = "https://lh3.googleusercontent.com/wP01Z9pC-x2y",
                caption = "Late night vibes with this soulful masterpiece! 🎶✨",
                likesCount = 42,
                isLikedByMe = false,
                comments = listOf(
                    SocialComment(id = "c1", authorName = "Elena Rostova", text = "Absolute classic track! 🔥"),
                    SocialComment(id = "c2", authorName = "Priya Ananth", text = "On repeat every single day ❤️")
                ),
                timestampText = "2h ago"
            ),
            SocialPost(
                id = "post_102",
                authorId = "friend_4",
                authorName = "Priya Ananth",
                songId = "K9vJd1f8Nsw",
                songTitle = "Arabic Kuthu",
                artistName = "Anirudh Ravichander • Jonita Gandhi",
                albumName = "Beast",
                thumbnailUrl = "https://lh3.googleusercontent.com/arabickuthu",
                caption = "Can never stop dancing to this beat! Who's listening with me? 💃🕺",
                likesCount = 89,
                isLikedByMe = true,
                comments = listOf(
                    SocialComment(id = "c3", authorName = "Rohan Verma", text = "That bass drop hits different!")
                ),
                timestampText = "5h ago"
            )
        )
    )
    val posts: StateFlow<List<SocialPost>> = _posts.asStateFlow()

    private val _directMessages = MutableStateFlow<List<DirectSongMessage>>(emptyList())
    val directMessages: StateFlow<List<DirectSongMessage>> = _directMessages.asStateFlow()

    fun toggleLikePost(postId: String) {
        _posts.value = _posts.value.map { post ->
            if (post.id == postId) {
                val newLiked = !post.isLikedByMe
                val newCount = if (newLiked) post.likesCount + 1 else (post.likesCount - 1).coerceAtLeast(0)
                post.copy(isLikedByMe = newLiked, likesCount = newCount)
            } else {
                post
            }
        }
    }

    fun addCommentToPost(postId: String, commentText: String, authorName: String = "You") {
        if (commentText.isBlank()) return
        val newComment = SocialComment(
            id = "comment_${System.currentTimeMillis()}",
            authorName = authorName,
            text = commentText.trim(),
            timestampText = "Just now"
        )

        _posts.value = _posts.value.map { post ->
            if (post.id == postId) {
                post.copy(comments = post.comments + newComment)
            } else {
                post
            }
        }
    }

    fun createPost(
        songId: String,
        title: String,
        artist: String,
        album: String? = null,
        thumbnailUrl: String? = null,
        caption: String
    ) {
        val newPost = SocialPost(
            id = "post_${System.currentTimeMillis()}",
            authorId = "user_me",
            authorName = "You",
            songId = songId,
            songTitle = title,
            artistName = artist,
            albumName = album,
            thumbnailUrl = thumbnailUrl,
            caption = caption,
            likesCount = 0,
            isLikedByMe = false,
            comments = emptyList(),
            timestampText = "Just now"
        )
        _posts.value = listOf(newPost) + _posts.value
    }

    fun sendSongToFriend(
        friendId: String,
        songId: String,
        title: String,
        artist: String,
        thumbnailUrl: String? = null,
        message: String? = null
    ) {
        val friend = _friends.value.firstOrNull { it.id == friendId } ?: return
        val newMessage = DirectSongMessage(
            id = "msg_${System.currentTimeMillis()}",
            senderId = "user_me",
            senderName = "You",
            receiverId = friend.id,
            songId = songId,
            songTitle = title,
            artistName = artist,
            thumbnailUrl = thumbnailUrl,
            messageText = message,
            timestampText = "Just now"
        )
        _directMessages.value = listOf(newMessage) + _directMessages.value
    }

    fun addFriend(username: String) {
        if (username.isBlank()) return
        val newFriend = SocialFriend(
            id = "friend_${System.currentTimeMillis()}",
            username = username.trim(),
            isOnline = true,
            currentTrackTitle = "Exploring Music",
            currentTrackArtist = "NØTUNE",
            lastActiveText = "Active Now"
        )
        _friends.value = listOf(newFriend) + _friends.value
    }
}
