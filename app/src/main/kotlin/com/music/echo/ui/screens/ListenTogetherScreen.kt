package echo.music.iad1tya.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import echo.music.iad1tya.LocalPlayerAwareWindowInsets
import echo.music.iad1tya.R
import echo.music.iad1tya.constants.AppBarHeight
import echo.music.iad1tya.listentogether.ChatMessagePayload
import echo.music.iad1tya.models.*
import echo.music.iad1tya.ui.theme.NothingFont
import echo.music.iad1tya.viewmodels.ListenTogetherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListenTogetherScreen(
    navController: NavController,
    viewModel: ListenTogetherViewModel = hiltViewModel(),
    showTopBar: Boolean = false
) {
    val context = LocalContext.current
    val windowInsets = LocalPlayerAwareWindowInsets.current

    val connectionState by viewModel.connectionState.collectAsState()
    val room by viewModel.currentRoom.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()

    var chatInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .statusBarsPadding(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 16.dp,
            bottom = 16.dp + AppBarHeight
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            com.music.echo.notune.theme.SystemTelemetryHeader(
                title = "NØTUNE // LISTEN TOGETHER NODE",
                subtitle = "Active Multi-User Audio Sync"
            )
        }

        if (room != null) {
            item {
                RoomInfoCard(
                    room = room!!,
                    onLeave = { viewModel.leaveRoom() },
                    onShare = {
                        val shareText = "🎵 Join my NØTUNE listen-along room: https://notune.app/room/${room!!.roomId} (Code: ${room!!.roomId})"
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("NØTUNE Room Code", room!!.roomId))
                        Toast.makeText(context, "Room code copied! Opening share menu...", Toast.LENGTH_SHORT).show()

                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Share NØTUNE Room"))
                    }
                )
            }

            item {
                Text(
                    text = "CONNECTED LISTENERS",
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont, color = Color(0xFFFF0031))
                )
            }

            items(room!!.members) { member ->
                MemberCard(member = member)
            }

            // Real-Time Room Chat & Interaction Stage
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.04f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "LIVE ROOM CHAT & REACTIONS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = NothingFont,
                                    color = Color(0xFF69D6E2),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "${chatMessages.size} MSGS",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = Color.White.copy(alpha = 0.5f))
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick Reaction Bar
                        Text(
                            text = "QUICK REACTIONS",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, color = Color.White.copy(alpha = 0.4f))
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        val reactions = listOf("🔥", "❤️", "🎵", "👏", "🚀", "✨")
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(reactions) { rx ->
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(Color.White.copy(alpha = 0.08f), CircleShape)
                                        .clickable {
                                            viewModel.sendChatMessage("REACTION:$rx")
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = rx, fontSize = 16.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Chat Message List
                        if (chatMessages.isEmpty()) {
                            Text(
                                text = "No messages yet. Send a reaction or message to your listeners!",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.4f)),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        } else {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 200.dp)
                            ) {
                                chatMessages.takeLast(10).forEach { msg ->
                                    val isReaction = msg.message.startsWith("REACTION:")
                                    val displayMsg = if (isReaction) msg.message.removePrefix("REACTION:") else msg.message
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color.White.copy(alpha = 0.03f), RoundedCornerShape(2.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = msg.username.uppercase(),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = NothingFont,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFFF0031)
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = displayMsg,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = if (isReaction) Color(0xFF69D6E2) else Color.White
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Message Input Field
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = chatInput,
                                onValueChange = { chatInput = it },
                                placeholder = { Text("Say something...", fontSize = 12.sp) },
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                                    focusedBorderColor = Color(0xFF69D6E2)
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (chatInput.isNotBlank()) {
                                        viewModel.sendChatMessage(chatInput.trim())
                                        chatInput = ""
                                    }
                                },
                                shape = RoundedCornerShape(2.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0031))
                            ) {
                                Text("SEND", style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont))
                            }
                        }
                    }
                }
            }
        } else {
            item {
                CreateJoinSection(
                    onCreate = { name, type -> viewModel.createRoom(name, type) },
                    onJoin = { viewModel.joinRoom(it) }
                )
            }
        }
    }
}

@Composable
private fun RoomInfoCard(room: Room, onLeave: () -> Unit, onShare: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ROOM_CODE: ${room.roomId}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = NothingFont,
                        color = Color(0xFF69D6E2),
                        fontWeight = FontWeight.Bold
                    )
                )

                // Room Type Badge
                val (typeLabel, typeColor) = when (room.type) {
                    RoomType.COUPLE -> "💑 COUPLE ROOM" to Color(0xFFFF4081)
                    RoomType.TEAM -> "🛡️ TEAM ROOM" to Color(0xFF00E5FF)
                    RoomType.FRIENDS -> "🎉 FRIENDS ROOM" to Color(0xFF00E676)
                    else -> "🌐 PUBLIC ROOM" to Color.White
                }
                Surface(
                    shape = RoundedCornerShape(2.dp),
                    color = typeColor.copy(alpha = 0.15f),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Text(
                        text = typeLabel,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = NothingFont,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = typeColor
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = room.name.uppercase(),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = NothingFont,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onShare,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF27272A)),
                    shape = RoundedCornerShape(2.dp)
                ) {
                    Text("SHARE ROOM", style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont))
                }

                Button(
                    onClick = onLeave,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0031)),
                    shape = RoundedCornerShape(2.dp)
                ) {
                    Text("LEAVE ROOM", style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont))
                }
            }
        }
    }
}

