package echo.music.iad1tya.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import echo.music.iad1tya.LocalPlayerAwareWindowInsets
import echo.music.iad1tya.R
import echo.music.iad1tya.constants.AppBarHeight
import echo.music.iad1tya.models.*
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
                subtitle = "Active Room Matrix"
            )
        }

        if (room != null) {
            item {
                RoomInfoCard(room = room!!, onLeave = { viewModel.leaveRoom() })
            }
            
            item {
                Text(
                    text = "CONNECTED_MEMBERS",
                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFFF0031))
                )
            }

            items(room!!.members) { member ->
                MemberCard(member = member)
            }
        } else {
            item {
                CreateJoinSection(
                    onCreate = { viewModel.createRoom(it) },
                    onJoin = { viewModel.joinRoom(it) }
                )
            }
        }
    }
}

@Composable
private fun RoomInfoCard(room: Room, onLeave: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "ROOM_ID: ${room.roomId}",
                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF69D6E2))
            )
            Text(
                text = room.name.uppercase(),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onLeave,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0031)),
                shape = RoundedCornerShape(2.dp)
            ) {
                Text("LEAVE_ROOM")
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
            Text(member.username.take(1).uppercase(), color = Color.White)
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(member.username.uppercase(), style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Color.White))
            Text(member.role.name, style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, color = Color.White.copy(alpha = 0.4f)))
        }
        if (member.isConnected) {
            Box(Modifier.size(8.dp).background(Color(0xFF00E676), RoundedCornerShape(4.dp)))
        }
    }
}

@Composable
private fun CreateJoinSection(onCreate: (String) -> Unit, onJoin: (String) -> Unit) {
    var nameInput by remember { mutableStateOf("") }
    var codeInput by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = nameInput,
            onValueChange = { nameInput = it },
            label = { Text("ROOM_NAME") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color.White.copy(alpha = 0.2f), focusedBorderColor = Color(0xFFFF0031))
        )
        Button(
            onClick = { onCreate(nameInput) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(2.dp)
        ) {
            Text("CREATE_NEW_ROOM")
        }

        HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

        OutlinedTextField(
            value = codeInput,
            onValueChange = { codeInput = it },
            label = { Text("ROOM_CODE") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color.White.copy(alpha = 0.2f), focusedBorderColor = Color(0xFF69D6E2))
        )
        Button(
            onClick = { onJoin(codeInput) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF18181B)),
            shape = RoundedCornerShape(2.dp)
        ) {
            Text("JOIN_EXISTING_ROOM")
        }
    }
}
