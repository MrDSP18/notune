package com.music.echo.notune.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.music.echo.notune.ui.theme.NotuneColors
import com.music.echo.notune.ui.theme.NotuneTypography

/**
 * Nothing-inspired NØTUNE Player Container.
 * Features reactive glyph canvas around album artwork, technical monospace position seekbars,
 * and high-contrast control buttons.
 */
@Composable
fun NotunePlayerScreen(
    title: String,
    artist: String,
    positionMs: Long,
    durationMs: Long,
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Float) -> Unit,
    onOpenQueue: () -> Unit,
    onOpenLyrics: () -> Unit,
    onOpenAi: () -> Unit,
    modifier: Modifier = Modifier
) {
    val posSec = positionMs / 1000
    val durSec = durationMs / 1000
    val formattedPos = String.format("%02d:%02d", posSec / 60, posSec % 60)
    val formattedDur = String.format("%02d:%02d", durSec / 60, durSec % 60)
    val sliderValue = if (durationMs > 0) positionMs.toFloat() / durationMs.toFloat() else 0f

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NotuneColors.OledBlack)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "← NØTUNE", style = NotuneTypography.DotMatrixLarge)
            Text(text = "⋮", style = NotuneTypography.DotMatrixHeader)
        }

        // Center Reactive Glyph Artwork
        Box(
            modifier = Modifier
                .size(280.dp)
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            NotuneGlyph(
                isPlaying = isPlaying,
                energyLevel = 0.75f,
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .background(NotuneColors.DarkCard)
                        .border(1.dp, NotuneColors.SubtleBorder),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "ALBUM\nART",
                        style = NotuneTypography.DotMatrixMedium,
                        color = NotuneColors.SecondaryGray
                    )
                }
            }
        }

        // Metadata
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = artist.uppercase(), style = NotuneTypography.DotMatrixSmall.copy(color = NotuneColors.NothingRed))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = title, style = NotuneTypography.TechnicalHeader, maxLines = 1)
        }

        // Seekbar & Time Telemetry
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = formattedPos, style = NotuneTypography.DotMatrixMedium)
                Text(text = formattedDur, style = NotuneTypography.DotMatrixMedium.copy(color = NotuneColors.SecondaryGray))
            }
            NotuneSlider(
                value = sliderValue,
                onValueChange = { percent -> onSeek(percent * durationMs) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Playback Action Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "◀",
                style = NotuneTypography.DotMatrixHeader,
                modifier = Modifier.clickable(onClick = onPrevious)
            )
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(NotuneColors.PureWhite)
                    .clickable(onClick = onPlayPause),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isPlaying) "Ⅱ" else "▶",
                    style = NotuneTypography.DotMatrixHeader.copy(color = NotuneColors.OledBlack)
                )
            }
            Text(
                text = "▶",
                style = NotuneTypography.DotMatrixHeader,
                modifier = Modifier.clickable(onClick = onNext)
            )
        }

        NotuneDivider()

        // Bottom Navigation Bar: QUEUE | LYRICS | AI
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "QUEUE",
                style = NotuneTypography.DotMatrixMedium,
                modifier = Modifier.clickable(onClick = onOpenQueue)
            )
            Text(
                text = "LYRICS",
                style = NotuneTypography.DotMatrixMedium,
                modifier = Modifier.clickable(onClick = onOpenLyrics)
            )
            Text(
                text = "AI",
                style = NotuneTypography.DotMatrixMedium.copy(color = NotuneColors.NothingRedGlow),
                modifier = Modifier.clickable(onClick = onOpenAi)
            )
        }
    }
}

/**
 * Technical NØTUNE Mini Player Card.
 * Compact floating bottom player with thin progress indicator line.
 */
@Composable
fun NotuneMiniPlayer(
    title: String,
    artist: String,
    progressPercent: Float,
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onExpand: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(NotuneColors.DarkSurface)
            .border(1.dp, NotuneColors.SubtleBorder)
            .clickable(onClick = onExpand)
    ) {
        // Thin top progress bar line
        Box(
            modifier = Modifier
                .fillMaxWidth(progressPercent.coerceIn(0f, 1f))
                .height(2.dp)
                .background(NotuneColors.NothingRed)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "▣", style = NotuneTypography.DotMatrixMedium.copy(color = NotuneColors.NothingRed))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = title, style = NotuneTypography.TrackTitle, maxLines = 1)
                    Text(text = artist, style = NotuneTypography.ArtistName, maxLines = 1)
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isPlaying) "Ⅱ" else "▶",
                    style = NotuneTypography.DotMatrixLarge,
                    modifier = Modifier
                        .clickable(onClick = onPlayPause)
                        .padding(horizontal = 8.dp)
                )
                Text(
                    text = "▶",
                    style = NotuneTypography.DotMatrixLarge,
                    modifier = Modifier
                        .clickable(onClick = onNext)
                        .padding(horizontal = 8.dp)
                )
            }
        }
    }
}
