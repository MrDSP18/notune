package com.music.echo.notune.social.ui

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import echo.music.iad1tya.R
import com.music.echo.notune.social.model.SocialFriend
import com.music.echo.notune.social.model.SocialPost
import com.music.echo.notune.social.repository.SocialRepository
import com.music.echo.notune.theme.NoTuneAmbientCanvas
import com.music.echo.notune.theme.NoTuneSurfaceCard
import echo.music.iad1tya.LocalPlayerConnection
import echo.music.iad1tya.constants.CardStyleVariant
import echo.music.iad1tya.models.MediaMetadata
import echo.music.iad1tya.playback.queues.YouTubeQueue

@Composable
fun SocialHubScreen(
    socialRepository: SocialRepository = remember { SocialRepository() },
    onNavigateToListenTogether: (String?) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    val context = LocalContext.current
    val playerConnection = LocalPlayerConnection.current
    val friends by socialRepository.friends.collectAsState()
    val posts by socialRepository.posts.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Feed, 1: Friends
    var showCreatePostModal by remember { mutableStateOf(false) }
    var showSendSongModalForFriend by remember { mutableStateOf<SocialFriend?>(null) }
    var showAddFriendModal by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        NoTuneAmbientCanvas(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                IconButton(onClick = onDismiss) {
                    Text("✕", fontSize = 20.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "COMMUNITY & FRIENDS",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.5.sp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "NØTUNE Social Music Hub",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
                    )
                }

                FilledTonalButton(
                    onClick = { showCreatePostModal = true },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("➕ POST SONG", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            // Tab Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selectedTab == 0) MaterialTheme.colorScheme.primary else Color.Transparent)
                        .clickable { selectedTab = 0 }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🔥 MUSIC FEED (${posts.size})",
                        fontWeight = FontWeight.Bold,
                        color = if (selectedTab == 0) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selectedTab == 1) MaterialTheme.colorScheme.primary else Color.Transparent)
                        .clickable { selectedTab = 1 }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val onlineCount = friends.count { it.isOnline }
                    Text(
                        text = "👥 FRIENDS (🟢 $onlineCount)",
                        fontWeight = FontWeight.Bold,
                        color = if (selectedTab == 1) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Content Area
            if (selectedTab == 0) {
                // Social Feed View
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(posts, key = { it.id }) { post ->
                        SocialPostCard(
                            post = post,
                            onLike = { socialRepository.toggleLikePost(post.id) },
                            onAddComment = { comment -> socialRepository.addCommentToPost(post.id, comment) },
                            onPlaySong = {
                                post.songId?.let { sId ->
                                    val meta = MediaMetadata(
                                        id = sId,
                                        title = post.songTitle,
                                        artists = listOf(MediaMetadata.Artist(name = post.artistName, id = null)),
                                        duration = -1,
                                        thumbnailUrl = post.thumbnailUrl
                                    )
                                    playerConnection?.playQueue(YouTubeQueue.radio(meta))
                                }
                            },
                            onShare = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "🎵 Check out '${post.songTitle}' shared by ${post.authorName} on NØTUNE!\nhttps://music.youtube.com/watch?v=${post.songId}"
                                    )
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share Music Post"))
                            }
                        )
                    }
                }
            } else {
                // Friends Network View
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "FRIEND NETWORK",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = { showAddFriendModal = true }) {
                            Text("➕ Add Friend", fontWeight = FontWeight.Bold)
                        }
                    }

                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(friends, key = { it.id }) { friend ->
                            FriendCard(
                                friend = friend,
                                onInviteToRoom = { onNavigateToListenTogether(friend.username) },
                                onSendSong = { showSendSongModalForFriend = friend }
                            )
                        }
                    }
                }
            }
        }

        // Modals
        if (showCreatePostModal) {
            CreatePostDialog(
                onDismiss = { showCreatePostModal = false },
                onPostSubmitted = { songId, title, artist, caption ->
                    socialRepository.createPost(
                        songId = songId,
                        title = title,
                        artist = artist,
                        caption = caption
                    )
                    showCreatePostModal = false
                }
            )
        }

        if (showSendSongModalForFriend != null) {
            val friend = showSendSongModalForFriend!!
            SendSongDialog(
                friend = friend,
                onDismiss = { showSendSongModalForFriend = null },
                onSend = { songId, title, artist, note ->
                    socialRepository.sendSongToFriend(
                        friendId = friend.id,
                        songId = songId,
                        title = title,
                        artist = artist,
                        message = note
                    )
                    showSendSongModalForFriend = null
                }
            )
        }

        if (showAddFriendModal) {
            AddFriendDialog(
                onDismiss = { showAddFriendModal = false },
                onAdd = { name ->
                    socialRepository.addFriend(name)
                    showAddFriendModal = false
                }
            )
        }
    }
}

