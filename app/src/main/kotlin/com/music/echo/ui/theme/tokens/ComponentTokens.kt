package com.music.echo.ui.theme.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class PlayerTokens(
    val artworkElevation: Dp = 12.dp,
    val controlSpacing: Dp = 24.dp,
    val progressHeight: Dp = 4.dp,
    val layoutStyle: String = "CLASSIC"
)

@Immutable
data class WidgetTokens(
    val cardCornerRadius: Dp = 16.dp,
    val opacity: Float = 0.9f,
    val headerStyle: String = "DOT_MATRIX",
    val accentGlow: Boolean = true
)

@Immutable
data class NavigationTokens(
    val navStyle: String = "FLOATING_PILL",
    val barHeight: Dp = 64.dp,
    val indicatorCornerRadius: Dp = 20.dp,
    val floatingPillElevation: Dp = 8.dp
)

@Immutable
data class LyricsTokens(
    val activeFontSizeSp: Float = 26f,
    val inactiveFontSizeSp: Float = 20f,
    val inactiveOpacity: Float = 0.4f,
    val alignment: String = "LEFT",
    val animationDurationMs: Int = 300
)

@Immutable
data class VisualizerTokens(
    val barWidthDp: Dp = 4.dp,
    val barGapDp: Dp = 2.dp,
    val maxBarHeightDp: Dp = 60.dp,
    val reactivitySpeedMs: Int = 50,
    val styleName: String = "SPECTRUM"
)
