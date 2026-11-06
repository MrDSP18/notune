package com.music.echo.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.music.echo.ui.theme.tokens.*
import echo.music.iad1tya.constants.ThemePreset

object NotuneThemeCatalog {
    fun getTokens(preset: ThemePreset, customAccent: Color = Color(0xFFFF0031)): ThemeTokens {
        return when (preset) {
            ThemePreset.NOTHING, ThemePreset.NOTUNE_PURE, ThemePreset.STITCH -> ThemeTokens(
                name = "NØTUNE VOID",
                colors = ColorTokens(
                    primary = Color(0xFFFF0031),
                    onPrimary = Color.White,
                    secondary = Color(0xFFE2E8F0),
                    accent = Color(0xFFFF0031),
                    background = Color(0xFF000000),
                    surface = Color(0xFF0D0D0D),
                    surfaceVariant = Color(0xFF1A1A1A),
                    card = Color(0xFF121212),
                    textPrimary = Color.White,
                    textSecondary = Color(0xFF94A3B8),
                    outline = Color(0xFF27272A),
                    progress = Color(0xFFFF0031),
                    visualizer = Color(0xFFFF0031),
                    lyricsHighlight = Color(0xFFFF0031),
                    glow = Color(0x33FF0031)
                ),
                shapes = ShapeTokens(
                    cardShape = RoundedCornerShape(4.dp),
                    artworkCornerRadius = 4.dp
                ),
                navigation = NavigationTokens(navStyle = "FLOATING_PILL")
            )

            ThemePreset.NEON, ThemePreset.TOKYO_NEON -> ThemeTokens(
                name = "NEON PULSE",
                colors = ColorTokens(
                    primary = Color(0xFF00F0FF),
                    onPrimary = Color.Black,
                    secondary = Color(0xFFFF007F),
                    accent = Color(0xFF00F0FF),
                    background = Color(0xFF070B19),
                    surface = Color(0xFF0F172A),
                    surfaceVariant = Color(0x3300F0FF),
                    card = Color(0xFF1E293B),
                    textPrimary = Color.White,
                    textSecondary = Color(0xFF38BDF8),
                    outline = Color(0xFF00F0FF),
                    progress = Color(0xFF00F0FF),
                    visualizer = Color(0xFFFF007F),
                    lyricsHighlight = Color(0xFF00F0FF),
                    glow = Color(0x6600F0FF)
                ),
                shapes = ShapeTokens(
                    cardShape = RoundedCornerShape(16.dp),
                    artworkCornerRadius = 16.dp
                ),
                blur = BlurTokens(glass = 24.dp),
                navigation = NavigationTokens(navStyle = "GLASS")
            )

            ThemePreset.AURORA -> ThemeTokens(
                name = "AURORA",
                colors = ColorTokens(
                    primary = Color(0xFF48CAE4),
                    onPrimary = Color.Black,
                    secondary = Color(0xFF06D6A0),
                    accent = Color(0xFF7209B7),
                    background = Color(0xFF0B132B),
                    surface = Color(0xFF1C2541),
                    surfaceVariant = Color(0x2248CAE4),
                    card = Color(0xFF1C2541),
                    textPrimary = Color.White,
                    textSecondary = Color(0xFF90E0EF),
                    outline = Color(0xFF48CAE4),
                    progress = Color(0xFF48CAE4),
                    visualizer = Color(0xFF06D6A0),
                    lyricsHighlight = Color(0xFF48CAE4),
                    glow = Color(0x4448CAE4)
                ),
                shapes = ShapeTokens(
                    cardShape = RoundedCornerShape(20.dp),
                    artworkCornerRadius = 20.dp
                ),
                navigation = NavigationTokens(navStyle = "CLASSIC")
            )

            ThemePreset.SOLAR -> ThemeTokens(
                name = "SOLAR",
                colors = ColorTokens(
                    primary = Color(0xFFF59E0B),
                    onPrimary = Color.Black,
                    secondary = Color(0xFFEA580C),
                    accent = Color(0xFFD97706),
                    background = Color(0xFFFFFBEB),
                    surface = Color(0xFFFEF3C7),
                    surfaceVariant = Color(0xFFFDE68A),
                    card = Color(0xFFFFFFFF),
                    textPrimary = Color(0xFF1F2937),
                    textSecondary = Color(0xFF6B7280),
                    outline = Color(0xFFF59E0B),
                    progress = Color(0xFFF59E0B),
                    visualizer = Color(0xFFEA580C),
                    lyricsHighlight = Color(0xFFD97706),
                    glow = Color(0x33F59E0B)
                ),
                shapes = ShapeTokens(
                    cardShape = RoundedCornerShape(16.dp),
                    artworkCornerRadius = 16.dp
                ),
                navigation = NavigationTokens(navStyle = "FLOATING_PILL")
            )

            ThemePreset.MONO -> ThemeTokens(
                name = "MONOCHROME",
                colors = ColorTokens(
                    primary = Color.White,
                    onPrimary = Color.Black,
                    secondary = Color(0xFFCCCCCC),
                    accent = Color.White,
                    background = Color.Black,
                    surface = Color(0xFF121212),
                    surfaceVariant = Color(0xFF222222),
                    card = Color(0xFF181818),
                    textPrimary = Color.White,
                    textSecondary = Color(0xFFA0A0A0),
                    outline = Color(0xFF444444),
                    progress = Color.White,
                    visualizer = Color.White,
                    lyricsHighlight = Color.White,
                    glow = Color(0x22FFFFFF)
                ),
                shapes = ShapeTokens(
                    cardShape = RectangleShape,
                    artworkCornerRadius = 0.dp
                ),
                navigation = NavigationTokens(navStyle = "MINIMAL_RAIL")
            )

            ThemePreset.CYBERPUNK -> ThemeTokens(
                name = "CYBERPUNK",
                colors = ColorTokens(
                    primary = Color(0xFFFF0055),
                    onPrimary = Color.White,
                    secondary = Color(0xFF00F0FF),
                    accent = Color(0xFFFFE600),
                    background = Color(0xFF090014),
                    surface = Color(0xFF18002E),
                    surfaceVariant = Color(0x33FF0055),
                    card = Color(0xFF240046),
                    textPrimary = Color.White,
                    textSecondary = Color(0xFF00F0FF),
                    outline = Color(0xFFFF0055),
                    progress = Color(0xFFFF0055),
                    visualizer = Color(0xFFFFE600),
                    lyricsHighlight = Color(0xFF00F0FF),
                    glow = Color(0x66FF0055)
                ),
                shapes = ShapeTokens(
                    cardShape = RoundedCornerShape(2.dp),
                    artworkCornerRadius = 2.dp
                ),
                navigation = NavigationTokens(navStyle = "TECHNICAL")
            )

            ThemePreset.RETRO, ThemePreset.SYNTHWAVE, ThemePreset.VAPORWAVE -> ThemeTokens(
                name = "RETRO WAVE",
                colors = ColorTokens(
                    primary = Color(0xFFFF71CE),
                    onPrimary = Color.Black,
                    secondary = Color(0xFF01CDFE),
                    accent = Color(0xFF05FFA1),
                    background = Color(0xFF180A29),
                    surface = Color(0xFF2B124C),
                    surfaceVariant = Color(0x33FF71CE),
                    card = Color(0xFF3D1A68),
                    textPrimary = Color.White,
                    textSecondary = Color(0xFFB983FF),
                    outline = Color(0xFFFF71CE),
                    progress = Color(0xFFFF71CE),
                    visualizer = Color(0xFF05FFA1),
                    lyricsHighlight = Color(0xFF01CDFE),
                    glow = Color(0x55FF71CE)
                ),
                shapes = ShapeTokens(
                    cardShape = RoundedCornerShape(8.dp),
                    artworkCornerRadius = 8.dp
                ),
                navigation = NavigationTokens(navStyle = "FLOATING_PILL")
            )

            ThemePreset.GLASS -> ThemeTokens(
                name = "GLASSFLOW",
                colors = ColorTokens(
                    primary = Color(0xFF38BDF8),
                    onPrimary = Color.Black,
                    secondary = Color(0xFF818CF8),
                    accent = Color(0xFF38BDF8),
                    background = Color(0xFF070F1E),
                    surface = Color(0x330F172A),
                    surfaceVariant = Color(0x22FFFFFF),
                    card = Color(0x2A1E293B),
                    textPrimary = Color.White,
                    textSecondary = Color(0xFF94A3B8),
                    outline = Color(0x44FFFFFF),
                    progress = Color(0xFF38BDF8),
                    visualizer = Color(0xFF818CF8),
                    lyricsHighlight = Color(0xFF38BDF8),
                    glow = Color(0x3338BDF8),
                    glassFill = Color(0x22FFFFFF),
                    glassBorder = Color(0x44FFFFFF)
                ),
                shapes = ShapeTokens(
                    cardShape = RoundedCornerShape(24.dp),
                    artworkCornerRadius = 24.dp
                ),
                blur = BlurTokens(glass = 32.dp),
                navigation = NavigationTokens(navStyle = "GLASS")
            )

            ThemePreset.TITANIUM_PRO, ThemePreset.CARBON -> ThemeTokens(
                name = "STUDIO",
                colors = ColorTokens(
                    primary = Color(0xFF3B82F6),
                    onPrimary = Color.White,
                    secondary = Color(0xFF64748B),
                    accent = Color(0xFF10B981),
                    background = Color(0xFF0F172A),
                    surface = Color(0xFF1E293B),
                    surfaceVariant = Color(0xFF334155),
                    card = Color(0xFF1E293B),
                    textPrimary = Color(0xFFF8FAFC),
                    textSecondary = Color(0xFF94A3B8),
                    outline = Color(0xFF475569),
                    progress = Color(0xFF3B82F6),
                    visualizer = Color(0xFF10B981),
                    lyricsHighlight = Color(0xFF3B82F6),
                    glow = Color(0x333B82F6)
                ),
                shapes = ShapeTokens(
                    cardShape = RoundedCornerShape(6.dp),
                    artworkCornerRadius = 6.dp
                ),
                navigation = NavigationTokens(navStyle = "TECHNICAL")
            )

            ThemePreset.FOREST, ThemePreset.DEEP_OCEANIC -> ThemeTokens(
                name = "ORGANIC",
                colors = ColorTokens(
                    primary = Color(0xFF10B981),
                    onPrimary = Color.Black,
                    secondary = Color(0xFF34D399),
                    accent = Color(0xFF059669),
                    background = Color(0xFF041711),
                    surface = Color(0xFF0B291F),
                    surfaceVariant = Color(0x2210B981),
                    card = Color(0xFF0F3629),
                    textPrimary = Color(0xFFECFDF5),
                    textSecondary = Color(0xFF6EE7B7),
                    outline = Color(0xFF10B981),
                    progress = Color(0xFF10B981),
                    visualizer = Color(0xFF34D399),
                    lyricsHighlight = Color(0xFF10B981),
                    glow = Color(0x3310B981)
                ),
                shapes = ShapeTokens(
                    cardShape = CircleShape,
                    artworkCornerRadius = 18.dp
                ),
                navigation = NavigationTokens(navStyle = "FLOATING_PILL")
            )

            else -> ThemeTokens(
                name = "CUSTOM",
                colors = ColorTokens(
                    primary = customAccent,
                    accent = customAccent,
                    progress = customAccent,
                    visualizer = customAccent,
                    lyricsHighlight = customAccent
                )
            )
        }
    }
}
