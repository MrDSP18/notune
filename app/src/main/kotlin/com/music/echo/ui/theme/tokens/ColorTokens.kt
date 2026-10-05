package com.music.echo.ui.theme.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class ColorTokens(
    val primary: Color = Color(0xFFFF0031),
    val onPrimary: Color = Color.White,
    val secondary: Color = Color(0xFFE2E8F0),
    val accent: Color = Color(0xFFFF0031),
    val background: Color = Color(0xFF000000),
    val surface: Color = Color(0xFF0D0D0D),
    val surfaceVariant: Color = Color(0xFF1A1A1A),
    val card: Color = Color(0xFF121212),
    val textPrimary: Color = Color.White,
    val textSecondary: Color = Color(0xFF94A3B8),
    val outline: Color = Color(0xFF27272A),
    val progress: Color = Color(0xFFFF0031),
    val visualizer: Color = Color(0xFFFF0031),
    val lyricsHighlight: Color = Color(0xFFFF0031),
    val glow: Color = Color(0x33FF0031),
    val glassFill: Color = Color(0x1AFFFFFF),
    val glassBorder: Color = Color(0x33FFFFFF)
)
