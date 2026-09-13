package echo.music.iad1tya.notune.rooms.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import echo.music.iad1tya.notune.rooms.RoomsViewModel
import echo.music.iad1tya.notune.rooms.models.RoomModel
import echo.music.iad1tya.notune.rooms.models.RoomType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicDiscoveryScreen(
    navController: NavController,
    viewModel: RoomsViewModel = hiltViewModel(),
    onRoomSelected: (RoomModel) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var selectedCategory by remember { mutableStateOf("All") }
    var showCreateModal by remember { mutableStateOf(false) }

    val categories = listOf("All", "Tamil Hits", "Late Night Vibes", "Chill Zone", "Gaming Music", "Rock Corner")

    val filteredRooms = remember(uiState.publicRooms, searchQuery, selectedCategory) {
        uiState.publicRooms.filter { room ->
            val matchesQuery = searchQuery.isBlank() || room.name.contains(searchQuery, ignoreCase = true) || (room.genreTag?.contains(searchQuery, ignoreCase = true) == true)
            val matchesCategory = selectedCategory == "All" || room.genreTag.equals(selectedCategory, ignoreCase = true)
            matchesQuery && matchesCategory
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("NØTUNE Rooms", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("📻", fontSize = 20.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreateModal = true },
                icon = { Text("＋") },
                text = { Text("Create Room") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Search public rooms by name or genre...") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Live Public Rooms 🔥",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredRooms.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🎵", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No public rooms found.\nBe the first to create one!",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredRooms) { room ->
                        PublicRoomCard(room = room, onJoin = { onRoomSelected(room) })
                    }
                }
            }
        }
    }

    if (showCreateModal) {
        CreateRoomDialog(
            onDismiss = { showCreateModal = false },
            onCreate = { name, desc, type, genre ->
                viewModel.createRoom(name, desc, type, genre)
                showCreateModal = false
            }
        )
    }
}

@Composable
private fun PublicRoomCard(
    room: RoomModel,
    onJoin: () -> Unit
) {
    val cardGradient = Brush.horizontalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.surface
        )
    )

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardGradient)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = room.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (room.isEncrypted) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("🔐", fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = room.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "👥 ${room.currentMemberCount}/${room.memberLimit}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                    if (room.genreTag != null) {
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "🏷️ ${room.genreTag}",
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Button(
                onClick = onJoin,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Join")
            }
        }
    }
}

@Composable
private fun CreateRoomDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String, RoomType, String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(RoomType.PUBLIC) }
    var genreTag by remember { mutableStateOf("Tamil Hits") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create NØTUNE Room") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Room Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description / Topic") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Room Type", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    RoomType.values().forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = { Text(type.name, fontSize = 11.sp) }
                        )
                    }
                }
                OutlinedTextField(
                    value = genreTag,
                    onValueChange = { genreTag = it },
                    label = { Text("Genre / Theme Tag") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onCreate(name, description, selectedType, genreTag.ifBlank { null })
                    }
                }
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
