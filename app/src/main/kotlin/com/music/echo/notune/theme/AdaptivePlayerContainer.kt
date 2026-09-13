package com.music.echo.notune.theme

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import echo.music.iad1tya.constants.MiniPlayerStyleVariant
import echo.music.iad1tya.constants.PlayerStyleVariant

@Composable
fun AdaptivePlayerPreview(
    playerStyle: PlayerStyleVariant = PlayerStyleVariant.MINIMAL,
    miniPlayerStyle: MiniPlayerStyleVariant = MiniPlayerStyleVariant.COMPACT,
    songTitle: String = "Until I Found You",
    artistName: String = "Stephen Sanchez",
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceVariant.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                NoTuneLogo(variant = echo.music.iad1tya.constants.LogoVariant.SYMBOL, size = 24.dp)
                Text(
                    text = "PLAYER STAGE • ${playerStyle.title.uppercase()}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.5.sp),
                    color = primaryColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (playerStyle) {
                PlayerStyleVariant.MINIMAL -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(primaryColor.copy(alpha = 0.15f), secondaryColor.copy(alpha = 0.05f))
                                )
                            )
                            .padding(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Brush.linearGradient(listOf(primaryColor, secondaryColor))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🎵", fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(songTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(artistName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(
                            onClick = {},
                            modifier = Modifier
                                .size(42.dp)
                                .background(primaryColor, CircleShape)
                        ) {
                            Text("▶", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                PlayerStyleVariant.CLASSIC -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(Brush.radialGradient(listOf(primaryColor.copy(alpha = 0.4f), primaryColor.copy(alpha = 0.1f))))
                                .border(1.5.dp, primaryColor.copy(alpha = 0.5f), RoundedCornerShape(24.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🎵", fontSize = 44.sp)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(songTitle, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                        Text(artistName, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                PlayerStyleVariant.CINEMATIC -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(primaryColor.copy(alpha = 0.3f), Color.Black.copy(alpha = 0.8f))
                                )
                            )
                            .border(1.dp, primaryColor.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                            .padding(20.dp),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        Column {
                            Text("CINEMATIC STAGE", style = MaterialTheme.typography.labelSmall, color = primaryColor, fontWeight = FontWeight.Bold)
                            Text(songTitle, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = Color.White)
                            Text(artistName, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
                        }
                    }
                }
                PlayerStyleVariant.TYPOGRAPHY -> {
                    Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
                        Text(songTitle.uppercase(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                        Text(artistName, style = MaterialTheme.typography.titleMedium, color = primaryColor, fontWeight = FontWeight.Bold)
                    }
                }
                else -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .border(1.5.dp, Brush.horizontalGradient(listOf(primaryColor, secondaryColor)), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("$songTitle • $artistName", style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Soundwave Visualizer Bar
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
            ) {
                val barWidth = 6.dp.toPx()
                val gap = 4.dp.toPx()
                val totalBars = (size.width / (barWidth + gap)).toInt()
                val heights = listOf(0.4f, 0.7f, 0.3f, 0.9f, 0.5f, 0.8f, 0.6f, 0.2f, 1.0f, 0.7f)

                for (i in 0 until totalBars) {
                    val hFraction = heights[i % heights.size]
                    val barH = size.height * hFraction
                    val startX = i * (barWidth + gap)
                    drawRoundRect(
                        color = if (i % 2 == 0) primaryColor else secondaryColor,
                        topLeft = Offset(startX, (size.height - barH) / 2f),
                        size = androidx.compose.ui.geometry.Size(barWidth, barH),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(barWidth / 2f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "MINI-PLAYER VARIANT: ${miniPlayerStyle.title}",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
