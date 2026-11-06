package echo.music.iad1tya.ui.screens.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
    val listState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = Color.Black,
        topBar = {
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
                    IconButton(onClick = { viewModel.scanDevice() }) {
                        Icon(painterResource(R.drawable.sync), contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black, titleContentColor = Color.White),
                scrollBehavior = scrollBehavior
            )
        },
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
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
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).clickable {
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
    }
}
