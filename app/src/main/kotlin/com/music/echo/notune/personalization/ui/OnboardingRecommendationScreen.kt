package com.music.echo.notune.personalization.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.music.echo.notune.personalization.OnboardingRecommendedTrack
import com.music.echo.notune.personalization.OnboardingSongRecommender
import com.music.echo.notune.personalization.model.TasteProfile
import com.music.echo.notune.theme.NoTuneAmbientCanvas
import com.music.echo.notune.theme.NoTuneSurfaceCard
import echo.music.iad1tya.constants.CardStyleVariant

@Composable
fun OnboardingRecommendationScreen(
    tasteProfile: TasteProfile = TasteProfile(),
    onPlayAll: (List<OnboardingRecommendedTrack>) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    val recommender = remember { OnboardingSongRecommender() }
    val recommendedTracks = remember(tasteProfile) { recommender.generateOnboardingMix(tasteProfile) }

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
                            Text("${recommendedTracks.size} songs matched to your genres, artists & languages", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { onPlayAll(recommendedTracks) },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("▶ PLAY ONBOARDING MIX NOW", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Black)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Track List
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(recommendedTracks) { track ->
                    NoTuneSurfaceCard(
                        cardStyle = CardStyleVariant.GLASS,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🎵", fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(track.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text("${track.artist} • ${track.matchedGenre} • ${track.matchedLanguage}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(track.matchReason, style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = {}) {
                                Text("▶", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
