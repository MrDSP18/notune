
package echo.music.iad1tya.notune

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.music.echo.notune.memory.ForgottenSongCandidate
import com.music.echo.notune.memory.MemoryInsight
import com.music.echo.notune.memory.TimeMachineEra
import echo.music.iad1tya.LocalPlayerConnection
import echo.music.iad1tya.playback.PlayerConnection
import echo.music.iad1tya.R
import echo.music.iad1tya.db.entities.Song
import echo.music.iad1tya.extensions.toMediaItem
import echo.music.iad1tya.playback.queues.ListQueue
import echo.music.iad1tya.ui.component.IconButton
import echo.music.iad1tya.ui.component.SongListItem
import echo.music.iad1tya.ui.theme.NothingFont
import kotlinx.coroutines.launch

private val ReplayTabs = listOf("Top Songs", "Forgotten Gems", "Time Machine", "Memories")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReplayScreen(
    navController: NavController,
    viewModel: ReplayViewModel = hiltViewModel()
) {
    val topSongs by viewModel.topSongs.collectAsState()
    val period by viewModel.period.collectAsState()
    val forgottenSongs by viewModel.forgottenSongs.collectAsState()
    val musicEras by viewModel.musicEras.collectAsState()
    val memoryInsights by viewModel.memoryInsights.collectAsState()
    val playerConnection = LocalPlayerConnection.current

    val pagerState = rememberPagerState(pageCount = { ReplayTabs.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "NØTUNE REPLAY",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = NothingFont,
                            letterSpacing = 2.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.arrow_back),
                            contentDescription = null
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color.Black
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Tab Row
            ScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = Color.Black,
                contentColor = Color.White,
                edgePadding = 16.dp,
                divider = {}
            ) {
                ReplayTabs.forEachIndexed { index, title ->
                    val selected = pagerState.currentPage == index
                    val color by animateColorAsState(
                        targetValue = if (selected) Color(0xFFFF0031) else Color.White.copy(alpha = 0.5f),
                        animationSpec = tween(250),
                        label = "tab_color_$index"
                    )
                    Tab(
                        selected = selected,
                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                        text = {
                            Text(
                                title,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = NothingFont,
                                    color = color,
                                    fontWeight = if (selected) FontWeight.Black else FontWeight.Normal
                                )
                            )
                        }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) { page ->
                when (page) {
                    0 -> TopSongsTab(topSongs, period, onPeriodChange = viewModel::setPeriod, playerConnection)
                    1 -> ForgottenGemsTab(forgottenSongs, playerConnection)
                    2 -> TimeMachineTab(musicEras, playerConnection)
                    3 -> MemoriesTab(memoryInsights)
                }
            }
        }
    }
}

// ─── Tab 0: Top Songs ────────────────────────────────────────────────────────

