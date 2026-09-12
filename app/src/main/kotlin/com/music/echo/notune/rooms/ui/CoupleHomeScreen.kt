package echo.music.iad1tya.notune.rooms.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import echo.music.iad1tya.notune.rooms.couple.CallState
import echo.music.iad1tya.notune.rooms.couple.CallType
import echo.music.iad1tya.notune.rooms.couple.CoupleCallController
import echo.music.iad1tya.ui.screens.couple.CoupleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoupleHomeScreen(
    navController: NavController,
    viewModel: CoupleViewModel = hiltViewModel(),
    callController: CoupleCallController = hiltViewModel<CoupleViewModel>().let { CoupleCallController() }
) {
    val activeSession by viewModel.activeSession.collectAsState()
    val callSession by callController.currentCall.collectAsState()
    var activeFeeling by remember { mutableStateOf("Missing you 🥰") }

    val feelings = listOf(
        "❤️ Love", "🥰 Missing you", "😊 Happy", "🥹 Emotional",
        "🔥 Excited", "🫂 Need you", "💭 Thinking of you", "😘 Kiss"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Couple Home", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("🔐 E2E Encrypted", fontSize = 11.sp, color = Color(0xFF4CAF50))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                // Hero Status Card
                val romanticGradient = Brush.linearGradient(
                    colors = listOf(Color(0xFFE91E63), Color(0xFF8E24AA), Color(0xFF4A148C))
                )
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(romanticGradient)
                            .padding(20.dp)
                    ) {
                        Column(modifier = Modifier.align(Alignment.TopStart)) {
                            Surface(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "Status: Listening Together 💞",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${activeSession?.partnerName ?: "Partner"} + You",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Distance Mode Active • 14 songs shared tonight",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 13.sp
                            )
                        }

                        // Quick Call Actions
                        Row(
                            modifier = Modifier.align(Alignment.BottomEnd),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { callController.startCall(activeSession?.partnerName ?: "Partner", CallType.AUDIO) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFFE91E63)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("📞 Audio")
                            }
                            Button(
                                onClick = { callController.startCall(activeSession?.partnerName ?: "Partner", CallType.VIDEO) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF8E24AA)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("📹 Video")
                            }
                        }
                    }
                }
            }

            // Quick Feelings Bar
            item {
                Column {
                    Text("Express Feeling 💭", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(feelings) { feeling ->
                            FilterChip(
                                selected = activeFeeling == feeling,
                                onClick = { activeFeeling = feeling },
                                label = { Text(feeling) }
                            )
                        }
                    }
                }
            }

            // Couple Features Grid
            item {
                Text("Couple Ecosystem", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FeatureTile(
                        title = "Secret Dedication",
                        subtitle = "Send private song message",
                        icon = "💌",
                        modifier = Modifier.weight(1f),
                        onClick = {}
                    )
                    FeatureTile(
                        title = "Couple DNA",
                        subtitle = "92% Romance Match",
                        icon = "🧬",
                        modifier = Modifier.weight(1f),
                        onClick = {}
                    )
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FeatureTile(
                        title = "Guess My Song",
                        subtitle = "Interactive game",
                        icon = "🎯",
                        modifier = Modifier.weight(1f),
                        onClick = {}
                    )
                    FeatureTile(
                        title = "Memory Capsule",
                        subtitle = "Locked until 2027",
                        icon = "🔒",
                        modifier = Modifier.weight(1f),
                        onClick = {}
                    )
                }
            }
        }
    }

    // Active Call Dialog Overlay
    if (callSession != null) {
        AlertDialog(
            onDismissRequest = { callController.endCall() },
            title = { Text(if (callSession!!.callType == CallType.VIDEO) "📹 Video Call" else "📞 Audio Call") },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("Calling ${callSession!!.partnerName}...", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Status: ${callSession!!.callState.name}", color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        IconButton(onClick = { callController.toggleMic() }) {
                            Text(if (callSession!!.isMicMuted) "🎙️ Off" else "🎙️ On")
                        }
                        IconButton(onClick = { callController.toggleCamera() }) {
                            Text(if (callSession!!.isCameraOn) "📷 On" else "📷 Off")
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { callController.endCall() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("End Call")
                }
            }
        )
    }
}

@Composable
private fun FeatureTile(
    title: String,
    subtitle: String,
    icon: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(icon, fontSize = 22.sp)
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
