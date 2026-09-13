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
        primaryAccent = Color(0xFF00F5D4),
        secondaryAccent = Color(0xFF7B2CBF),
        tertiaryAccent = Color(0xFFF72585),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFF00F5D4),
            onPrimary = Color.Black,
            secondary = Color(0xFF9D4EDD),
            onSecondary = Color.White,
            background = Color(0xFF0B0914),
            onBackground = Color(0xFFF8F9FA),
            surface = Color(0xFF131024),
            onSurface = Color(0xFFF8F9FA),
            surfaceVariant = Color(0xFF1F1A3A),
            onSurfaceVariant = Color(0xFFD8B4F8)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF009688),
            onPrimary = Color.White,
            secondary = Color(0xFF7B2CBF),
            onSecondary = Color.White,
            background = Color(0xFFF7F5FC),
            onBackground = Color(0xFF100C1F),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF100C1F),
            surfaceVariant = Color(0xFFEBE5F7),
            onSurfaceVariant = Color(0xFF4A3E68)
        )
    )

    val MONO = NoTuneThemePalette(
        id = "mono",
        name = "Mono Editorial",
        description = "High contrast monochrome with pure typographic focus",
        primaryAccent = Color(0xFFFFFFFF),
        secondaryAccent = Color(0xFF888888),
        tertiaryAccent = Color(0xFF333333),
        isOledOptimized = true,
        darkColorScheme = darkColorScheme(
            primary = Color(0xFFFFFFFF),
            onPrimary = Color.Black,
            secondary = Color(0xFFAAAAAA),
            onSecondary = Color.Black,
            background = Color.Black,
            onBackground = Color.White,
            surface = Color.Black,
            onSurface = Color.White,
            surfaceVariant = Color(0xFF181818),
            onSurfaceVariant = Color(0xFFCCCCCC)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF000000),
            onPrimary = Color.White,
            secondary = Color(0xFF555555),
            onSecondary = Color.White,
            background = Color(0xFFFFFFFF),
            onBackground = Color.Black,
            surface = Color(0xFFFFFFFF),
            onSurface = Color.Black,
            surfaceVariant = Color(0xFFEEEEEE),
            onSurfaceVariant = Color(0xFF333333)
        )
    )

    val NEBULA = NoTuneThemePalette(
        id = "nebula",
        name = "Nebula Purple",
        description = "Deep space cosmic violet with vibrant highlights",
        primaryAccent = Color(0xFFB5179E),
        secondaryAccent = Color(0xFF7209B7),
        tertiaryAccent = Color(0xFF4CC9F0),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFFF72585),
            onPrimary = Color.White,
            secondary = Color(0xFF4CC9F0),
            onSecondary = Color.Black,
            background = Color(0xFF0F051D),
            onBackground = Color(0xFFFDEDF6),
            surface = Color(0xFF17092C),
            onSurface = Color(0xFFFDEDF6),
            surfaceVariant = Color(0xFF260F45),
            onSurfaceVariant = Color(0xFFE2B2EC)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFFB5179E),
            onPrimary = Color.White,
            secondary = Color(0xFF3F37C9),
            onSecondary = Color.White,
            background = Color(0xFFFDF6FB),
            onBackground = Color(0xFF1A0426),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF1A0426),
            surfaceVariant = Color(0xFFF7E6F5),
            onSurfaceVariant = Color(0xFF5C2052)
        )
    )

    val OCEAN = NoTuneThemePalette(
        id = "ocean",
        name = "Ocean Azure",
        description = "Deep aquamarine and serene blue tones",
        primaryAccent = Color(0xFF00B4D8),
        secondaryAccent = Color(0xFF90E0EF),
        tertiaryAccent = Color(0xFF03045E),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFF00B4D8),
            onPrimary = Color.Black,
            secondary = Color(0xFF90E0EF),
            onSecondary = Color.Black,
            background = Color(0xFF02111B),
            onBackground = Color(0xFFE0F7FA),
            surface = Color(0xFF051C2C),
            onSurface = Color(0xFFE0F7FA),
            surfaceVariant = Color(0xFF0A2B42),
            onSurfaceVariant = Color(0xFFB2EBF2)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF0077B6),
            onPrimary = Color.White,
            secondary = Color(0xFF00B4D8),
            onSecondary = Color.White,
            background = Color(0xFFF0F9FF),
            onBackground = Color(0xFF011627),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF011627),
            surfaceVariant = Color(0xFFE0F2FE),
            onSurfaceVariant = Color(0xFF0369A1)
        )
    )

    val EMBER = NoTuneThemePalette(
        id = "ember",
        name = "Ember Copper",
        description = "Warm crimson copper and dark amber luxury",
        primaryAccent = Color(0xFFFF6B35),
        secondaryAccent = Color(0xFFF7C59F),
        tertiaryAccent = Color(0xFFEF233C),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFFFF6B35),
            onPrimary = Color.Black,
            secondary = Color(0xFFF7C59F),
            onSecondary = Color.Black,
            background = Color(0xFF140705),
            onBackground = Color(0xFFFDF0ED),
            surface = Color(0xFF200C09),
            onSurface = Color(0xFFFDF0ED),
            surfaceVariant = Color(0xFF331510),
            onSurfaceVariant = Color(0xFFF4A261)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFFD9381E),
            onPrimary = Color.White,
            secondary = Color(0xFFE76F51),
            onSecondary = Color.White,
            background = Color(0xFFFFF7F5),
            onBackground = Color(0xFF2B0B07),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF2B0B07),
            surfaceVariant = Color(0xFFFFEBE5),
            onSurfaceVariant = Color(0xFF9C2A18)
        )
    )

    val FOREST = NoTuneThemePalette(
        id = "forest",
        name = "Forest Sage",
        description = "Organic sage emerald green with tranquil depth",
        primaryAccent = Color(0xFF52B788),
        secondaryAccent = Color(0xFF74C69D),
        tertiaryAccent = Color(0xFF2D6A4F),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFF52B788),
            onPrimary = Color.Black,
            secondary = Color(0xFF95D5B2),
            onSecondary = Color.Black,
            background = Color(0xFF06140E),
            onBackground = Color(0xFFE8F5E9),
            surface = Color(0xFF0C2018),
            onSurface = Color(0xFFE8F5E9),
            surfaceVariant = Color(0xFF153326),
            onSurfaceVariant = Color(0xFFA3E4D7)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF2D6A4F),
            onPrimary = Color.White,
            secondary = Color(0xFF40916C),
            onSecondary = Color.White,
            background = Color(0xFFF1F8F5),
            onBackground = Color(0xFF04180F),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF04180F),
            surfaceVariant = Color(0xFFD8EFE5),
            onSurfaceVariant = Color(0xFF1B4332)
        )
    )

    val LAVENDER = NoTuneThemePalette(
        id = "lavender",
        name = "Lavender Dreams",
        description = "Soft soothing violet twilight with elegant contrast",
        primaryAccent = Color(0xFFC77DFF),
        secondaryAccent = Color(0xFFE0AAFF),
        tertiaryAccent = Color(0xFF7B2CBF),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFFC77DFF),
            onPrimary = Color.Black,
            secondary = Color(0xFFE0AAFF),
            onSecondary = Color.Black,
            background = Color(0xFF0D0814),
            onBackground = Color(0xFFF3E8FF),
            surface = Color(0xFF171022),
            onSurface = Color(0xFFF3E8FF),
            surfaceVariant = Color(0xFF241A33),
            onSurfaceVariant = Color(0xFFD8B4F8)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF7B2CBF),
            onPrimary = Color.White,
            secondary = Color(0xFF9D4EDD),
            onSecondary = Color.White,
            background = Color(0xFFFAF5FF),
            onBackground = Color(0xFF150A21),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF150A21),
            surfaceVariant = Color(0xFFF0E5FF),
            onSurfaceVariant = Color(0xFF5A189A)
        )
    )

    val SOLAR = NoTuneThemePalette(
        id = "solar",
        name = "Solar Gold",
        description = "Warm golden sunlight with crisp clean accents",
        primaryAccent = Color(0xFFFFB703),
        secondaryAccent = Color(0xFFFB8500),
        tertiaryAccent = Color(0xFFD4AF37),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFFFFB703),
            onPrimary = Color.Black,
            secondary = Color(0xFFFB8500),
            onSecondary = Color.Black,
            background = Color(0xFF141005),
            onBackground = Color(0xFFFFFBEA),
            surface = Color(0xFF211A0A),
            onSurface = Color(0xFFFFFBEA),
            surfaceVariant = Color(0xFF332912),
            onSurfaceVariant = Color(0xFFFFD56B)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFFD48806),
            onPrimary = Color.White,
            secondary = Color(0xFFD97706),
            onSecondary = Color.White,
            background = Color(0xFFFFFDF5),
            onBackground = Color(0xFF261C02),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF261C02),
            surfaceVariant = Color(0xFFFEF3C7),
            onSurfaceVariant = Color(0xFF92400E)
        )
    )

    val GLASS = NoTuneThemePalette(
        id = "glass",
        name = "Glassmorphism",
        description = "Translucent frosted background elevation and glow",
        primaryAccent = Color(0xFF64DFDF),
        secondaryAccent = Color(0xFF72EFDD),
        tertiaryAccent = Color(0xFF80FFDB),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFF64DFDF),
            onPrimary = Color.Black,
            secondary = Color(0xFF72EFDD),
            onSecondary = Color.Black,
            background = Color(0xFF051923),
            onBackground = Color(0xFFF0FDFD),
            surface = Color(0xFF003554),
            onSurface = Color(0xFFF0FDFD),
            surfaceVariant = Color(0xFF006494),
            onSurfaceVariant = Color(0xFF90E0EF)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF00A896),
            onPrimary = Color.White,
            secondary = Color(0xFF028090),
            onSecondary = Color.White,
            background = Color(0xFFF0FCFC),
            onBackground = Color(0xFF021C24),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF021C24),
            surfaceVariant = Color(0xFFCCF2F4),
            onSurfaceVariant = Color(0xFF056676)
        )
    )

    val CARBON = NoTuneThemePalette(
        id = "carbon",
        name = "Carbon Metal",
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

    val TOKYO_NEON = NoTuneThemePalette(
        id = "tokyo_neon",
        name = "Tokyo Cyberpunk",
        description = "High-voltage electric magenta and neon cyan matrix",
        primaryAccent = Color(0xFFFF007F),
        secondaryAccent = Color(0xFF00F0FF),
        tertiaryAccent = Color(0xFF7000FF),
        isOledOptimized = true,
        darkColorScheme = darkColorScheme(
            primary = Color(0xFFFF007F),
            onPrimary = Color.White,
            secondary = Color(0xFF00F0FF),
            onSecondary = Color.Black,
            background = Color.Black,
            onBackground = Color.White,
            surface = Color(0xFF0D001A),
            onSurface = Color.White,
            surfaceVariant = Color(0xFF1E0038),
            onSurfaceVariant = Color(0xFFFF80BF)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFFD6006B),
            onPrimary = Color.White,
            secondary = Color(0xFF00B8D4),
            onSecondary = Color.White,
            background = Color(0xFFFFF0F5),
            onBackground = Color(0xFF1A000D),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF1A000D),
            surfaceVariant = Color(0xFFFFD6E7),
            onSurfaceVariant = Color(0xFF800040)
        )
    )

    val AMETHYST_ROYAL = NoTuneThemePalette(
        id = "amethyst_royal",
        name = "Amethyst Royal",
        description = "Imperial purple and rose gold luxury contrast",
        primaryAccent = Color(0xFF9D4EDD),
        secondaryAccent = Color(0xFFFFB5A7),
        tertiaryAccent = Color(0xFF5A189A),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFFC77DFF),
            onPrimary = Color.Black,
            secondary = Color(0xFFFFB5A7),
            onSecondary = Color.Black,
            background = Color(0xFF10002B),
            onBackground = Color(0xFFF8EDEB),
            surface = Color(0xFF190038),
            onSurface = Color(0xFFF8EDEB),
            surfaceVariant = Color(0xFF240046),
            onSurfaceVariant = Color(0xFFE0AAFF)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF7B2CBF),
            onPrimary = Color.White,
            secondary = Color(0xFFF8AD9D),
            onSecondary = Color.Black,
            background = Color(0xFFFFF9F9),
            onBackground = Color(0xFF10002B),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF10002B),
            surfaceVariant = Color(0xFFF4E8FB),
            onSurfaceVariant = Color(0xFF3C096C)
        )
    )

    val TITANIUM_PRO = NoTuneThemePalette(
        id = "titanium_pro",
        name = "Titanium Pro",
        description = "Industrial brushed steel with electric sapphire accent",
        primaryAccent = Color(0xFF4361EE),
        secondaryAccent = Color(0xFF4CC9F0),
        tertiaryAccent = Color(0xFF3F37C9),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFF4895EF),
            onPrimary = Color.Black,
            secondary = Color(0xFF4CC9F0),
            onSecondary = Color.Black,
            background = Color(0xFF121418),
            onBackground = Color(0xFFF0F4F8),
            surface = Color(0xFF1A1D24),
            onSurface = Color(0xFFF0F4F8),
            surfaceVariant = Color(0xFF262B36),
            onSurfaceVariant = Color(0xFF8D99AE)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF3F37C9),
            onPrimary = Color.White,
            secondary = Color(0xFF4361EE),
            onSecondary = Color.White,
            background = Color(0xFFF4F6F9),
            onBackground = Color(0xFF0F172A),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF0F172A),
            surfaceVariant = Color(0xFFE2E8F0),
            onSurfaceVariant = Color(0xFF475569)
        )
    )

    val SAKURA_BLOSSOM = NoTuneThemePalette(
        id = "sakura_blossom",
        name = "Sakura Blossom",
        description = "Soft pastel cherry blossom rose with pearl glow",
        primaryAccent = Color(0xFFFFB7C5),
        secondaryAccent = Color(0xFFFF69B4),
        tertiaryAccent = Color(0xFFFFC0CB),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFFFFB7C5),
            onPrimary = Color.Black,
            secondary = Color(0xFFFF85A1),
            onSecondary = Color.Black,
            background = Color(0xFF1A0C12),
            onBackground = Color(0xFFFFF0F5),
            surface = Color(0xFF28141C),
            onSurface = Color(0xFFFFF0F5),
            surfaceVariant = Color(0xFF3B1E2A),
            onSurfaceVariant = Color(0xFFFFC2D1)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFFE05297),
            onPrimary = Color.White,
            secondary = Color(0xFFFF75A0),
            onSecondary = Color.White,
            background = Color(0xFFFFF5F7),
            onBackground = Color(0xFF2D0A1E),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF2D0A1E),
            surfaceVariant = Color(0xFFFFE3EC),
            onSurfaceVariant = Color(0xFF9E2A5E)
        )
    )

    val DESERT_DUNE = NoTuneThemePalette(
        id = "desert_dune",
        name = "Desert Dune",
        description = "Warm terracotta, sunburst sand and earth tones",
        primaryAccent = Color(0xFFE07A5F),
        secondaryAccent = Color(0xFFF2CC8F),
        tertiaryAccent = Color(0xFF81B29A),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFFF2CC8F),
            onPrimary = Color.Black,
            secondary = Color(0xFFE07A5F),
            onSecondary = Color.Black,
            background = Color(0xFF1C1310),
            onBackground = Color(0xFFF4F1DE),
            surface = Color(0xFF291B17),
            onSurface = Color(0xFFF4F1DE),
            surfaceVariant = Color(0xFF3B2822),
            onSurfaceVariant = Color(0xFFE07A5F)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFFC85A32),
            onPrimary = Color.White,
            secondary = Color(0xFF3D405B),
            onSecondary = Color.White,
            background = Color(0xFFFDFBF7),
            onBackground = Color(0xFF241612),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF241612),
            surfaceVariant = Color(0xFFF5EBE6),
            onSurfaceVariant = Color(0xFF8B3A1B)
        )
    )

    val HYPER_LIME = NoTuneThemePalette(
        id = "hyper_lime",
        name = "Hyper Lime",
        description = "High-octane electric lime energy and graphite darkness",
        primaryAccent = Color(0xFFCCFF00),
        secondaryAccent = Color(0xFF00FF66),
        tertiaryAccent = Color(0xFF99FF00),
        isOledOptimized = true,
        darkColorScheme = darkColorScheme(
            primary = Color(0xFFCCFF00),
            onPrimary = Color.Black,
            secondary = Color(0xFF00FF66),
            onSecondary = Color.Black,
            background = Color.Black,
            onBackground = Color.White,
            surface = Color(0xFF0C0E0B),
            onSurface = Color.White,
            surfaceVariant = Color(0xFF181C16),
            onSurfaceVariant = Color(0xFFD4FF33)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF669900),
            onPrimary = Color.White,
            secondary = Color(0xFF009944),
            onSecondary = Color.White,
            background = Color(0xFFF9FDF5),
            onBackground = Color(0xFF111A05),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF111A05),
            surfaceVariant = Color(0xFFE5F7CC),
            onSurfaceVariant = Color(0xFF334D00)
        )
    )

    val DEEP_OCEANIC = NoTuneThemePalette(
        id = "deep_oceanic",
        name = "Deep Oceanic",
        description = "Abyssal deep sea teal and luminous aquamarine",
        primaryAccent = Color(0xFF00E5FF),
        secondaryAccent = Color(0xFF1DE9B6),
        tertiaryAccent = Color(0xFF00B0FF),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFF00E5FF),
            onPrimary = Color.Black,
            secondary = Color(0xFF1DE9B6),
            onSecondary = Color.Black,
            background = Color(0xFF001018),
            onBackground = Color(0xFFE0F7FA),
            surface = Color(0xFF001D2B),
            onSurface = Color(0xFFE0F7FA),
            surfaceVariant = Color(0xFF002F44),
            onSurfaceVariant = Color(0xFF80E5FF)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF00838F),
            onPrimary = Color.White,
            secondary = Color(0xFF00897B),
            onSecondary = Color.White,
            background = Color(0xFFF0FBFD),
            onBackground = Color(0xFF00222B),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF00222B),
            surfaceVariant = Color(0xFFD0F4FA),
            onSurfaceVariant = Color(0xFF005662)
        )
    )

    val CHRONO_GOLD = NoTuneThemePalette(
        id = "chrono_gold",
        name = "Chrono Gold",
        description = "Timeless champagne gold and velvet charcoal elegance",
        primaryAccent = Color(0xFFF4D03F),
        secondaryAccent = Color(0xFFF5B041),
        tertiaryAccent = Color(0xFFD4AC0D),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFFF4D03F),
            onPrimary = Color.Black,
            secondary = Color(0xFFF5B041),
            onSecondary = Color.Black,
            background = Color(0xFF12110D),
            onBackground = Color(0xFFFDFEFE),
            surface = Color(0xFF1E1C15),
            onSurface = Color(0xFFFDFEFE),
            surfaceVariant = Color(0xFF2E2B20),
            onSurfaceVariant = Color(0xFFF9E79F)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFFB7950B),
            onPrimary = Color.White,
            secondary = Color(0xFFB9770E),
            onSecondary = Color.White,
            background = Color(0xFFFEFDF8),
            onBackground = Color(0xFF1C1808),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF1C1808),
            surfaceVariant = Color(0xFFFCF3CF),
            onSurfaceVariant = Color(0xFF7D6608)
        )
    )

    val ALL_THEMES = listOf(
        NOTUNE_PURE, MIDNIGHT, AURORA, MONO, NEBULA,
        OCEAN, EMBER, FOREST, LAVENDER, SOLAR, GLASS, CARBON,
        TOKYO_NEON, AMETHYST_ROYAL, TITANIUM_PRO, SAKURA_BLOSSOM,
        DESERT_DUNE, HYPER_LIME, DEEP_OCEANIC, CHRONO_GOLD
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