@Composable
private fun TopSongsTab(
    topSongs: List<Song>,
    period: ReplayPeriod,
    onPeriodChange: (ReplayPeriod) -> Unit,
    playerConnection: echo.music.iad1tya.playback.PlayerConnection?
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Text(
                text = "YOUR SOUNDTRACK — ${period.label.uppercase()}",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontFamily = NothingFont,
                    color = Color(0xFFFF0031)
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        // Period chips
        item {
            androidx.compose.foundation.lazy.LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                items(ReplayPeriod.values().toList()) { p ->
                    FilterChip(
                        selected = period == p,
                        onClick = { onPeriodChange(p) },
                        label = { Text(p.label, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFFF0031),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }
        if (topSongs.isEmpty()) {
            item {
                EmptyStateCard("🎵", "No play history yet", "Start listening to build your Replay.")
            }
        } else {
            items(topSongs) { song ->
                SongListItem(
                    song = song,
                    modifier = Modifier.clickable {
                        playerConnection?.playQueue(
                            ListQueue("Replay", topSongs.map { it.toMediaItem() }, topSongs.indexOf(song))
                        )
                    }
                )
            }
        }
    }
}

// ─── Tab 1: Forgotten Gems ───────────────────────────────────────────────────

@Composable
private fun ForgottenGemsTab(
    forgottenSongs: List<ForgottenSongCandidate>,
    playerConnection: echo.music.iad1tya.playback.PlayerConnection?
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Text(
                text = "SONGS YOU'VE FORGOTTEN",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontFamily = NothingFont,
                    color = Color(0xFFFF0031)
                ),
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Loved once, gone quiet for 90+ days.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        if (forgottenSongs.isEmpty()) {
            item {
                EmptyStateCard("💿", "Nothing forgotten yet", "Songs you've listened to often but haven't heard in a while will appear here.")
            }
        } else {
            val songList = forgottenSongs.map { it.song }
            items(forgottenSongs) { candidate ->
                Column {
                    SongListItem(
                        song = candidate.song,
                        modifier = Modifier.clickable {
                            playerConnection?.playQueue(
                                ListQueue(
                                    "Forgotten Gems",
                                    songList.map { it.toMediaItem() },
                                    songList.indexOf(candidate.song)
                                )
                            )
                        }
                    )
                    Text(
                        text = "⏳ ${candidate.daysUnplayed} days ago • played ${candidate.totalHistoricalPlays}× historically",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.45f),
                        modifier = Modifier.padding(start = 56.dp, bottom = 4.dp)
                    )
                }
            }
        }
    }
}

// ─── Tab 2: Time Machine ─────────────────────────────────────────────────────

@Composable
private fun TimeMachineTab(
    musicEras: List<TimeMachineEra>,
    playerConnection: echo.music.iad1tya.playback.PlayerConnection?
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Text(
                text = "YOUR MUSIC ERAS",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontFamily = NothingFont,
                    color = Color(0xFFFF0031)
                ),
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Songs that defined each year of your listening history.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        if (musicEras.isEmpty()) {
            item {
                EmptyStateCard("⏳", "No eras yet", "Your listening history by year will appear here once you've built up some plays.")
            }
        } else {
            items(musicEras) { era ->
                EraCard(era = era, playerConnection = playerConnection)
            }
        }
    }
}

@Composable
private fun EraCard(
    era: TimeMachineEra,
    playerConnection: echo.music.iad1tya.playback.PlayerConnection?
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .clickable { expanded = !expanded }
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "🕰️ ${era.eraLabel}",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = NothingFont,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                ),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${era.songs.size} songs",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.5f)
            )
            Text(
                text = if (expanded) " ▲" else " ▼",
                color = Color(0xFFFF0031),
                fontWeight = FontWeight.Bold
            )
        }
        if (expanded) {
            Spacer(modifier = Modifier.height(8.dp))
            era.songs.forEach { song ->
                SongListItem(
                    song = song,
                    modifier = Modifier.clickable {
                        playerConnection?.playQueue(
                            ListQueue(
                                era.eraLabel,
                                era.songs.map { it.toMediaItem() },
                                era.songs.indexOf(song)
                            )
                        )
                    }
                )
            }
        }
    }
}

// ─── Tab 3: Memories ─────────────────────────────────────────────────────────

@Composable
private fun MemoriesTab(insights: List<MemoryInsight>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Text(
                text = "CONTEXTUAL MEMORIES",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontFamily = NothingFont,
                    color = Color(0xFFFF0031)
                ),
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "NØTUNE noticed these patterns in your listening.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        if (insights.isEmpty()) {
            item {
                EmptyStateCard("🧠", "No memories yet", "Listen more and NØTUNE will surface personal insights about your habits.")
            }
        } else {
            items(insights) { insight ->
                MemoryInsightCard(insight)
            }
        }
    }
}

@Composable
private fun MemoryInsightCard(insight: MemoryInsight) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .padding(16.dp)
    ) {
        Text(text = insight.icon, fontSize = 36.sp, modifier = Modifier.padding(end = 16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = insight.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                color = Color.White
            )
            Text(
                text = insight.description,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f)
            )
            Text(
                text = "🎵 ${insight.songTitle} — ${insight.artistName}",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFFF0031),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

// ─── Shared ───────────────────────────────────────────────────────────────────

@Composable
private fun EmptyStateCard(icon: String, title: String, subtitle: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp)
    ) {
        Text(icon, fontSize = 48.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.5f),
            modifier = Modifier.padding(horizontal = 32.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
