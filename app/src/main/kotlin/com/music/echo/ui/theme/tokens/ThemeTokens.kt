package com.music.echo.ui.theme.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

@Immutable
data class ThemeTokens(
    val name: String = "NØTUNE VOID",
    val colors: ColorTokens = ColorTokens(),
    val typography: TypographyTokens = TypographyTokens(),
    val spacing: SpacingTokens = SpacingTokens(),
    val shapes: ShapeTokens = ShapeTokens(),
    val elevation: ElevationTokens = ElevationTokens(),
    val motion: MotionTokens = MotionTokens(),
    val blur: BlurTokens = BlurTokens(),
    val icon: IconTokens = IconTokens(),
    val player: PlayerTokens = PlayerTokens(),
    val widget: WidgetTokens = WidgetTokens(),
    val navigation: NavigationTokens = NavigationTokens(),
    val lyrics: LyricsTokens = LyricsTokens(),
    val visualizer: VisualizerTokens = VisualizerTokens()
)

val LocalThemeTokens = staticCompositionLocalOf { ThemeTokens() }
