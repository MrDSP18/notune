package com.music.echo.notune.social.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.music.echo.notune.theme.NoTuneAmbientCanvas
import com.music.echo.notune.theme.NoTuneSurfaceCard
import com.music.echo.viewmodels.FriendsViewModel
import echo.music.iad1tya.constants.CardStyleVariant
import echo.music.iad1tya.models.SocialUser

@Composable
fun FriendCirclesScreen(
    friendsViewModel: FriendsViewModel = hiltViewModel(),
    onNavigateToListenTogether: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    val friends by friendsViewModel.friends.collectAsState()
    val requests by friendsViewModel.friendRequests.collectAsState()
    val mutationError by friendsViewModel.mutationError.collectAsState()

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
            // Top Bar
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
                        text = "FRIEND CIRCLES & POLLS",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.5.sp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text("Squad Groups & Music Challenges", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                mutationError?.let { error ->
                    item {
                        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(error, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.error)
                                TextButton(onClick = friendsViewModel::clearMutationError) { Text("DISMISS") }
                            }
                        }
                    }
                }
                item {
                    Text("FRIEND NODES", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }

                if (friends.isEmpty()) {
                    item {
                        SocialEmptyState("No friends are cached on this device yet.")
                    }
                } else {
                    items(friends, key = { it.id }) { friend ->
                        FriendNode(friend = friend, onNavigateToListenTogether = onNavigateToListenTogether)
                    }
                }

                if (requests.isNotEmpty()) {
                    item {
                        Text("FRIEND REQUESTS", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    items(requests, key = { it.id }) { request ->
                        FriendRequestNode(
                            request = request,
                            onAccept = { friendsViewModel.acceptRequest(request.id) },
                            onReject = { friendsViewModel.rejectRequest(request.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FriendNode(friend: SocialUser, onNavigateToListenTogether: () -> Unit) {
    NoTuneSurfaceCard(cardStyle = CardStyleVariant.GLASS, modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(friend.displayName.take(1).uppercase(), fontSize = 28.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(friend.displayName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(friend.presence?.state?.name ?: "OFFLINE", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Button(onClick = onNavigateToListenTogether) {
                Text("ROOM", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun FriendRequestNode(request: SocialUser, onAccept: () -> Unit, onReject: () -> Unit) {
    NoTuneSurfaceCard(cardStyle = CardStyleVariant.GLASS, modifier = Modifier.fillMaxWidth()) {
        Column {
            Text(request.displayName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onAccept) { Text("ACCEPT") }
                OutlinedButton(onClick = onReject) { Text("REJECT") }
            }
        }
    }
}

@Composable
private fun SocialEmptyState(message: String) {
    NoTuneSurfaceCard(cardStyle = CardStyleVariant.GLASS, modifier = Modifier.fillMaxWidth()) {
        Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
