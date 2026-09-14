package com.music.echo.notune.personalization.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.music.echo.notune.personalization.OnboardingRecommendedTrack
import com.music.echo.notune.personalization.OnboardingSongRecommender
import com.music.echo.notune.personalization.model.TasteProfile
import com.music.echo.notune.theme.NoTuneAmbientCanvas
import com.music.echo.notune.theme.NoTuneSurfaceCard
import echo.music.iad1tya.LocalPlayerConnection
import echo.music.iad1tya.constants.CardStyleVariant
import echo.music.iad1tya.extensions.toMediaItem
import echo.music.iad1tya.playback.queues.ListQueue

@Composable
fun OnboardingRecommendationScreen(
    tasteProfile: TasteProfile = TasteProfile(),
    onPlayAll: (List<OnboardingRecommendedTrack>) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    val recommender = remember { OnboardingSongRecommender() }
    val playerConnection = LocalPlayerConnection.current
    var recommendedTracks by remember { mutableStateOf<List<OnboardingRecommendedTrack>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(tasteProfile) {
        isLoading = true
        recommendedTracks = recommender.fetchOnboardingMix(tasteProfile)
        isLoading = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        NoTuneAmbientCanvas(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                IconButton(onClick = onDismiss) {
                    Text("✕", fontSize = 20.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "POST-ONBOARDING CURATION",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.5.sp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Tailored For Your Taste Selections",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
                    )
                }
            }

            // Summary Card
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                NoTuneSurfaceCard(
                    cardStyle = CardStyleVariant.GLASS,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Brush.linearGradient(listOf(Color(0xFFFF0031), Color(0xFFFF8800)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("✨", fontSize = 28.sp)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("ONBOARDING TASTE MIX", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                            val statusText = if (isLoading) "Curating songs based on your artists, genres & languages..." else "${recommendedTracks.size} songs tailored to your taste profile"
                            Text(statusText, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (recommendedTracks.isNotEmpty()) {
                                onPlayAll(recommendedTracks)
                                val mediaItems = recommendedTracks.mapNotNull { it.mediaMetadata?.toMediaItem() }
                                if (mediaItems.isNotEmpty()) {
                                    playerConnection?.playQueue(ListQueue("Onboarding Taste Mix", mediaItems, 0))
                                }
                            }
                        },
                        enabled = !isLoading && recommendedTracks.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("▶ PLAY ONBOARDING MIX NOW", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Black)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Matching real songs to your onboarding choices...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                // Track List
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(recommendedTracks) { track ->
                        NoTuneSurfaceCard(
                            cardStyle = CardStyleVariant.GLASS,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    track.mediaMetadata?.toMediaItem()?.let { mediaItem ->
                                        val mediaItems = recommendedTracks.mapNotNull { it.mediaMetadata?.toMediaItem() }
                                        val index = mediaItems.indexOf(mediaItem).coerceAtLeast(0)
                                        playerConnection?.playQueue(ListQueue("Onboarding Taste Mix", mediaItems, index))
                                    }
                                }
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (!track.thumbnailUrl.isNullOrBlank()) {
                                    AsyncImage(
                                        model = track.thumbnailUrl,
                                        contentDescription = track.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("🎵", fontSize = 20.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = track.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "${track.artist} • ${track.matchedGenre} • ${track.matchedLanguage}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = track.matchReason,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        maxLines = 1
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        track.mediaMetadata?.toMediaItem()?.let { mediaItem ->
                                            val mediaItems = recommendedTracks.mapNotNull { it.mediaMetadata?.toMediaItem() }
                                            val index = mediaItems.indexOf(mediaItem).coerceAtLeast(0)
                                            playerConnection?.playQueue(ListQueue("Onboarding Taste Mix", mediaItems, index))
                                        }
                                    }
                                ) {
                                    Text("▶", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

