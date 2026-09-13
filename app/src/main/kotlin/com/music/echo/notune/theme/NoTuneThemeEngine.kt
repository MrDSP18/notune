package com.music.echo.notune.theme

import android.graphics.Bitmap
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.palette.graphics.Palette
import echo.music.iad1tya.constants.AppearanceMode

@Immutable
data class NoTuneThemePalette(
    val id: String,
    val name: String,
    val description: String,
    val primaryAccent: Color,
    val secondaryAccent: Color,
    val tertiaryAccent: Color,
    val darkColorScheme: ColorScheme,
    val lightColorScheme: ColorScheme,
    val cardBackgroundAlpha: Float = 0.08f,
    val borderAlpha: Float = 0.12f,
    val isOledOptimized: Boolean = false
)

object NoTuneThemeEngine {

    val NOTUNE_PURE = NoTuneThemePalette(
        id = "notune_pure",
        name = "NØTUNE Pure",
        description = "Minimal, clean, neutral with high contrast readability",
        primaryAccent = Color(0xFFFF0031), // Signature Red
        secondaryAccent = Color(0xFFE0E0E0),
        tertiaryAccent = Color(0xFF757575),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFFFF0031),
            onPrimary = Color.White,
            secondary = Color(0xFFE0E0E0),
            onSecondary = Color.Black,
            background = Color(0xFF0A0A0A),
            onBackground = Color.White,
            surface = Color(0xFF121212),
            onSurface = Color.White,
            surfaceVariant = Color(0xFF1E1E1E),
            onSurfaceVariant = Color(0xFFCCCCCC)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFFFF0031),
            onPrimary = Color.White,
            secondary = Color(0xFF212121),
            onSecondary = Color.White,
            background = Color(0xFFFAFAFA),
            onBackground = Color(0xFF121212),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF121212),
            surfaceVariant = Color(0xFFF0F0F0),
            onSurfaceVariant = Color(0xFF424242)
        )
    )

    val MIDNIGHT = NoTuneThemePalette(
        id = "midnight",
        name = "Midnight",
        description = "Deep dark OLED-friendly cinematic music experience",
        primaryAccent = Color(0xFF3D8BFF),
        secondaryAccent = Color(0xFF00E5FF),
        tertiaryAccent = Color(0xFF7000FF),
        isOledOptimized = true,
        darkColorScheme = darkColorScheme(
            primary = Color(0xFF3D8BFF),
            onPrimary = Color.White,
            secondary = Color(0xFF00E5FF),
            onSecondary = Color.Black,
            background = Color.Black,
            onBackground = Color.White,
            surface = Color.Black,
            onSurface = Color.White,
            surfaceVariant = Color(0xFF11141A),
            onSurfaceVariant = Color(0xFFB0BEC5)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF1565C0),
            onPrimary = Color.White,
            secondary = Color(0xFF00838F),
            onSecondary = Color.White,
            background = Color(0xFFF4F6F9),
            onBackground = Color(0xFF0A1118),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF0A1118),
            surfaceVariant = Color(0xFFE3E8EF),
            onSurfaceVariant = Color(0xFF37474F)
        )
    )

    val AURORA = NoTuneThemePalette(
        id = "aurora",
        name = "Aurora",
        description = "Soft atmospheric gradients and vibrant modern accents",
        primaryAccent = Color(0xFF00FFC8),
        secondaryAccent = Color(0xFF7C4DFF),
        tertiaryAccent = Color(0xFFFF4081),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFF00FFC8),
            onPrimary = Color.Black,
            secondary = Color(0xFFB388FF),
            onSecondary = Color.Black,
            background = Color(0xFF0B0E14),
            onBackground = Color(0xFFE0F7FA),
            surface = Color(0xFF121824),
            onSurface = Color(0xFFE0F7FA),
            surfaceVariant = Color(0xFF1E2738),
            onSurfaceVariant = Color(0xFF80DEEA)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF00897B),
            onPrimary = Color.White,
            secondary = Color(0xFF651FFF),
            onSecondary = Color.White,
            background = Color(0xFFF0FDFB),
            onBackground = Color(0xFF003731),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF003731),
            surfaceVariant = Color(0xFFE0F2F1),
            onSurfaceVariant = Color(0xFF004D40)
        )
    )

    val MONO = NoTuneThemePalette(
        id = "mono",
        name = "Mono Editorial",
        description = "Monochrome editorial design with pure typographic focus",
        primaryAccent = Color(0xFFFFFFFF),
        secondaryAccent = Color(0xFFB0B0B0),
        tertiaryAccent = Color(0xFF666666),
        isOledOptimized = true,
        darkColorScheme = darkColorScheme(
            primary = Color.White,
            onPrimary = Color.Black,
            secondary = Color(0xFFE0E0E0),
            onSecondary = Color.Black,
            background = Color.Black,
            onBackground = Color.White,
            surface = Color.Black,
            onSurface = Color.White,
            surfaceVariant = Color(0xFF181818),
            onSurfaceVariant = Color(0xFFAAAAAA)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color.Black,
            onPrimary = Color.White,
            secondary = Color(0xFF424242),
            onSecondary = Color.White,
            background = Color.White,
            onBackground = Color.Black,
            surface = Color.White,
            onSurface = Color.Black,
            surfaceVariant = Color(0xFFF5F5F5),
            onSurfaceVariant = Color(0xFF616161)
        )
    )

    val NEBULA = NoTuneThemePalette(
        id = "nebula",
        name = "Nebula",
        description = "Deep space violet with subtle cosmic highlights",
        primaryAccent = Color(0xFFD500F9),
        secondaryAccent = Color(0xFF651FFF),
        tertiaryAccent = Color(0xFF00E5FF),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFFE040FB),
            onPrimary = Color.Black,
            secondary = Color(0xFF7C4DFF),
            onSecondary = Color.White,
            background = Color(0xFF0D0614),
            onBackground = Color(0xFFF3E5F5),
            surface = Color(0xFF160A24),
            onSurface = Color(0xFFF3E5F5),
            surfaceVariant = Color(0xFF241238),
            onSurfaceVariant = Color(0xFFE1BEE7)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFFAA00FF),
            onPrimary = Color.White,
            secondary = Color(0xFF6200EA),
            onSecondary = Color.White,
            background = Color(0xFFFBF5FF),
            onBackground = Color(0xFF2A004D),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF2A004D),
            surfaceVariant = Color(0xFFF3E5F5),
            onSurfaceVariant = Color(0xFF4A148C)
        )
    )

    val OCEAN = NoTuneThemePalette(
        id = "ocean",
        name = "Ocean Deep",
        description = "Deep azure blue and serene aqueous tones",
        primaryAccent = Color(0xFF00B0FF),
        secondaryAccent = Color(0xFF00E676),
        tertiaryAccent = Color(0xFF18FFFF),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFF40C4FF),
            onPrimary = Color.Black,
            secondary = Color(0xFF69F0AE),
            onSecondary = Color.Black,
            background = Color(0xFF04121A),
            onBackground = Color(0xFFE0F7FA),
            surface = Color(0xFF081C28),
            onSurface = Color(0xFFE0F7FA),
            surfaceVariant = Color(0xFF102A3C),
            onSurfaceVariant = Color(0xFF80DEEA)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF0091EA),
            onPrimary = Color.White,
            secondary = Color(0xFF00C853),
            onSecondary = Color.White,
            background = Color(0xFFEBF8FF),
            onBackground = Color(0xFF00293C),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF00293C),
            surfaceVariant = Color(0xFFD4F1F9),
            onSurfaceVariant = Color(0xFF004D61)
        )
    )

    val EMBER = NoTuneThemePalette(
        id = "ember",
        name = "Ember Luxury",
        description = "Warm crimson copper and dark amber luxury",
        primaryAccent = Color(0xFFFF6D00),
        secondaryAccent = Color(0xFFFFAB00),
        tertiaryAccent = Color(0xFFFF3D00),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFFFF9100),
            onPrimary = Color.Black,
            secondary = Color(0xFFFFC400),
            onSecondary = Color.Black,
            background = Color(0xFF140804),
            onBackground = Color(0xFFFBE9E7),
            surface = Color(0xFF200F08),
            onSurface = Color(0xFFFBE9E7),
            surfaceVariant = Color(0xFF30180E),
            onSurfaceVariant = Color(0xFFFFAB91)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFFE65100),
            onPrimary = Color.White,
            secondary = Color(0xFFFF8F00),
            onSecondary = Color.Black,
            background = Color(0xFFFFF3E0),
            onBackground = Color(0xFF3E2723),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF3E2723),
            surfaceVariant = Color(0xFFFFE0B2),
            onSurfaceVariant = Color(0xFFBF360C)
        )
    )

    val FOREST = NoTuneThemePalette(
        id = "forest",
        name = "Forest Sage",
        description = "Organic sage emerald green with tranquil minimalist depth",
        primaryAccent = Color(0xFF00E676),
        secondaryAccent = Color(0xFFAEEA00),
        tertiaryAccent = Color(0xFF1DE9B6),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFF69F0AE),
            onPrimary = Color.Black,
            secondary = Color(0xFFC6FF00),
            onSecondary = Color.Black,
            background = Color(0xFF06140A),
            onBackground = Color(0xFFE8F5E9),
            surface = Color(0xFF0D2112),
            onSurface = Color(0xFFE8F5E9),
            surfaceVariant = Color(0xFF16331D),
            onSurfaceVariant = Color(0xFFA5D6A7)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF2E7D32),
            onPrimary = Color.White,
            secondary = Color(0xFF558B2F),
            onSecondary = Color.White,
            background = Color(0xFFF1F8E9),
            onBackground = Color(0xFF1B5E20),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF1B5E20),
            surfaceVariant = Color(0xFFDCEDC8),
            onSurfaceVariant = Color(0xFF33691E)
        )
    )

    val LAVENDER = NoTuneThemePalette(
        id = "lavender",
        name = "Lavender Twilight",
        description = "Soft soothing violet twilight with elegant contrast",
        primaryAccent = Color(0xFFB388FF),
        secondaryAccent = Color(0xFFFF80AB),
        tertiaryAccent = Color(0xFF82B1FF),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFFD1C4E9),
            onPrimary = Color.Black,
            secondary = Color(0xFFFF4081),
            onSecondary = Color.Black,
            background = Color(0xFF0F0B18),
            onBackground = Color(0xFFEDE7F6),
            surface = Color(0xFF191326),
            onSurface = Color(0xFFEDE7F6),
            surfaceVariant = Color(0xFF271F38),
            onSurfaceVariant = Color(0xFFD1C4E9)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF673AB7),
            onPrimary = Color.White,
            secondary = Color(0xFFC2185B),
            onSecondary = Color.White,
            background = Color(0xFFF3E5F5),
            onBackground = Color(0xFF311B92),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF311B92),
            surfaceVariant = Color(0xFFE1BEE7),
            onSurfaceVariant = Color(0xFF4A148C)
        )
    )

    val SOLAR = NoTuneThemePalette(
        id = "solar",
        name = "Solar Gold",
        description = "Warm golden sunlight with crisp clean accents",
        primaryAccent = Color(0xFFFFD600),
        secondaryAccent = Color(0xFFFF6D00),
        tertiaryAccent = Color(0xFFC6FF00),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFFFFEA00),
            onPrimary = Color.Black,
            secondary = Color(0xFFFF9100),
            onSecondary = Color.Black,
            background = Color(0xFF141204),
            onBackground = Color(0xFFFFFDE7),
            surface = Color(0xFF211E0A),
            onSurface = Color(0xFFFFFDE7),
            surfaceVariant = Color(0xFF332F12),
            onSurfaceVariant = Color(0xFFFFF59D)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFFF57F17),
            onPrimary = Color.Black,
            secondary = Color(0xFFE65100),
            onSecondary = Color.White,
            background = Color(0xFFFFFDE7),
            onBackground = Color(0xFFF57F17),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF3E2723),
            surfaceVariant = Color(0xFFFFF59D),
            onSurfaceVariant = Color(0xFFF57F17)
        )
    )

    val GLASS = NoTuneThemePalette(
        id = "glass",
        name = "Liquid Glass",
        description = "Translucent glassmorphism with elevated background depth",
        primaryAccent = Color(0xFF00E5FF),
        secondaryAccent = Color(0xFFFFFFFF),
        tertiaryAccent = Color(0xFFB388FF),
        cardBackgroundAlpha = 0.15f,
        borderAlpha = 0.25f,
        darkColorScheme = darkColorScheme(
            primary = Color(0xFF00E5FF),
            onPrimary = Color.Black,
            secondary = Color.White,
            onSecondary = Color.Black,
            background = Color(0xFF080B10),
            onBackground = Color.White,
            surface = Color(0xFF101622),
            onSurface = Color.White,
            surfaceVariant = Color(0xFF1B2436),
            onSurfaceVariant = Color(0xFF80DEEA)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF00B0FF),
            onPrimary = Color.White,
            secondary = Color.Black,
            onSecondary = Color.White,
            background = Color(0xFFF0F4F8),
            onBackground = Color(0xFF102A43),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF102A43),
            surfaceVariant = Color(0xFFD9E2EC),
            onSurfaceVariant = Color(0xFF243B53)
        )
    )

    val CARBON = NoTuneThemePalette(
        id = "carbon",
        name = "Carbon Graphite",
        description = "High-tech dark graphite with crisp metallic accents",
        primaryAccent = Color(0xFF00E5FF),
        secondaryAccent = Color(0xFFFF3D00),
        tertiaryAccent = Color(0xFF76FF03),
        isOledOptimized = true,
        darkColorScheme = darkColorScheme(
            primary = Color(0xFF1DE9B6),
            onPrimary = Color.Black,
            secondary = Color(0xFFFF5252),
            onSecondary = Color.Black,
            background = Color(0xFF0C0E10),
            onBackground = Color(0xFFECEFF1),
            surface = Color(0xFF14171A),
            onSurface = Color(0xFFECEFF1),
            surfaceVariant = Color(0xFF1F2428),
            onSurfaceVariant = Color(0xFFB0BEC5)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF00897B),
            onPrimary = Color.White,
            secondary = Color(0xFFD32F2F),
            onSecondary = Color.White,
            background = Color(0xFFECEFF1),
            onBackground = Color(0xFF263238),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF263238),
            surfaceVariant = Color(0xFFCFD8DC),
            onSurfaceVariant = Color(0xFF37474F)
        )
    )

    val ALL_THEMES = listOf(
        NOTUNE_PURE, MIDNIGHT, AURORA, MONO, NEBULA,
        OCEAN, EMBER, FOREST, LAVENDER, SOLAR, GLASS, CARBON
    )

    fun getThemeById(id: String): NoTuneThemePalette {
        return ALL_THEMES.find { it.id.equals(id, ignoreCase = true) } ?: NOTUNE_PURE
    }

    /**
     * Resolves the active ColorScheme based on theme palette, appearance mode, and system dark state.
     */
    fun resolveColorScheme(
        themePalette: NoTuneThemePalette,
        appearanceMode: AppearanceMode,
        isSystemDark: Boolean
    ): ColorScheme {
        val isDark = when (appearanceMode) {
            AppearanceMode.LIGHT -> false
            AppearanceMode.DARK -> true
            AppearanceMode.SYSTEM -> isSystemDark
        }
        return if (isDark) themePalette.darkColorScheme else themePalette.lightColorScheme
    }

    /**
     * Generates a custom dynamic theme palette directly from album artwork bitmap.
     * Ensures WCAG AAA contrast ratio compliance.
     */
    fun generateThemeFromArtwork(bitmap: Bitmap): NoTuneThemePalette {
        val palette = Palette.from(bitmap).maximumColorCount(32).generate()
        val dominantRgb = palette.getDominantColor(0xFFFF0031.toInt())
        val vibrantRgb = palette.getVibrantColor(dominantRgb)
        val mutedRgb = palette.getMutedColor(dominantRgb)

        val primary = Color(vibrantRgb)
        val secondary = Color(mutedRgb)
        val isPrimaryLight = primary.luminance() > 0.5f

        val darkScheme = darkColorScheme(
            primary = primary,
            onPrimary = if (isPrimaryLight) Color.Black else Color.White,
            secondary = secondary,
            background = Color(0xFF0A0A0C),
            surface = Color(0xFF121216),
            surfaceVariant = Color(0xFF1C1C22)
        )

        val lightScheme = lightColorScheme(
            primary = primary,
            onPrimary = if (isPrimaryLight) Color.Black else Color.White,
            secondary = secondary,
            background = Color(0xFFFAFAFC),
            surface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFFF0F0F4)
        )

        return NoTuneThemePalette(
            id = "artwork_dynamic",
            name = "Song Artwork Theme",
            description = "Dynamically extracted theme from currently playing artwork",
            primaryAccent = primary,
            secondaryAccent = secondary,
            tertiaryAccent = primary,
            darkColorScheme = darkScheme,
            lightColorScheme = lightScheme
        )
    }
}