@Composable
private fun SocialPostCard(
    post: SocialPost,
    onLike: () -> Unit,
    onAddComment: (String) -> Unit,
    onPlaySong: () -> Unit,
    onShare: () -> Unit
) {
    var expandedComments by remember { mutableStateOf(false) }
    var commentInput by remember { mutableStateOf("") }

    NoTuneSurfaceCard(
        cardStyle = CardStyleVariant.GLASS,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // Author Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Color(0xFF00C6FF), Color(0xFF0072FF)))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(post.authorName.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(post.authorName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(post.timestampText, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Caption
            if (post.caption.isNotBlank()) {
                Text(
                    text = post.caption,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }

            // Song Card Container
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    .clickable { onPlaySong() }
                    .padding(10.dp)
            ) {
                if (!post.thumbnailUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = post.thumbnailUrl,
                        contentDescription = post.songTitle,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🎵", fontSize = 22.sp)
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(post.songTitle, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, maxLines = 1)
                    Text(post.artistName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                }

                FilledIconButton(
                    onClick = onPlaySong,
                    modifier = Modifier.size(36.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("▶", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Social Action Buttons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(onClick = onLike) {
                    Text(if (post.isLikedByMe) "❤️ ${post.likesCount}" else "🤍 ${post.likesCount}", fontWeight = FontWeight.Bold)
                }

                TextButton(onClick = { expandedComments = !expandedComments }) {
                    Text("💬 ${post.comments.size} Comments", fontWeight = FontWeight.Bold)
                }

                TextButton(onClick = onShare) {
                    Text("🔗 Share", fontWeight = FontWeight.Bold)
                }
            }

            // Comments Section
            AnimatedVisibility(visible = expandedComments) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                    post.comments.forEach { comment ->
                        Row(modifier = Modifier.padding(vertical = 4.dp)) {
                            Text("${comment.authorName}: ", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                            Text(comment.text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    ) {
                        OutlinedTextField(
                            value = commentInput,
                            onValueChange = { commentInput = it },
                            placeholder = { Text("Write a comment...", fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f).height(46.dp),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = {
                                if (commentInput.isNotBlank()) {
                                    onAddComment(commentInput)
                                    commentInput = ""
                                }
                            }
                        ) {
                            Text("📤", fontSize = 18.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FriendCard(
    friend: SocialFriend,
    onInviteToRoom: () -> Unit,
    onSendSong: () -> Unit
) {
    NoTuneSurfaceCard(
        cardStyle = CardStyleVariant.GLASS,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Color(0xFFFF007A), Color(0xFF9600FF)))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(friend.username.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
                }

                // Presence Dot
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(if (friend.isOnline) Color(0xFF00E676) else Color(0xFF9E9E9E))
                        .align(Alignment.BottomEnd)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(friend.username, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (friend.isOnline) "🟢 Online" else "⚪ ${friend.lastActiveText}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (friend.isOnline) Color(0xFF00E676) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (friend.currentTrackTitle != null) {
                    Text(
                        text = "🎧 ${friend.currentTrackTitle} • ${friend.currentTrackArtist}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                if (friend.isOnline) {
                    Button(
                        onClick = onInviteToRoom,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("📻 Room", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedButton(
                    onClick = onSendSong,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("🎵 Send", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun CreatePostDialog(
    onDismiss: () -> Unit,
    onPostSubmitted: (songId: String, title: String, artist: String, caption: String) -> Unit
) {
    var songId by remember { mutableStateOf("BddP6PYo2gs") }
    var title by remember { mutableStateOf("Kesariya") }
    var artist by remember { mutableStateOf("Arijit Singh") }
    var caption by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Share Music to Feed", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Song Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = artist,
                    onValueChange = { artist = it },
                    label = { Text("Artist Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = caption,
                    onValueChange = { caption = it },
                    label = { Text("Add a Caption...") },
                    modifier = Modifier.fillMaxWidth().height(100.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onPostSubmitted(songId, title, artist, caption)
                    }
                }
            ) {
                Text("Publish Post", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun SendSongDialog(
    friend: SocialFriend,
    onDismiss: () -> Unit,
    onSend: (songId: String, title: String, artist: String, note: String) -> Unit
) {
    var title by remember { mutableStateOf("Starboy") }
    var artist by remember { mutableStateOf("The Weeknd") }
    var note by remember { mutableStateOf("Listen to this awesome track!") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Send Song to ${friend.username}", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Song Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = artist,
                    onValueChange = { artist = it },
                    label = { Text("Artist Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Message Note") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSend("34Na4j8AVgA", title, artist, note)
                    }
                }
            ) {
                Text("Send Suggestion", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun AddFriendDialog(
    onDismiss: () -> Unit,
    onAdd: (username: String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Friend", fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Friend Username") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onAdd(name)
                    }
                }
            ) {
                Text("Add", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
