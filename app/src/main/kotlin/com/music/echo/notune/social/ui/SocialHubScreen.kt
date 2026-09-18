package com.music.echo.notune.social.ui

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import echo.music.iad1tya.R
import echo.music.iad1tya.models.*
import echo.music.iad1tya.ui.theme.NothingFont
import com.music.echo.notune.theme.NoTuneAmbientCanvas
import com.music.echo.notune.theme.NoTuneSurfaceCard
import com.music.echo.viewmodels.FriendsViewModel
import com.music.echo.viewmodels.SocialFeedViewModel
import echo.music.iad1tya.LocalPlayerConnection
import echo.music.iad1tya.constants.CardStyleVariant
import echo.music.iad1tya.playback.queues.YouTubeQueue

@Composable
fun SocialHubScreen(
    feedViewModel: SocialFeedViewModel = hiltViewModel(),
    friendsViewModel: FriendsViewModel = hiltViewModel(),
    onNavigateToListenTogether: (String?) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    val context = LocalContext.current
    val playerConnection = LocalPlayerConnection.current
    val friends by friendsViewModel.friends.collectAsState()
    val posts by feedViewModel.feed.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Feed, 1: Friends
    var showCreatePostModal by remember { mutableStateOf(false) }
    var showSendSongModalForFriend by remember { mutableStateOf<SocialUser?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
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
                    Icon(painterResource(R.drawable.arrow_back), contentDescription = null, tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "COMMUNITY // FRIENDS",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont, color = Color(0xFFFF0031), letterSpacing = 1.5.sp)
                    )
                    Text(
                        text = "NØTUNE Social OS",
                        style = MaterialTheme.typography.titleMedium.copy(fontFamily = NothingFont, fontWeight = FontWeight.Bold, color = Color.White)
                    )
                }

                FilledTonalButton(
                    onClick = { showCreatePostModal = true },
                    shape = RoundedCornerShape(2.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color.White.copy(alpha = 0.1f), contentColor = Color.White)
                ) {
                    Text("POST_SONG", style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont))
                }
            }

            // Tab Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(2.dp))
                    .padding(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (selectedTab == 0) Color(0xFFFF0031) else Color.Transparent)
                        .clickable { selectedTab = 0 }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "NETWORK_FEED",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont, fontWeight = FontWeight.Bold, color = if (selectedTab == 0) Color.White else Color.White.copy(alpha = 0.6f))
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (selectedTab == 1) Color(0xFFFF0031) else Color.Transparent)
                        .clickable { selectedTab = 1 }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "FRIEND_NODES",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont, fontWeight = FontWeight.Bold, color = if (selectedTab == 1) Color.White else Color.White.copy(alpha = 0.6f))
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Content Area
            if (selectedTab == 0) {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(posts, key = { it.id }) { post ->
                        SocialPostCard(
                            post = post,
                            onLike = { feedViewModel.likePost(post.id) },
                            onPlaySong = {
                                val content = post.content
                                if (content is PostContent.Song) {
                                    playerConnection?.playQueue(YouTubeQueue.radio(content.metadata))
                                }
                            }
                        )
                    }
                }
            } else {
                Column(modifier = Modifier.weight(1f)) {
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
    }
}

@Composable
private fun SocialPostCard(
    post: SocialPost,
    onLike: () -> Unit,
    onPlaySong: () -> Unit
) {
    NoTuneSurfaceCard(
        cardStyle = CardStyleVariant.GLASS,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.White.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(post.author.displayName.take(1).uppercase(), color = Color.White, style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(post.author.displayName.uppercase(), style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont, fontWeight = FontWeight.Bold, color = Color.White))
                    Text(post.createdAt.toString(), style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, color = Color.White.copy(alpha = 0.4f)))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (post.caption.isNotBlank()) {
                Text(
                    text = post.caption,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.8f)),
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // Song Content
            val content = post.content
            if (content is PostContent.Song) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.03f), RoundedCornerShape(2.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(2.dp))
                        .clickable { onPlaySong() }
                        .padding(8.dp)
                ) {
                    AsyncImage(
                        model = content.metadata.thumbnailUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(40.dp).clip(RoundedCornerShape(2.dp))
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(content.metadata.title.uppercase(), style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont, fontWeight = FontWeight.Bold, color = Color.White), maxLines = 1)
                        Text(content.metadata.artists.firstOrNull()?.name?.uppercase() ?: "UNKNOWN", style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont, fontSize = 9.sp, color = Color(0xFFFF0031)), maxLines = 1)
                    }

                    Icon(painterResource(R.drawable.play), contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                TextButton(onClick = onLike, contentPadding = PaddingValues(0.dp)) {
                    Text(text = if (post.isLiked) "❤️ ${post.likesCount}" else "🤍 ${post.likesCount}", style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont, color = Color.White))
                }
            }
        }
    }
}

@Composable
private fun FriendCard(
    friend: SocialUser,
    onInviteToRoom: () -> Unit,
    onSendSong: () -> Unit
) {
    NoTuneSurfaceCard(
        cardStyle = CardStyleVariant.GLASS,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(12.dp)) {
            Box {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.White.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(friend.displayName.take(1).uppercase(), color = Color.White, style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont))
                }

                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(if (friend.presence?.state == PresenceState.ONLINE || friend.presence?.state == PresenceState.LISTENING) Color(0xFF00E676) else Color.Gray)
                        .align(Alignment.BottomEnd)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(friend.displayName.uppercase(), style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont, fontWeight = FontWeight.Bold, color = Color.White))
                if (friend.presence?.state == PresenceState.LISTENING && friend.presence?.currentSong != null) {
                    Text(
                        text = "LISTENING: ${friend.presence?.currentSong?.title?.uppercase()}",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont, fontSize = 8.sp, color = Color(0xFFFF0031)),
                        maxLines = 1
                    )
                } else {
                    Text(
                        text = friend.presence?.state?.name ?: "OFFLINE",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont, fontSize = 8.sp, color = Color.White.copy(alpha = 0.4f))
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onInviteToRoom, modifier = Modifier.size(32.dp)) {
                    Icon(painterResource(R.drawable.radio), contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onSendSong, modifier = Modifier.size(32.dp)) {
                    Icon(painterResource(R.drawable.send_chat), contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