@Composable
private fun MemberCard(member: RoomMember) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.03f), RoundedCornerShape(2.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(2.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(member.username.take(1).uppercase(), color = Color.White, style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(member.username.uppercase(), style = MaterialTheme.typography.labelMedium.copy(fontFamily = NothingFont, fontWeight = FontWeight.Bold, color = Color.White))
            Text(member.role.name, style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont, fontSize = 8.sp, color = Color.White.copy(alpha = 0.4f)))
        }
        if (member.isConnected) {
            Box(Modifier.size(8.dp).background(Color(0xFF00E676), RoundedCornerShape(4.dp)))
        }
    }
}

@Composable
private fun CreateJoinSection(onCreate: (String, RoomType) -> Unit, onJoin: (String) -> Unit) {
    var nameInput by remember { mutableStateOf("") }
    var codeInput by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(RoomType.FRIENDS) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "SELECT ROOM TYPE",
            style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont, color = Color.White.copy(alpha = 0.6f))
        )

        // Room Type Selector Cards (Couple, Friends, Team)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Couple Room
            val isCouple = selectedType == RoomType.COUPLE
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { selectedType = RoomType.COUPLE }
                    .border(
                        1.dp,
                        if (isCouple) Color(0xFFFF4081) else Color.White.copy(alpha = 0.1f),
                        RoundedCornerShape(2.dp)
                    ),
                shape = RoundedCornerShape(2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCouple) Color(0xFFFF4081).copy(alpha = 0.15f) else Color.White.copy(alpha = 0.03f)
                )
            ) {
                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("💑", fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("COUPLE", style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont, fontWeight = FontWeight.Bold, color = if (isCouple) Color(0xFFFF4081) else Color.White))
                    Text("2 Members", style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp, color = Color.White.copy(alpha = 0.5f)))
                }
            }

            // Friends Room
            val isFriends = selectedType == RoomType.FRIENDS
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { selectedType = RoomType.FRIENDS }
                    .border(
                        1.dp,
                        if (isFriends) Color(0xFF00E676) else Color.White.copy(alpha = 0.1f),
                        RoundedCornerShape(2.dp)
                    ),
                shape = RoundedCornerShape(2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isFriends) Color(0xFF00E676).copy(alpha = 0.15f) else Color.White.copy(alpha = 0.03f)
                )
            ) {
                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🎉", fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("FRIENDS", style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont, fontWeight = FontWeight.Bold, color = if (isFriends) Color(0xFF00E676) else Color.White))
                    Text("10 Members", style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp, color = Color.White.copy(alpha = 0.5f)))
                }
            }

            // Team Room
            val isTeam = selectedType == RoomType.TEAM
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { selectedType = RoomType.TEAM }
                    .border(
                        1.dp,
                        if (isTeam) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.1f),
                        RoundedCornerShape(2.dp)
                    ),
                shape = RoundedCornerShape(2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isTeam) Color(0xFF00E5FF).copy(alpha = 0.15f) else Color.White.copy(alpha = 0.03f)
                )
            ) {
                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🛡️", fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("TEAM", style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont, fontWeight = FontWeight.Bold, color = if (isTeam) Color(0xFF00E5FF) else Color.White))
                    Text("50 Members", style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp, color = Color.White.copy(alpha = 0.5f)))
                }
            }
        }

        OutlinedTextField(
            value = nameInput,
            onValueChange = { nameInput = it },
            label = { Text("ROOM_NAME") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color.White.copy(alpha = 0.2f), focusedBorderColor = Color(0xFFFF0031))
        )
        Button(
            onClick = { onCreate(nameInput, selectedType) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(2.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0031))
        ) {
            Text("CREATE_NEW_ROOM", style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont))
        }

        HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

        OutlinedTextField(
            value = codeInput,
            onValueChange = { codeInput = it },
            label = { Text("ENTER_ROOM_CODE") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color.White.copy(alpha = 0.2f), focusedBorderColor = Color(0xFF69D6E2))
        )
        Button(
            onClick = { onJoin(codeInput) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF18181B)),
            shape = RoundedCornerShape(2.dp)
        ) {
            Text("JOIN_EXISTING_ROOM", style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont))
        }
    }
}
