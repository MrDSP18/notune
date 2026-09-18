package echo.music.iad1tya.ui.screens.settings.integrations

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import echo.music.iad1tya.LocalPlayerAwareWindowInsets
import echo.music.iad1tya.R
import echo.music.iad1tya.constants.*
import echo.music.iad1tya.listentogether.*
import echo.music.iad1tya.ui.component.DefaultDialog
import echo.music.iad1tya.ui.component.IconButton
import echo.music.iad1tya.ui.component.IntegrationCard
import echo.music.iad1tya.ui.component.IntegrationCardItem
import echo.music.iad1tya.ui.utils.backToMain
import echo.music.iad1tya.utils.rememberPreference
import echo.music.iad1tya.viewmodels.ListenTogetherViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListenTogetherSettings(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: ListenTogetherViewModel = hiltViewModel(),
    highlightKey: String? = null
) {
    val context = LocalContext.current
    
    val connectionState by viewModel.connectionState.collectAsState()
    val room by viewModel.currentRoom.collectAsState()
    val logs by viewModel.logs.collectAsState()
    val blockedUsernames by viewModel.blockedUsernames.collectAsState()
    
    var serverUrl by rememberPreference(ListenTogetherServerUrlKey, ListenTogetherServers.defaultServerUrl)
    var username by rememberPreference(ListenTogetherUsernameKey, "")
    
    var showLogsDialog by rememberSaveable { mutableStateOf(false) }
    var showBlockedUsersDialog by rememberSaveable { mutableStateOf(false) }
    
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            IntegrationCard(
                title = "ROOM_SETTINGS",
                items = listOf(
                    IntegrationCardItem(
                        icon = painterResource(R.drawable.person),
                        title = { Text("BLOCKED_NODES") },
                        description = { Text("${blockedUsernames.size} NODES") },
                        onClick = { showBlockedUsersDialog = true }
                    ),
                    IntegrationCardItem(
                        icon = painterResource(R.drawable.bug_report),
                        title = { Text("VIEW_TELEMETRY_LOGS") },
                        description = { Text("TRACE_ROOM_SOCKET_MESSAGES") },
                        onClick = { showLogsDialog = true }
                    )
                )
            )
        }
    }

    if (showLogsDialog) {
        LogsDialog(
            logs = logs,
            onClear = { viewModel.clearLogs() },
            onDismiss = { showLogsDialog = false }
        )
    }

    if (showBlockedUsersDialog) {
        BlockedUsersDialog(
            blockedUsernames = blockedUsernames,
            onUnblock = { viewModel.unblockUser(it) },
            onDismiss = { showBlockedUsersDialog = false }
        )
    }
}

@Composable
fun LogsDialog(
    logs: List<LogEntry>,
    onClear: () -> Unit,
    onDismiss: () -> Unit
) {
    DefaultDialog(
        onDismiss = onDismiss,
        title = { Text("TELEMETRY_LOG") },
        buttons = {
            TextButton(onClick = onClear) { Text("CLEAR") }
            Button(onClick = onDismiss) { Text("OK") }
        }
    ) {
        LazyColumn(modifier = Modifier.height(300.dp)) {
            items(logs) { log ->
                Text(
                    text = "[${log.timestamp}] ${log.message}",
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, color = Color.White.copy(alpha = 0.7f)),
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun BlockedUsersDialog(
    blockedUsernames: Set<String>,
    onUnblock: (String) -> Unit,
    onDismiss: () -> Unit
) {
    DefaultDialog(
        onDismiss = onDismiss,
        title = { Text("BLOCKED_NODES") },
        buttons = {
            Button(onClick = onDismiss) { Text("OK") }
        }
    ) {
        LazyColumn(modifier = Modifier.height(200.dp)) {
            items(blockedUsernames.toList()) { username ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Text(username, modifier = Modifier.weight(1f))
                    TextButton(onClick = { onUnblock(username) }) { Text("UNBLOCK") }
                }
            }
        }
    }
}
