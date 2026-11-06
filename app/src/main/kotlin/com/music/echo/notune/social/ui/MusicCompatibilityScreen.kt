package com.music.echo.notune.social.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.music.echo.notune.social.dna.MusicDnaAnalyzer
import com.music.echo.notune.theme.NoTuneAmbientCanvas
import com.music.echo.notune.theme.NoTuneSurfaceCard
import echo.music.iad1tya.constants.CardStyleVariant

@Composable
fun MusicCompatibilityScreen(
    friendUsername: String = "Friend",
    onStartListenTogether: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    val analyzer = remember { MusicDnaAnalyzer() }
    val compat = remember(friendUsername) { analyzer.calculateFriendCompatibility(friendUsername) }

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
            // Top Bar
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
                        text = "MUSIC TASTE COMPATIBILITY",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.5.sp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text("You & $friendUsername", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                if (!compat.hasSufficientData) {
                    item {
                        NoTuneSurfaceCard(cardStyle = CardStyleVariant.GLASS, modifier = Modifier.fillMaxWidth()) {
                            Text("COMPATIBILITY UNAVAILABLE", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(compat.explanationText, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    return@LazyColumn
                }

                // Match Header Card
                item {
                    NoTuneSurfaceCard(cardStyle = CardStyleVariant.GLASS, modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .background(Brush.linearGradient(listOf(Color(0xFFFF007A), Color(0xFF7C4DFF))), shape = RoundedCornerShape(20.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("${compat.overallMatchPercentage}%", fontWeight = FontWeight.Black, fontSize = 22.sp, color = Color.White)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("MUSIC MATCH SCORE", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                                Text("Incredible taste alignment across artists, moods & genres!", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onStartListenTogether,
                            modifier = Modifier.fillMaxWidth().height(46.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("📻 START LISTEN TOGETHER ROOM", fontWeight = FontWeight.Black)
                        }
                    }
                }

                // Match Breakdown Metrics
                item {
                    NoTuneSurfaceCard(cardStyle = CardStyleVariant.GLASS, modifier = Modifier.fillMaxWidth()) {
                        Text("📊 DETAILED METRIC BREAKDOWN", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            MetricItem("🎤 Artist", "${compat.artistMatchPercentage}%")
                            MetricItem("🎵 Genre", "${compat.genreMatchPercentage}%")
                            MetricItem("🌙 Mood", "${compat.moodMatchPercentage}%")
                            MetricItem("🗣️ Language", "${compat.languageMatchPercentage}%")
                        }
                    }
                }

                // Mutual Favorites
                item {
                    Text("🤝 SONGS YOU BOTH LOVE", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }

                items(compat.mutualLovedSongs) { song ->
                    NoTuneSurfaceCard(cardStyle = CardStyleVariant.GLASS, modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("❤️", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(song, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Introductions
                item {
                    Text("🔥 SONGS YOU CAN INTRODUCE TO THEM", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }

                items(compat.recommendedIntroductions) { song ->
                    NoTuneSurfaceCard(cardStyle = CardStyleVariant.GLASS, modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("💡", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(song, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricItem(label: String, score: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(score, fontWeight = FontWeight.Black, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
        Text(label, style = MaterialTheme.typography.labelSmall, fontSize = 10.sp)
    }
}
