package com.music.echo.notune.social.repository

import com.music.echo.notune.social.model.*
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
                username = "aarav_sharma",
                displayName = "Aarav Sharma",
                isOnline = true,
                presenceStateText = "🎧 Listening",
                currentTrackTitle = "Until I Found You",
                currentTrackArtist = "Stephen Sanchez",
                currentTrackThumbnail = "https://lh3.googleusercontent.com/wP01Z9pC-x2y",
                currentTrackId = "BddP6PYo2gs",
                lastActiveText = "Active Now",
                mutualFriendsCount = 8
            ),
            SocialFriend(
                id = "friend_2",
                username = "elena_rostova",
                displayName = "Elena Rostova",
                isOnline = true,
                presenceStateText = "🟢 Online",
                currentTrackTitle = "Starboy",
                currentTrackArtist = "The Weeknd",
                currentTrackThumbnail = "https://lh3.googleusercontent.com/starboy",
                currentTrackId = "34Na4j8AVgA",
                lastActiveText = "Active Now",
                mutualFriendsCount = 5
            ),
            SocialFriend(
                id = "friend_3",
                username = "rohan_verma",
                displayName = "Rohan Verma",
                isOnline = false,
                presenceStateText = "⚫ Offline",
                lastActiveText = "Active 15m ago",
                mutualFriendsCount = 12
            ),
            SocialFriend(
                id = "friend_4",
                username = "priya_ananth",
                displayName = "Priya Ananth",
                isOnline = true,
                presenceStateText = "🎵 Listening Together",
                currentTrackTitle = "Arabic Kuthu",
                currentTrackArtist = "Anirudh Ravichander",
                currentTrackThumbnail = "https://lh3.googleusercontent.com/arabickuthu",
                currentTrackId = "K9vJd1f8Nsw",
                lastActiveText = "Active Now",
                mutualFriendsCount = 6
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
                postType = SocialPostType.SONG,
                songId = "BddP6PYo2gs",
                songTitle = "Until I Found You",
                artistName = "Stephen Sanchez",
                albumName = "Easy on My Eyes",
                caption = "This song hits different at 2 AM 🌙✨",
                likesCount = 56,
                repostsCount = 12,
                activeReaction = PostReactionType.LOVE,
                isLikedByMe = true,
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
                postType = SocialPostType.LYRICS,
                songId = "K9vJd1f8Nsw",
                songTitle = "Arabic Kuthu",
                artistName = "Anirudh Ravichander",
                caption = "Best workout anthem ever! 💃🕺",
                lyricsSnippet = "Halamithi Habibo... Georgia wrap me up in all your love!",
                likesCount = 94,
                repostsCount = 28,
                activeReaction = PostReactionType.FIRE,
                isLikedByMe = true,
                comments = listOf(
                    SocialComment(id = "c3", authorName = "Rohan Verma", text = "That bass drop is insane!")
                ),
                timestampText = "4h ago"
            ),
            SocialPost(
                id = "post_103",
                authorId = "friend_2",
                authorName = "Elena Rostova",
                postType = SocialPostType.MOMENT,
                songId = "34Na4j8AVgA",
                songTitle = "Starboy",
                artistName = "The Weeknd",
                caption = "THIS PART AT 02:14 🔥⚡",
                momentTimestamp = "02:14",
                likesCount = 38,
                timestampText = "6h ago"
            )
        )
    )
    val posts: StateFlow<List<SocialPost>> = _posts.asStateFlow()

    private val _stories = MutableStateFlow<List<MusicStory>>(
        listOf(
            MusicStory(id = "st1", authorName = "Aarav Sharma", songTitle = "Until I Found You", artistName = "Stephen Sanchez", caption = "Late night retro vibes 🌙"),
            MusicStory(id = "st2", authorName = "Priya Ananth", songTitle = "Arabic Kuthu", artistName = "Anirudh", caption = "Energy booster 🔥")
        )
    )
    val stories: StateFlow<List<MusicStory>> = _stories.asStateFlow()

    private val _circles = MutableStateFlow<List<FriendCircle>>(
        listOf(
            FriendCircle(id = "circle_1", name = "Night Vibes Squad", emoji = "🌙", membersCount = 6, sharedPlaylistName = "Midnight Audiophile Mix"),
            FriendCircle(id = "circle_2", name = "Tamil Hits Family", emoji = "🔥", membersCount = 12, sharedPlaylistName = "Anirudh & AR Rahman Top Tracks")
        )
    )
    val circles: StateFlow<List<FriendCircle>> = _circles.asStateFlow()

    private val _polls = MutableStateFlow<List<MusicPoll>>(
        listOf(
            MusicPoll(
                id = "poll_1",
                question = "What should be our main queue anthem tonight?",
                creatorName = "Aarav Sharma",
                options = listOf(
                    MusicPollOption("opt1", "Anirudh Ravichander", votes = 14),
                    MusicPollOption("opt2", "A.R. Rahman", votes = 18),
                    MusicPollOption("opt3", "The Weeknd", votes = 9)
                ),
                totalVotes = 41
            )
        )
    )
    val polls: StateFlow<List<MusicPoll>> = _polls.asStateFlow()

    private val _directMessages = MutableStateFlow<List<DirectSongMessage>>(emptyList())
    val directMessages: StateFlow<List<DirectSongMessage>> = _directMessages.asStateFlow()

    fun reactToPost(postId: String, reaction: PostReactionType) {
        _posts.value = _posts.value.map { post ->
            if (post.id == postId) {
                val isSame = post.activeReaction == reaction
                val newReaction = if (isSame) null else reaction
                val newCount = if (!isSame) post.likesCount + 1 else (post.likesCount - 1).coerceAtLeast(0)
                post.copy(activeReaction = newReaction, isLikedByMe = newReaction != null, likesCount = newCount)
            } else post
        }
    }

    fun toggleLikePost(postId: String) {
        reactToPost(postId, PostReactionType.LOVE)
    }

    fun addComment(postId: String, text: String, authorName: String = "You") {
        if (text.isBlank()) return
        val newComment = SocialComment(id = "c_${System.currentTimeMillis()}", authorName = authorName, text = text.trim())
        _posts.value = _posts.value.map { post ->
            if (post.id == postId) post.copy(comments = post.comments + newComment) else post
        }
    }

    fun addCommentToPost(postId: String, comment: String) {
        addComment(postId, comment)
    }

    fun createPost(
        songId: String,
        title: String,
        artist: String,
        postType: SocialPostType = SocialPostType.SONG,
        album: String? = null,
        thumbnailUrl: String? = null,
        caption: String = "",
        lyricsSnippet: String? = null,
        momentTimestamp: String? = null
    ) {
        val newPost = SocialPost(
            id = "post_${System.currentTimeMillis()}",
            authorId = "user_me",
            authorName = "You",
            postType = postType,
            songId = songId,
            songTitle = title,
            artistName = artist,
            albumName = album,
            thumbnailUrl = thumbnailUrl,
            caption = caption,
            lyricsSnippet = lyricsSnippet,
            momentTimestamp = momentTimestamp,
            likesCount = 0,
            timestampText = "Just now"
        )
        _posts.value = listOf(newPost) + _posts.value
    }

    fun votePoll(pollId: String, optionId: String) {
        _posts.value // trigger recompose if needed
        _polls.value = _polls.value.map { poll ->
            if (poll.id == pollId && !poll.isVoted) {
                val newOptions = poll.options.map { opt ->
                    if (opt.id == optionId) opt.copy(votes = opt.votes + 1) else opt
                }
                poll.copy(options = newOptions, totalVotes = poll.totalVotes + 1, isVoted = true)
            } else poll
        }
    }

    fun sendSongToFriend(
        friendId: String,
        songId: String,
        title: String,
        artist: String,
        message: String? = null,
        isGift: Boolean = false
    ) {
        val friend = _friends.value.firstOrNull { it.id == friendId } ?: return
        val newMsg = DirectSongMessage(
            id = "msg_${System.currentTimeMillis()}",
            senderId = "user_me",
            senderName = "You",
            receiverId = friend.id,
            songId = songId,
            songTitle = title,
            artistName = artist,
            messageText = message,
            isGift = isGift
        )
        _directMessages.value = listOf(newMsg) + _directMessages.value
    }

    fun addFriend(username: String) {
        if (username.isBlank()) return
        val newFriend = SocialFriend(
            id = "friend_${System.currentTimeMillis()}",
            username = username.lowercase().trim(),
            displayName = username.trim(),
            isOnline = true,
            presenceStateText = "🟢 Online",
            lastActiveText = "Active Now"
        )
        _friends.value = listOf(newFriend) + _friends.value
    }
}
