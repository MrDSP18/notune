package echo.music.iad1tya.notune.rooms.ui

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import echo.music.iad1tya.notune.rooms.RoomsViewModel
import echo.music.iad1tya.notune.rooms.models.QueueVoteItem
import echo.music.iad1tya.notune.rooms.models.RoomModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartyModeScreen(
    navController: NavController,
    room: RoomModel,
    viewModel: RoomsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var messageText by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) } // 0: Shared Queue, 1: Live Chat

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(room.name, fontWeight = FontWeight.Bold)
                            if (room.isEncrypted) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(uiState.securityBadgeText, fontSize = 11.sp, color = Color(0xFF4CAF50))
                            }
                        }
                        Text(
                            text = "Host: ${room.hostName} • ${room.type.name} MODE",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Live DJ Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("🎧 Live DJ Mode", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = room.currentSongTitle ?: "No song playing in room",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = room.currentSongArtist ?: "Tap to request a song",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                    IconButton(
                        onClick = { /* Toggle DJ mode */ },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        Text("▶️", fontSize = 20.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tab Selector
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent
            ) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Shared Queue 🗳️") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Live Chat 💬") })
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (selectedTab == 0) {
                // Shared Queue Tab
                if (uiState.queueVotes.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No songs in party queue yet.\nSuggest a song to start voting!",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.queueVotes) { item ->
                            QueueVoteRow(item = item, onVote = { viewModel.voteQueueSong(item.songId) })
                        }
                    }
                }
            } else {
                // Live Chat Tab
                Column(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.activeMessages) { msg ->
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(msg.senderName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text(msg.content, fontSize = 14.sp)
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            placeholder = { Text(if (room.isEncrypted) "Encrypted message... 🔐" else "Send a message...") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (messageText.isNotBlank()) {
                                    viewModel.sendMessage(messageText)
                                    messageText = ""
                                }
                            }
                        ) {
                            Text("➔", fontSize = 20.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QueueVoteRow(
    item: QueueVoteItem,
    onVote: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("${item.artist} • Added by ${item.addedByUsername}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onVote) {
                    Text(if (item.userVoted) "❤️" else "🤍", fontSize = 18.sp)
                }
                Text("${item.votes}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}
