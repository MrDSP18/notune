package com.music.echo.notune.ui.theme

import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val NotuneDarkColorScheme = darkColorScheme(
    primary = NotuneColors.NothingRed,
    onPrimary = NotuneColors.PureWhite,
    primaryContainer = NotuneColors.DarkCard,
    onPrimaryContainer = NotuneColors.PureWhite,
    secondary = NotuneColors.SecondaryGray,
    onSecondary = NotuneColors.OledBlack,
    background = NotuneColors.OledBlack,
    onBackground = NotuneColors.PureWhite,
    surface = NotuneColors.DarkSurface,
    onSurface = NotuneColors.PureWhite,
    surfaceVariant = NotuneColors.DarkCard,
    onSurfaceVariant = NotuneColors.SecondaryGray,
    outline = NotuneColors.SubtleBorder
)

private val NotuneShapes = Shapes(
    small = CutCornerShape(0.dp),
    medium = CutCornerShape(2.dp),
    large = CutCornerShape(4.dp)
)

/**
 * NØTUNE Master Design System Theme Provider.
 * Enforces high-contrast OLED monochrome palette, sharp edges, and dot-matrix typography.
 */
@Composable
fun NotuneTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = NotuneDarkColorScheme,
        shapes = NotuneShapes,
        content = content
    )
}
