package echo.music.iad1tya.notune.rooms.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import echo.music.iad1tya.notune.rooms.RoomsViewModel
import echo.music.iad1tya.models.Room
import echo.music.iad1tya.ui.theme.NothingFont
import echo.music.iad1tya.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartyModeScreen(
    navController: NavController,
    room: Room,
    viewModel: RoomsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var messageText by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(room.name.uppercase(), style = MaterialTheme.typography.titleMedium.copy(fontFamily = NothingFont, fontWeight = FontWeight.Bold))
                        Text(
                            text = "HOST_NODE: ${room.hostId.take(8).uppercase()}",
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont, color = Color(0xFFFF0031))
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.Black)
                .padding(horizontal = 16.dp)
        ) {
            // Live Status
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("AUDIO_SYNC_STATUS", style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont, color = Color(0xFF69D6E2)))
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = room.playbackState.trackMetadata?.title?.uppercase() ?: "IDLE // WAITING_FOR_HOST",
                        style = MaterialTheme.typography.bodyMedium.copy(fontFamily = NothingFont, fontWeight = FontWeight.Bold, color = Color.White)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = Color(0xFFFF0031),
                indicator = { TabRowDefaults.SecondaryIndicator(Modifier.tabIndicatorOffset(selectedTab), color = Color(0xFFFF0031)) }
            ) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("QUEUE", style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont)) })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("COMM_LOG", style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont)) })
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (selectedTab == 0) {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(room.queue) { item ->
                        QueueItemRow(item = item)
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Messaging implementation placeholder
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
                            placeholder = { Text("ENTER_COMMAND", style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont)) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(2.dp),
                            colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color.White.copy(alpha = 0.2f), focusedBorderColor = Color(0xFFFF0031))
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
                            Icon(painterResource(id = R.drawable.send_chat), contentDescription = null, tint = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QueueItemRow(item: echo.music.iad1tya.models.RoomQueueItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.03f), RoundedCornerShape(2.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(item.metadata.title.uppercase(), style = MaterialTheme.typography.labelMedium.copy(fontFamily = NothingFont, fontWeight = FontWeight.Bold, color = Color.White))
            Text("ADDED_BY: ${item.addedBy.uppercase()}", style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont, fontSize = 8.sp, color = Color.White.copy(alpha = 0.4f)))
        }
    }
}
