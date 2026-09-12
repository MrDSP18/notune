
package echo.music.iad1tya.notune

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import echo.music.iad1tya.LocalPlayerConnection
import echo.music.iad1tya.R
import echo.music.iad1tya.playback.queues.ListQueue
import echo.music.iad1tya.ui.component.IconButton
import echo.music.iad1tya.ui.component.SongListItem
import echo.music.iad1tya.ui.theme.NothingFont
import echo.music.iad1tya.extensions.toMediaItem
import androidx.compose.ui.res.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReplayScreen(
    navController: NavController,
    viewModel: ReplayViewModel = hiltViewModel()
) {
    val topSongs by viewModel.topSongs.collectAsState()
    val playerConnection = LocalPlayerConnection.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("NØTUNE REPLAY", style = MaterialTheme.typography.titleLarge.copy(fontFamily = NothingFont, letterSpacing = 2.sp)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(painter = painterResource(id = R.drawable.arrow_back), contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        },
        containerColor = Color.Black
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text(
                    text = "YOUR SOUNDTRACK THIS MONTH",
                    style = MaterialTheme.typography.labelMedium.copy(fontFamily = NothingFont, color = Color(0xFFFF0031)),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            
            items(topSongs) { song ->
                SongListItem(
                    song = song,
                    onClick = {
                        playerConnection?.playQueue(ListQueue("Replay", topSongs.map { it.toMediaItem() }, topSongs.indexOf(song)))
                    }
                )
            }
        }
    }
}
