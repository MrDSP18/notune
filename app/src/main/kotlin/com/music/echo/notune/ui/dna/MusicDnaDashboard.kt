package com.music.echo.notune.ui.dna

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import echo.music.iad1tya.ui.theme.NothingFont

@Composable
fun MusicDnaDashboard(
    personalityTitle: String = "MIDNIGHT EXPLORER",
    discoveredArtists: Int = 127,
    discoveredGenres: Int = 34,
    discoveredLanguages: Int = 18,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "YOUR MUSIC DNA",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = NothingFont,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = personalityTitle,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = NothingFont,
                            fontWeight = FontWeight.Black
                        ),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Audio Spectrum Traits
            DnaTraitBar("Energy", 0.80f, Color(0xFFEF4444))
            DnaTraitBar("Acoustic", 0.60f, Color(0xFFF59E0B))
            DnaTraitBar("Electronic", 0.40f, Color(0xFF3B82F6))
            DnaTraitBar("Instrumental", 0.70f, Color(0xFF10B981))

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "MOOD DISTRIBUTION",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = NothingFont,
                    color = Color(0xFFA1A1AA),
                    letterSpacing = 1.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .clip(RoundedCornerShape(6.dp))
            ) {
                Box(modifier = Modifier.weight(0.42f).fillMaxHeight().background(Color(0xFF818CF8)))
                Box(modifier = Modifier.weight(0.24f).fillMaxHeight().background(Color(0xFFF43F5E)))
                Box(modifier = Modifier.weight(0.18f).fillMaxHeight().background(Color(0xFF38BDF8)))
                Box(modifier = Modifier.weight(0.11f).fillMaxHeight().background(Color(0xFF34D399)))
                Box(modifier = Modifier.weight(0.05f).fillMaxHeight().background(Color(0xFFA1A1AA)))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "CALM 42% • ENERGETIC 24% • DREAMY 18% • FOCUSED 11%",
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont),
                color = Color(0xFF71717A)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Discovery Counters Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DnaStatItem("$discoveredArtists", "Artists")
                DnaStatItem("$discoveredGenres", "Genres")
                DnaStatItem("$discoveredLanguages", "Languages")
            }
        }
    }
}

@Composable
private fun DnaTraitBar(label: String, progress: Float, color: Color) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.White)
            Text(text = "${(progress * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, color = color)
        }
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = color,
            trackColor = Color(0xFF27272A)
        )
    }
}

@Composable
private fun DnaStatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = NothingFont,
                fontWeight = FontWeight.Black
            ),
            color = Color.White
        )
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont),
            color = Color(0xFF71717A)
        )
    }
}
