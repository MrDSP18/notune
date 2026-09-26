package com.music.echo.notune

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.music.echo.notune.theme.NoTuneAmbientCanvas
import com.music.echo.notune.theme.NoTuneSurfaceCard
import echo.music.iad1tya.constants.CardStyleVariant

@Composable
fun MusicDnaInspectorScreen(
    songTitle: String = "Starboy",
    artistName: String = "The Weeknd",
    bpm: Int = 124,
    camelotKey: String = "8A (A Minor)",
    energyLevel: Float = 0.88f,
    valenceMood: Float = 0.72f,
    loudnessLufs: Float = -6.4f,
    onBackClick: () -> Unit = {}
) {
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                IconButton(onClick = onBackClick) {
                    Text("←", fontSize = 24.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "TRACK MUSICOLOGY ANALYSIS",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.5.sp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Music DNA & Spectral Inspector",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                // Header Track Info Card
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
                            Text("🧬", fontSize = 24.sp)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(songTitle, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                            Text(artistName, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                com.music.echo.notune.ui.components.NotuneMusicDnaView(
                    energyLevel = (energyLevel * 100).toInt(),
                    discoveryRate = 82,
                    repetitionRate = 21,
                    topLanguage = "Tamil (41%)",
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Key & Tempo Card Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    NoTuneSurfaceCard(
                        cardStyle = CardStyleVariant.GLASS,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("HARMONIC KEY", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(camelotKey, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                    }

                    NoTuneSurfaceCard(
                        cardStyle = CardStyleVariant.GLASS,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("BPM TEMPO", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("$bpm BPM", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Energy & Valence Radar Card
                NoTuneSurfaceCard(
                    cardStyle = CardStyleVariant.GLASS,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("ENERGY & MOOD METRICS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Dance Energy: ${(energyLevel * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    LinearProgressIndicator(
                        progress = { energyLevel },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFFFF0031)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Valence / Happiness Mood: ${(valenceMood * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    LinearProgressIndicator(
                        progress = { valenceMood },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF00E5FF)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Integrated Loudness: $loudnessLufs LUFS", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    LinearProgressIndicator(
                        progress = { (loudnessLufs + 18f) / 18f },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFFA855F7)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
