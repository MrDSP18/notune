package echo.music.iad1tya.ui.screens.couple

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import echo.music.iad1tya.R
import echo.music.iad1tya.db.entities.CoupleSessionEntity
import echo.music.iad1tya.db.entities.MemoryCapsuleEntity
import echo.music.iad1tya.db.entities.OurSongEntity
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoupleModeScreen(
    navController: NavController,
    viewModel: CoupleViewModel = hiltViewModel()
) {
    val session by viewModel.activeSession.collectAsState()
    val isVaultUnlocked by viewModel.isVaultUnlocked.collectAsState()
    
    var selectedTab by remember { mutableStateOf(0) }
    var showPairDialog by remember { mutableStateOf(false) }
    var showCapsuleDialog by remember { mutableStateOf(false) }
    var showOurSongDialog by remember { mutableStateOf(false) }
    var showVaultPinDialog by remember { mutableStateOf(false) }

    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFE91E63), // Romantic Pink
            Color(0xFF9C27B0), // Soft Purple
            Color(0xFF673AB7)  // Deep Violet
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "NØTUNE Couple Mode",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "❤️", fontSize = 20.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Header Hero Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(gradientBrush)
                        .padding(20.dp)
                ) {
                    Column(
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        if (session != null) {
                            Surface(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "Connected Session",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Connected with ${session?.partnerName}",
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Pair Code: ${session?.pairCode}",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 14.sp
                            )
                        } else {
                            Text(
                                text = "Sync Your Music Journey",
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Listen together, unlock shared memories & mood sync",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { showPairDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = Color(0xFFE91E63)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                            ) {
                                Text("Connect Partner", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Navigation Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Overview") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Memories") }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Mood Sync") }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("Vault 🔒") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tab Content
            Crossfade(targetState = selectedTab, label = "CoupleTabTransition") { tabIndex ->
                when (tabIndex) {
                    0 -> OverviewTab(
                        session = session,
                        onConnectClick = { showPairDialog = true },
                        onSetOurSongClick = { showOurSongDialog = true },
                        onAddCapsuleClick = { showCapsuleDialog = true }
                    )
                    1 -> MemoriesTab(
                        session = session,
                        onAddCapsuleClick = { showCapsuleDialog = true }
                    )
                    2 -> MoodSyncTab(session = session)
                    3 -> VaultTab(
                        isUnlocked = isVaultUnlocked,
                        onUnlockClick = { showVaultPinDialog = true },
                        onLockClick = { viewModel.lockVault() }
                    )
                }
            }
        }
    }

    // Dialogs
    if (showPairDialog) {
        PairDeviceDialog(
            onDismiss = { showPairDialog = false },
            onPair = { partnerName, code ->
                viewModel.createSession(partnerName, code)
                showPairDialog = false
            }
        )
    }

    if (showOurSongDialog && session != null) {
        SetOurSongDialog(
            onDismiss = { showOurSongDialog = false },
            onSave = { title, artist, note ->
                viewModel.setOurSong(session!!.id, title, artist, "custom_song_id", note)
                showOurSongDialog = false
            }
        )
    }

    if (showCapsuleDialog && session != null) {
        CreateCapsuleDialog(
            onDismiss = { showCapsuleDialog = false },
            onSave = { title, note ->
                viewModel.addMemoryCapsule(session!!.id, title, note, null, null)
                showCapsuleDialog = false
            }
        )
    }

    if (showVaultPinDialog) {
        VaultPinDialog(
            onDismiss = { showVaultPinDialog = false },
            onUnlock = { pin ->
                val success = viewModel.unlockVault(pin)
                if (success) showVaultPinDialog = false
                success
            }
        )
    }
}

@Composable
private fun OverviewTab(
    session: CoupleSessionEntity?,
    onConnectClick: () -> Unit,
    onSetOurSongClick: () -> Unit,
    onAddCapsuleClick: () -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            // Our Special Song Card
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🎵 Our Anthem / Our Song",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = onSetOurSongClick) {
                            Text("✏️")
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No song selected yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            // Feature Quick Grid
            Text(
                text = "Shared Features",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FeatureTile(
                    title = "AI Couple Mix",
                    subtitle = "Blended Playlist",
                    icon = "✨",
                    modifier = Modifier.weight(1f),
                    onClick = {}
                )
                FeatureTile(
                    title = "Memory Capsule",
                    subtitle = "Lock a Moment",
                    icon = "💌",
                    modifier = Modifier.weight(1f),
                    onClick = onAddCapsuleClick
                )
            }
        }
    }
}

@Composable
private fun MemoriesTab(
    session: CoupleSessionEntity?,
    onAddCapsuleClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Shared Memory Capsules 💌",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = onAddCapsuleClick,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("+ New Capsule")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No memory capsules yet.\nCreate one to lock a song & note for an anniversary!",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun MoodSyncTab(session: CoupleSessionEntity?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "💞", fontSize = 48.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Real-Time Mood Sync",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "When both partners are listening, NØTUNE analyzes beats & genres to calculate harmonic resonance.",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun VaultTab(
    isUnlocked: Boolean,
    onUnlockClick: () -> Unit,
    onLockClick: () -> Unit
) {
    if (!isUnlocked) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "🔒", fontSize = 54.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Private Couple Vault",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Protected by PIN / Biometrics. Store hidden playlists, audio letters, and private memories.",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onUnlockClick,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Unlock Vault")
            }
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Vault Unlocked 🔓",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
                OutlinedButton(onClick = onLockClick) {
                    Text("Lock Vault")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Your private couple items are securely decrypted and ready.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
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
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = icon, fontSize = 24.sp)
            Column {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PairDeviceDialog(
    onDismiss: () -> Unit,
    onPair: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Connect Partner") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Partner Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Pair Code (e.g. LOVE2026)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank() && code.isNotBlank()) onPair(name, code) }
            ) {
                Text("Connect")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun SetOurSongDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var artist by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Our Song") },
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
                    label = { Text("Artist") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Special Note (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (title.isNotBlank() && artist.isNotBlank()) onSave(title, artist, note.ifBlank { null }) }
            ) {
                Text("Save Anthem")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun CreateCapsuleDialog(
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Memory Capsule") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Capsule Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Memory Note / Message") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (title.isNotBlank()) onSave(title, note) }
            ) {
                Text("Lock Capsule")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun VaultPinDialog(
    onDismiss: () -> Unit,
    onUnlock: (String) -> Boolean
) {
    var pin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Enter Vault PIN") },
        text = {
            Column {
                OutlinedTextField(
                    value = pin,
                    onValueChange = {
                        pin = it
                        error = false
                    },
                    label = { Text("PIN (Demo: 1234)") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    isError = error,
                    modifier = Modifier.fillMaxWidth()
                )
                if (error) {
                    Text(
                        text = "Incorrect PIN. Try 1234.",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (!onUnlock(pin)) {
                        error = true
                    }
                }
            ) {
                Text("Unlock")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
