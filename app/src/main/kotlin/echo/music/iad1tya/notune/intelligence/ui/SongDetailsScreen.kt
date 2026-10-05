package echo.music.iad1tya.notune.intelligence.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Queue
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import echo.music.iad1tya.LocalPlayerConnection
import echo.music.iad1tya.notune.intelligence.knowledge.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongDetailsScreen(
    songId: String,
    navController: NavController,
    viewModel: KnowledgeViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val uiState by viewModel.songState.collectAsState()
    val playerConnection = LocalPlayerConnection.current
    val context = LocalContext.current

    LaunchedEffect(songId) {
        viewModel.loadSongDetails(songId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Deep Song Intelligence", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (val state = uiState) {
                is KnowledgeUiState.Loading -> {
                    SongDetailsSkeleton()
                }
                is KnowledgeUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadSongDetails(songId) }) {
                            Text("Retry")
                        }
                    }
                }
                is KnowledgeUiState.Success -> {
                    val song = state.data
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // HERO SECTION
                        item {
                            SongHeroHeader(
                                song = song,
                                onPlayClick = {
                                    // Play song without disrupting queue
                                },
                                onArtistClick = { personId ->
                                    if (!personId.isNullOrBlank()) {
                                        navController.safeNavigateEntity("person/$personId")
                                    }
                                },
                                onMovieClick = { movieId ->
                                    if (!movieId.isNullOrBlank()) {
                                        navController.safeNavigateEntity("movie/$movieId")
                                    }
                                },
                                onAlbumClick = { albumId ->
                                    if (!albumId.isNullOrBlank()) {
                                        navController.safeNavigateEntity("album/$albumId")
                                    }
                                }
                            )
                        }

                        // NØ AI EXPLANATION
                        item {
                            NoAiExplanationCard(
                                explanation = "Selected because you listened to high-energy tracks from this composer recently."
                            )
                        }

                        // METADATA GRID
                        item {
                            SongMetadataCard(song = song)
                        }

                        // CREDITS SECTION (EXPANDABLE)
                        item {
                            CreditsCard(
                                credits = song.credits,
                                onPersonClick = { personId ->
                                    if (!personId.isNullOrBlank()) {
                                        navController.safeNavigateEntity("person/$personId")
                                    }
                                }
                            )
                        }

                        // MOVIE / CAST SECTION
                        if (song.movieId != null || song.movieTitle != null) {
                            item {
                                MovieSummaryCard(
                                    movieId = song.movieId ?: "movie_unknown",
                                    movieTitle = song.movieTitle ?: "Unknown Movie",
                                    onMovieClick = { movieId ->
                                        if (!movieId.isNullOrBlank()) {
                                            navController.safeNavigateEntity("movie/$movieId")
                                        }
                                    }
                                )
                            }
                        }

                        // RELATED SONGS
                        if (song.relatedSongs.isNotEmpty()) {
                            item {
                                RelatedSongsSection(
                                    relatedSongs = song.relatedSongs,
                                    onSongClick = { relatedSongId ->
                                        if (!relatedSongId.isNullOrBlank()) {
                                            navController.safeNavigateEntity("song_details/$relatedSongId")
                                        }
                                    }
                                )
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SongDetailsSkeleton() {
    echo.music.iad1tya.ui.component.shimmer.ShimmerHost(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
        Spacer(modifier = Modifier.height(16.dp))
        echo.music.iad1tya.ui.component.shimmer.TextPlaceholder(
            height = 24.dp,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .align(Alignment.CenterHorizontally),
            shape = RoundedCornerShape(4.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        echo.music.iad1tya.ui.component.shimmer.TextPlaceholder(
            height = 16.dp,
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .align(Alignment.CenterHorizontally),
            shape = RoundedCornerShape(4.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        echo.music.iad1tya.ui.component.shimmer.TextPlaceholder(height = 18.dp, modifier = Modifier.fillMaxWidth(0.3f))
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
        }
    }
}

fun NavController.safeNavigateEntity(route: String) {
    if (route.isBlank()) return
    val currentRoute = currentBackStackEntry?.destination?.route
    if (currentRoute == route) return
    try {
        navigate(route) {
            launchSingleTop = true
        }
    } catch (e: Exception) {
        // Safe navigation fallback
    }
}

@Composable
fun SongHeroHeader(
    song: SongDetails,
    onPlayClick: () -> Unit,
    onArtistClick: (String) -> Unit,
    onMovieClick: (String) -> Unit,
    onAlbumClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                if (song.artwork?.url != null) {
                    AsyncImage(
                        model = song.artwork.url,
                        contentDescription = song.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        modifier = Modifier
                            .size(64.dp)
                            .align(Alignment.Center),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                val original = song.originalTitle
                if (!original.isNullOrBlank()) {
                    Text(
                        text = original,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val primaryArtist = song.artists.firstOrNull()
                    Text(
                        text = primaryArtist?.name ?: "Information unavailable",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            primaryArtist?.personId?.let { onArtistClick(it) }
                        }
                    )
                    if (song.albumTitle != null) {
                        Text(" • ", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = song.albumTitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.clickable {
                                song.albumId?.let { onAlbumClick(it) }
                            }
                        )
                    }
                }
            }

            // Quick Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalIconButton(onClick = onPlayClick) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Play")
                }
                OutlinedIconButton(onClick = { /* Queue */ }) {
                    Icon(Icons.Default.Queue, contentDescription = "Enqueue")
                }
                OutlinedIconButton(onClick = { /* Favorite */ }) {
                    Icon(Icons.Default.FavoriteBorder, contentDescription = "Favorite")
                }
                OutlinedIconButton(onClick = { /* Share */ }) {
                    Icon(Icons.Default.Share, contentDescription = "Share")
                }
            }
        }
    }
}

@Composable
fun NoAiExplanationCard(explanation: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.tertiary
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("NØ", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "NØ AI Playback Context",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = explanation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun SongMetadataCard(song: SongDetails) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Track Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Divider()

            MetadataRow("Language", song.language ?: "Information unavailable")
            MetadataRow("Genre", song.genre ?: "Information unavailable")
            MetadataRow("Mood", song.mood ?: "Information unavailable")
            MetadataRow("Energy", song.energy?.let { "%.0f%%".format(it * 100) } ?: "Information unavailable")
            MetadataRow("Tempo", song.bpm?.let { "%.0f BPM".format(it) } ?: "Information unavailable")
            MetadataRow("Duration", if (song.durationMs > 0) "%d:%02d".format(song.durationMs / 60000, (song.durationMs / 1000) % 60) else "Information unavailable")
            MetadataRow("Audio Quality", song.audioQuality ?: "Lossless Auto")
            MetadataRow("Metadata Source", song.source.name)
        }
    }
}

@Composable
fun MetadataRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun CreditsCard(
    credits: SongCredits,
    onPersonClick: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Song Credits & Personnel",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (expanded) "Hide" else "Show All",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            val singersText = if (credits.singers.isNotEmpty()) credits.singers.joinToString { it.name } else "Information unavailable"
            CreditItem("Singers", singersText, credits.singers, onPersonClick)

            val composersText = if (credits.composers.isNotEmpty()) credits.composers.joinToString { it.name } else "Information unavailable"
            CreditItem("Composer / Music Director", composersText, credits.composers, onPersonClick)

            AnimatedVisibility(visible = expanded) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    val lyricistsText = if (credits.lyricists.isNotEmpty()) credits.lyricists.joinToString { it.name } else "Information unavailable"
                    CreditItem("Lyricist", lyricistsText, credits.lyricists, onPersonClick)

                    val producersText = if (credits.producers.isNotEmpty()) credits.producers.joinToString { it.name } else "Information unavailable"
                    CreditItem("Producer", producersText, credits.producers, onPersonClick)

                    CreditItem("Music Label", credits.label ?: "Information unavailable", emptyList(), onPersonClick)
                }
            }
        }
    }
}

@Composable
fun CreditItem(
    label: String,
    value: String,
    people: List<PersonDetails>,
    onPersonClick: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (people.isNotEmpty()) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                people.forEach { person ->
                    AssistChip(
                        onClick = { onPersonClick(person.personId) },
                        label = { Text(person.name) },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    )
                }
            }
        } else {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun MovieSummaryCard(
    movieId: String,
    movieTitle: String,
    onMovieClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onMovieClick(movieId) },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Movie,
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Movie / Soundtrack",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = movieTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Open Movie"
            )
        }
    }
}

@Composable
fun RelatedSongsSection(
    relatedSongs: List<RelatedSong>,
    onSongClick: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Related Songs & Recommendations",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(relatedSongs) { related ->
                Card(
                    modifier = Modifier
                        .width(160.dp)
                        .clickable { onSongClick(related.songId) },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = related.title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = related.artist,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = related.relationshipReason,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

private fun String?.isNull_or_blank(): Boolean = this == null || this.isBlank()
