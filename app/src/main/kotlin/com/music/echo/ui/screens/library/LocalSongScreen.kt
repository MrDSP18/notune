package echo.music.iad1tya.ui.screens.library

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import echo.music.iad1tya.LocalPlayerConnection
import echo.music.iad1tya.R
import echo.music.iad1tya.extensions.toMediaItem
import echo.music.iad1tya.playback.queues.ListQueue
import echo.music.iad1tya.ui.component.LocalMenuState
import echo.music.iad1tya.ui.component.SongListItem
import echo.music.iad1tya.ui.menu.SongMenu
import echo.music.iad1tya.viewmodels.LocalSongsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalSongScreen(
    navController: NavController,
    onBack: () -> Unit = { navController.navigateUp() },
    viewModel: LocalSongsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val menuState = LocalMenuState.current
    val playerConnection = LocalPlayerConnection.current ?: return
    val isPlaying by playerConnection.isPlaying.collectAsState()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()

    val songs by viewModel.songs.collectAsState()
    val folders by viewModel.folders.collectAsState()
    val recentlyAdded by viewModel.recentlyAdded.collectAsState()
    val mostPlayed by viewModel.mostPlayed.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val recommendations by viewModel.recommendations.collectAsState()
    val duplicates by viewModel.duplicates.collectAsState()
    val scanState by viewModel.scanState.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Songs", "Folders", "Smart Mix", "Duplicates")
    val listState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val safFolderLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        if (uri != null) {
            context.contentResolver.takePersistableUriPermission(
                uri,
                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            viewModel.scanSafFolder(uri)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.refreshLocalInsights()
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = Color.Black,
        topBar = {
            Column {
                LargeTopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.local_history),
                            fontWeight = FontWeight.Bold,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(painterResource(R.drawable.arrow_back), contentDescription = null, tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = { safFolderLauncher.launch(null) }) {
                            Icon(painterResource(R.drawable.folder_managed), contentDescription = "Add SAF Folder", tint = Color.White)
                        }
                        IconButton(onClick = { viewModel.scanDevice() }) {
                            Icon(painterResource(R.drawable.sync), contentDescription = "Sync", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black, titleContentColor = Color.White),
                    scrollBehavior = scrollBehavior
                )

                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.Black,
                    contentColor = Color.Red
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    text = title,
                                    color = if (selectedTabIndex == index) Color.Red else Color.Gray,
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
            }
        },
    ) { paddingValues ->
        if (scanState.isScanning) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color.Red)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Scanning local music...", color = Color.White)
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when (selectedTabIndex) {
                    0 -> { // Songs
                        itemsIndexed(items = songs, key = { _, item -> item.id }) { index, song ->
                            SongListItem(
                                song = song,
                                isActive = song.id == mediaMetadata?.id,
                                isPlaying = isPlaying,
                                trailingContent = {
                                    IconButton(onClick = {
                                        menuState.show {
                                            SongMenu(
                                                originalSong = song,
                                                navController = navController,
                                                onDismiss = menuState::dismiss
                                            )
                                        }
                                    }) {
                                        Icon(painterResource(R.drawable.more_vert), contentDescription = null, tint = Color.White)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .clickable {
                                        playerConnection.playQueue(
                                            ListQueue(
                                                title = context.getString(R.string.local_history),
                                                items = songs.map { it.toMediaItem() },
                                                startIndex = index
                                            )
                                        )
                                    }
                            )
                        }
                    }
                    1 -> { // Folders
                        items(folders, key = { it.path }) { folder ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 4.dp)
                                    .clickable {
                                        if (folder.directSongs.isNotEmpty()) {
                                            playerConnection.playQueue(
                                                ListQueue(
                                                    title = folder.name,
                                                    items = folder.directSongs.map { it.toMediaItem() }
                                                )
                                            )
                                        }
                                    },
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF181818))
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.folder_managed),
                                        contentDescription = null,
                                        tint = Color.Red,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = folder.name, color = Color.White, fontWeight = FontWeight.Bold)
                                        Text(text = "${folder.songCount} songs", color = Color.Gray, fontSize = 12.sp)
                                    }
                                    IconButton(onClick = {
                                        if (folder.directSongs.isNotEmpty()) {
                                            playerConnection.playQueue(
                                                ListQueue(
                                                    title = folder.name,
                                                    items = folder.directSongs.shuffled().map { it.toMediaItem() }
                                                )
                                            )
                                        }
                                    }) {
                                        Icon(painterResource(R.drawable.shuffle), contentDescription = "Shuffle Folder", tint = Color.White)
                                    }
                                }
                            }
                        }
                    }
                    2 -> { // Smart Mix
                        item {
                            Text("Local Recommendations", color = Color.Red, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
                        }
                        items(recommendations) { song ->
                            SongListItem(
                                song = song,
                                isActive = song.id == mediaMetadata?.id,
                                isPlaying = isPlaying,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .clickable {
                                        playerConnection.playQueue(
                                            ListQueue(
                                                title = "Recommendations",
                                                items = recommendations.map { it.toMediaItem() }
                                            )
                                        )
                                    }
                            )
                        }

                        item {
                            Text("Recently Added (${recentlyAdded.size})", color = Color.Red, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
                        }
                        items(recentlyAdded.take(15)) { song ->
                            SongListItem(
                                song = song,
                                isActive = song.id == mediaMetadata?.id,
                                isPlaying = isPlaying,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .clickable {
                                        playerConnection.playQueue(
                                            ListQueue(
                                                title = "Recently Added",
                                                items = recentlyAdded.map { it.toMediaItem() }
                                            )
                                        )
                                    }
                            )
                        }

                        item {
                            Text("Most Played (${mostPlayed.size})", color = Color.Red, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
                        }
                        items(mostPlayed.take(15)) { song ->
                            SongListItem(
                                song = song,
                                isActive = song.id == mediaMetadata?.id,
                                isPlaying = isPlaying,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .clickable {
                                        playerConnection.playQueue(
                                            ListQueue(
                                                title = "Most Played",
                                                items = mostPlayed.map { it.toMediaItem() }
                                            )
                                        )
                                    }
                            )
                        }
                    }
                    3 -> { // Duplicates
                        if (duplicates.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                    Text("No duplicate songs found!", color = Color.Gray)
                                }
                            }
                        } else {
                            items(duplicates) { group ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 4.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(group.titleKey, color = Color.White, fontWeight = FontWeight.Bold)
                                        Text("${group.primaryArtist} • ${group.duplicates.size} copies", color = Color.Gray, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
