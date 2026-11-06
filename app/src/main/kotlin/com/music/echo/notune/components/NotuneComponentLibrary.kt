package com.music.echo.notune.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.music.echo.ui.theme.NotuneLogo
import com.music.echo.ui.theme.tokens.LocalThemeTokens
import echo.music.iad1tya.R

// ─── 1. NoTuneButton ────────────────────────────────────────────────────────
@Composable
fun NoTuneButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isSecondary: Boolean = false,
    icon: Int? = null
) {
    val tokens = LocalThemeTokens.current
    val containerColor = if (isSecondary) tokens.colors.surfaceVariant else tokens.colors.primary
    val contentColor = if (isSecondary) tokens.colors.textPrimary else tokens.colors.onPrimary

    Button(
        onClick = onClick,
        enabled = enabled,
        shape = tokens.shapes.cardShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        contentPadding = PaddingValues(horizontal = tokens.spacing.md, vertical = tokens.spacing.sm),
        modifier = modifier
    ) {
        if (icon != null) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(tokens.icon.sizeSmall)
            )
            Spacer(modifier = Modifier.width(tokens.spacing.xs))
        }
        Text(
            text = text,
            style = tokens.typography.labelLarge,
            color = contentColor
        )
    }
}

// ─── 2. NoTuneIconButton ──────────────────────────────────────────────────
@Composable
fun NoTuneIconButton(
    onClick: () -> Unit,
    iconRes: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalThemeTokens.current.colors.textPrimary
) {
    val tokens = LocalThemeTokens.current
    IconButton(
        onClick = onClick,
        modifier = modifier.size(tokens.icon.sizeLarge)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(tokens.icon.sizeMedium)
        )
    }
}

// ─── 3. NoTuneCard ─────────────────────────────────────────────────────────
@Composable
fun NoTuneCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    backgroundColor: Color = LocalThemeTokens.current.colors.card,
    borderColor: Color = LocalThemeTokens.current.colors.outline,
    content: @Composable ColumnScope.() -> Unit
) {
    val tokens = LocalThemeTokens.current
    Surface(
        shape = tokens.shapes.cardShape,
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
    ) {
        Column(
            modifier = Modifier.padding(tokens.spacing.md),
            content = content
        )
    }
}

// ─── 4. NoTuneChip ─────────────────────────────────────────────────────────
@Composable
fun NoTuneChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalThemeTokens.current
    Surface(
        shape = CircleShape,
        color = if (selected) tokens.colors.primary else tokens.colors.surfaceVariant,
        border = BorderStroke(1.dp, if (selected) tokens.colors.primary else tokens.colors.outline),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            style = tokens.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = if (selected) tokens.colors.onPrimary else tokens.colors.textPrimary,
            modifier = Modifier.padding(horizontal = tokens.spacing.md, vertical = tokens.spacing.xs)
        )
    }
}

// ─── 5. NoTuneSlider ───────────────────────────────────────────────────────
@Composable
fun NoTuneSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f
) {
    val tokens = LocalThemeTokens.current
    Slider(
        value = value,
        onValueChange = onValueChange,
        valueRange = valueRange,
        colors = SliderDefaults.colors(
            thumbColor = tokens.colors.primary,
            activeTrackColor = tokens.colors.primary,
            inactiveTrackColor = tokens.colors.outline
        ),
        modifier = modifier
    )
}

// ─── 6. NoTuneArtwork ──────────────────────────────────────────────────────
@Composable
fun NoTuneArtwork(
    url: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    shape: androidx.compose.ui.graphics.Shape = LocalThemeTokens.current.shapes.cardShape
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(LocalThemeTokens.current.colors.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (!url.isNullOrBlank()) {
            AsyncImage(
                model = url,
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                painter = painterResource(id = R.drawable.music_note),
                contentDescription = null,
                tint = LocalThemeTokens.current.colors.textSecondary,
                modifier = Modifier.size(size * 0.4f)
            )
        }
    }
}

// ─── 7. NoTuneSongRow ──────────────────────────────────────────────────────
@Composable
fun NoTuneSongRow(
    title: String,
    artist: String,
    artworkUrl: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isCurrentlyPlaying: Boolean = false,
    trailingContent: (@Composable () -> Unit)? = null
) {
    val tokens = LocalThemeTokens.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = tokens.spacing.xs, horizontal = tokens.spacing.sm)
    ) {
        NoTuneArtwork(
            url = artworkUrl,
            contentDescription = title,
            size = 48.dp
        )
        Spacer(modifier = Modifier.width(tokens.spacing.md))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = tokens.typography.titleSmall,
                color = if (isCurrentlyPlaying) tokens.colors.primary else tokens.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = artist,
                style = tokens.typography.bodySmall,
                color = tokens.colors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (trailingContent != null) {
            Spacer(modifier = Modifier.width(tokens.spacing.sm))
            trailingContent()
        }
    }
}

// ─── 8. NoTuneVisualizer ──────────────────────────────────────────────────
@Composable
fun NoTuneVisualizer(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    barCount: Int = 12,
    color: Color = LocalThemeTokens.current.colors.visualizer
) {
    val transition = rememberInfiniteTransition(label = "visualizer")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val barWidth = w / (barCount * 1.5f)
        val gap = barWidth * 0.5f

        for (i in 0 until barCount) {
            val factor = if (isPlaying) {
                0.2f + 0.8f * kotlin.math.abs(kotlin.math.sin(phase + i * 0.5f))
            } else 0.15f
            val barH = h * factor
            val x = i * (barWidth + gap) + gap / 2f
            val y = h - barH
            drawRoundRect(
                color = color,
                topLeft = Offset(x, y),
                size = Size(barWidth, barH),
                cornerRadius = CornerRadius(barWidth / 2f)
            )
        }
    }
}

// ─── 9. NoTuneSkeleton ────────────────────────────────────────────────────
@Composable
fun NoTuneSkeleton(
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape = LocalThemeTokens.current.shapes.cardShape
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = modifier
            .clip(shape)
            .background(LocalThemeTokens.current.colors.surfaceVariant.copy(alpha = alpha))
    )
}

// ─── 10. NoTuneEmptyState ─────────────────────────────────────────────────
@Composable
fun NoTuneEmptyState(
    message: String,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val tokens = LocalThemeTokens.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(tokens.spacing.xl)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.music_note),
            contentDescription = null,
            tint = tokens.colors.textSecondary,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(tokens.spacing.md))
        Text(
            text = message,
            style = tokens.typography.bodyMedium,
            color = tokens.colors.textSecondary
        )
        if (actionText != null && onAction != null) {
            Spacer(modifier = Modifier.height(tokens.spacing.md))
            NoTuneButton(text = actionText, onClick = onAction)
        }
    }
}

// ─── 11. NoTuneErrorState ─────────────────────────────────────────────────
@Composable
fun NoTuneErrorState(
    errorMessage: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalThemeTokens.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(tokens.spacing.xl)
    ) {
        Text(
            text = "⚠️",
            fontSize = 36.sp
        )
        Spacer(modifier = Modifier.height(tokens.spacing.sm))
        Text(
            text = errorMessage,
            style = tokens.typography.bodyMedium,
            color = tokens.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(tokens.spacing.md))
        NoTuneButton(text = "Retry", onClick = onRetry)
    }
}
