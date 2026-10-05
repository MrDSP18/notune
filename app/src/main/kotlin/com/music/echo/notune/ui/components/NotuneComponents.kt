package com.music.echo.notune.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.music.echo.notune.ui.theme.NotuneColors
import com.music.echo.notune.ui.theme.NotuneTypography

/**
 * Technical high-contrast button with Nothing Red accent borders.
 */
@Composable
fun NotuneButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = true,
    enabled: Boolean = true
) {
    val backgroundColor = if (isPrimary) NotuneColors.PureWhite else NotuneColors.OledBlack
    val textColor = if (isPrimary) NotuneColors.OledBlack else NotuneColors.PureWhite
    val borderColor = if (isPrimary) NotuneColors.PureWhite else NotuneColors.SubtleBorder

    Box(
        modifier = modifier
            .background(if (enabled) backgroundColor else NotuneColors.MutedGray, shape = CutCornerShape(0.dp))
            .border(1.dp, borderColor, CutCornerShape(0.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            style = NotuneTypography.DotMatrixMedium.copy(color = textColor)
        )
    }
}

/**
 * Numbered technical item row for Queue, Playlists, and Track Lists.
 * Displays formatted index ("01", "02"), track metadata, playing indicator, and duration.
 */
@Composable
fun NotuneRow(
    index: Int,
    title: String,
    subtitle: String,
    durationMs: Long,
    isPlaying: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formattedIndex = String.format("%02d", index)
    val minutes = (durationMs / 1000) / 60
    val seconds = (durationMs / 1000) % 60
    val formattedDuration = String.format("%02d:%02d", minutes, seconds)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(if (isPlaying) NotuneColors.DarkCard else NotuneColors.OledBlack)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = formattedIndex,
            style = NotuneTypography.DotMatrixMedium.copy(
                color = if (isPlaying) NotuneColors.NothingRed else NotuneColors.MutedGray
            ),
            modifier = Modifier.width(32.dp)
        )

        if (isPlaying) {
            Text(
                text = "●",
                style = NotuneTypography.DotMatrixSmall.copy(color = NotuneColors.NothingRed),
                modifier = Modifier.padding(end = 8.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = NotuneTypography.TrackTitle,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = NotuneTypography.ArtistName,
                maxLines = 1
            )
        }

        Text(
            text = formattedDuration,
            style = NotuneTypography.DotMatrixMedium.copy(color = NotuneColors.SecondaryGray)
        )
    }
}

/**
 * Thin 1dp sub-pixel technical grid divider.
 */
@Composable
fun NotuneDivider(
    modifier: Modifier = Modifier,
    color: Color = NotuneColors.SubtleBorder
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(color)
    )
}

/**
 * Technical precision seekbar with dot-matrix readouts.
 */
@Composable
fun NotuneSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    onValueChangeFinished: (() -> Unit)? = null
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        onValueChangeFinished = onValueChangeFinished,
        valueRange = valueRange,
        colors = SliderDefaults.colors(
            thumbColor = NotuneColors.NothingRed,
            activeTrackColor = NotuneColors.PureWhite,
            inactiveTrackColor = NotuneColors.SubtleBorder
        ),
        modifier = modifier
    )
}

/**
 * Terminal-style NØTUNE AI Operating System Control Center Status Indicator.
 */
@Composable
fun NotuneAIIndicator(
    isOnline: Boolean = true,
    currentActivity: String = "IDLE",
    contextEnergy: Int = 65,
    contextMood: String = "CALM",
    queueSize: Int = 12,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(NotuneColors.DarkSurface)
            .border(1.dp, NotuneColors.SubtleBorder)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "NØTUNE INTELLIGENCE",
                style = NotuneTypography.DotMatrixLarge
            )
            Text(
                text = if (isOnline) "● ONLINE" else "○ OFFLINE",
                style = NotuneTypography.DotMatrixMedium.copy(
                    color = if (isOnline) NotuneColors.DotMatrixGreen else NotuneColors.NothingRed
                )
            )
        }

        NotuneDivider(modifier = Modifier.padding(vertical = 12.dp))

        Text(
            text = "CURRENT CONTEXT",
            style = NotuneTypography.DotMatrixSmall.copy(color = NotuneColors.SecondaryGray)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "ENERGY: $contextEnergy%", style = NotuneTypography.DotMatrixMedium)
            Text(text = "MOOD: $contextMood", style = NotuneTypography.DotMatrixMedium)
            Text(text = "QUEUE: $queueSize TRACKS", style = NotuneTypography.DotMatrixMedium)
        }

        NotuneDivider(modifier = Modifier.padding(vertical = 12.dp))

        Text(
            text = "AI ACTIVITY: $currentActivity",
            style = NotuneTypography.TerminalPrompt
        )
    }
}

/**
 * Technical diagnostic telemetry interface for Music DNA.
 */
@Composable
fun NotuneMusicDnaView(
    energyLevel: Int = 72,
    discoveryRate: Int = 82,
    repetitionRate: Int = 21,
    topLanguage: String = "Tamil (41%)",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(NotuneColors.OledBlack)
            .border(1.dp, NotuneColors.SubtleBorder)
            .padding(16.dp)
    ) {
        Text(
            text = "MUSIC DNA TELEMETRY",
            style = NotuneTypography.DotMatrixHeader
        )
        NotuneDivider(modifier = Modifier.padding(vertical = 12.dp))

        // Energy Bar
        Text(text = "ENERGY SPECTRUM: $energyLevel%", style = NotuneTypography.DotMatrixMedium)
        Spacer(modifier = Modifier.height(4.dp))
        val energyBlocks = energyLevel / 10
        val energyBar = "█".repeat(energyBlocks) + "░".repeat(10 - energyBlocks)
        Text(text = energyBar, style = NotuneTypography.DotMatrixLarge.copy(color = NotuneColors.NothingRed))

        Spacer(modifier = Modifier.height(16.dp))

        // Discovery Rate
        Text(text = "DISCOVERY RATIO: $discoveryRate%", style = NotuneTypography.DotMatrixMedium)
        Spacer(modifier = Modifier.height(4.dp))
        val discoveryBlocks = discoveryRate / 10
        val discoveryBar = "█".repeat(discoveryBlocks) + "░".repeat(10 - discoveryBlocks)
        Text(text = discoveryBar, style = NotuneTypography.DotMatrixLarge.copy(color = NotuneColors.DotMatrixGreen))

        Spacer(modifier = Modifier.height(16.dp))

        // Repetition Rate
        Text(text = "REPETITION INDEX: $repetitionRate%", style = NotuneTypography.DotMatrixMedium)
        Spacer(modifier = Modifier.height(4.dp))
        val repetitionBlocks = repetitionRate / 10
        val repetitionBar = "█".repeat(repetitionBlocks) + "░".repeat(10 - repetitionBlocks)
        Text(text = repetitionBar, style = NotuneTypography.DotMatrixLarge.copy(color = NotuneColors.DotMatrixAmber))

        NotuneDivider(modifier = Modifier.padding(vertical = 12.dp))
        Text(text = "DOMINANT CATALOG: $topLanguage", style = NotuneTypography.DotMatrixMedium)
    }
}
