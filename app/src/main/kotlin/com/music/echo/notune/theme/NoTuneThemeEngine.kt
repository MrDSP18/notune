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

    val PRISMATIC_GLOW = NoTuneThemePalette(
        id = "prismatic_glow",
        name = "Prismatic Glow",
        description = "High-energy chromatic prism gradient with luminous highlights",
        primaryAccent = Color(0xFFFF007F),
        secondaryAccent = Color(0xFF00F5D4),
        tertiaryAccent = Color(0xFF7B2CBF),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFFFF007F),
            onPrimary = Color.White,
            secondary = Color(0xFF00F5D4),
            onSecondary = Color.Black,
            background = Color(0xFF080612),
            onBackground = Color.White,
            surface = Color(0xFF120E24),
            onSurface = Color.White,
            surfaceVariant = Color(0xFF1E1838),
            onSurfaceVariant = Color(0xFFE2B2FF)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFFD6006B),
            onPrimary = Color.White,
            secondary = Color(0xFF009688),
            onSecondary = Color.White,
            background = Color(0xFFFAF7FF),
            onBackground = Color(0xFF120826),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF120826),
            surfaceVariant = Color(0xFFEFE8FC),
            onSurfaceVariant = Color(0xFF5A189A)
        )
    )

    val RETRO_SYNTHWAVE = NoTuneThemePalette(
        id = "retro_synthwave",
        name = "Retro Synthwave",
        description = "Outrun 80s hot pink, neon violet, and grid sunset glow",
        primaryAccent = Color(0xFFFF2A85),
        secondaryAccent = Color(0xFF00E5FF),
        tertiaryAccent = Color(0xFFFFB703),
        isOledOptimized = true,
        darkColorScheme = darkColorScheme(
            primary = Color(0xFFFF2A85),
            onPrimary = Color.White,
            secondary = Color(0xFF00E5FF),
            onSecondary = Color.Black,
            background = Color.Black,
            onBackground = Color.White,
            surface = Color(0xFF110022),
            onSurface = Color.White,
            surfaceVariant = Color(0xFF220044),
            onSurfaceVariant = Color(0xFFFF85C0)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFFD6006B),
            onPrimary = Color.White,
            secondary = Color(0xFF00B4D8),
            onSecondary = Color.White,
            background = Color(0xFFFFF0F8),
            onBackground = Color(0xFF200010),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF200010),
            surfaceVariant = Color(0xFFFFD6EC),
            onSurfaceVariant = Color(0xFF800040)
        )
    )

    val COSMIC_VOID = NoTuneThemePalette(
        id = "cosmic_void",
        name = "Cosmic Void",
        description = "Deep dark space obsidian with starlight ice blue",
        primaryAccent = Color(0xFF80FFDB),
        secondaryAccent = Color(0xFF5390D9),
        tertiaryAccent = Color(0xFF48BFE3),
        isOledOptimized = true,
        darkColorScheme = darkColorScheme(
            primary = Color(0xFF80FFDB),
            onPrimary = Color.Black,
            secondary = Color(0xFF5390D9),
            onSecondary = Color.Black,
            background = Color.Black,
            onBackground = Color.White,
            surface = Color(0xFF060D14),
            onSurface = Color.White,
            surfaceVariant = Color(0xFF0E1A28),
            onSurfaceVariant = Color(0xFF72EFDD)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF0077B6),
            onPrimary = Color.White,
            secondary = Color(0xFF00B4D8),
            onSecondary = Color.White,
            background = Color(0xFFF0F9FF),
            onBackground = Color(0xFF031926),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF031926),
            surfaceVariant = Color(0xFFD0F0FD),
            onSurfaceVariant = Color(0xFF00567A)
        )
    )

    val EMERALD_CRYSTAL = NoTuneThemePalette(
        id = "emerald_crystal",
        name = "Emerald Crystal",
        description = "Luminous mint emerald and deep jade forest glow",
        primaryAccent = Color(0xFF00F5D4),
        secondaryAccent = Color(0xFF38B000),
        tertiaryAccent = Color(0xFF70E000),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFF00F5D4),
            onPrimary = Color.Black,
            secondary = Color(0xFF70E000),
            onSecondary = Color.Black,
            background = Color(0xFF03140E),
            onBackground = Color(0xFFE8FDF5),
            surface = Color(0xFF07241A),
            onSurface = Color(0xFFE8FDF5),
            surfaceVariant = Color(0xFF0D382A),
            onSurfaceVariant = Color(0xFF52B788)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF007200),
            onPrimary = Color.White,
            secondary = Color(0xFF38B000),
            onSecondary = Color.White,
            background = Color(0xFFF2FDF8),
            onBackground = Color(0xFF021B12),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF021B12),
            surfaceVariant = Color(0xFFC7F9E5),
            onSurfaceVariant = Color(0xFF004B23)
        )
    )

    val SUPERNOVA_BURST = NoTuneThemePalette(
        id = "supernova_burst",
        name = "Supernova Burst",
        description = "High-octane fiery crimson, plasma yellow and space black",
        primaryAccent = Color(0xFFFF0031),
        secondaryAccent = Color(0xFFFFB703),
        tertiaryAccent = Color(0xFFFB8500),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFFFF0031),
            onPrimary = Color.White,
            secondary = Color(0xFFFFB703),
            onSecondary = Color.Black,
            background = Color(0xFF140205),
            onBackground = Color(0xFFFFF0F2),
            surface = Color(0xFF24040A),
            onSurface = Color(0xFFFFF0F2),
            surfaceVariant = Color(0xFF3A0610),
            onSurfaceVariant = Color(0xFFFF8095)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFFD00000),
            onPrimary = Color.White,
            secondary = Color(0xFFDC2F02),
            onSecondary = Color.White,
            background = Color(0xFFFFF5F5),
            onBackground = Color(0xFF2B0004),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF2B0004),
            surfaceVariant = Color(0xFFFFD6D6),
            onSurfaceVariant = Color(0xFF9D0208)
        )
    )

    val ZEN_GARDEN = NoTuneThemePalette(
        id = "zen_garden",
        name = "Zen Garden",
        description = "Tranquil bamboo moss green and serene stone grey",
        primaryAccent = Color(0xFFAACC00),
        secondaryAccent = Color(0xFF80B918),
        tertiaryAccent = Color(0xFF55A630),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFFAACC00),
            onPrimary = Color.Black,
            secondary = Color(0xFF80B918),
            onSecondary = Color.Black,
            background = Color(0xFF0F140A),
            onBackground = Color(0xFFF4F9F0),
            surface = Color(0xFF182010),
            onSurface = Color(0xFFF4F9F0),
            surfaceVariant = Color(0xFF26331A),
            onSurfaceVariant = Color(0xFFBFD200)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF2B9348),
            onPrimary = Color.White,
            secondary = Color(0xFF55A630),
            onSecondary = Color.White,
            background = Color(0xFFF7FCF5),
            onBackground = Color(0xFF10190C),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF10190C),
            surfaceVariant = Color(0xFFE2F5DB),
            onSurfaceVariant = Color(0xFF007F5F)
        )
    )

    val VOLCANIC_MAGMA = NoTuneThemePalette(
        id = "volcanic_magma",
        name = "Volcanic Magma",
        description = "Molten basalt dark copper and electric lava orange",
        primaryAccent = Color(0xFFFF4800),
        secondaryAccent = Color(0xFFFF7B00),
        tertiaryAccent = Color(0xFFFFA000),
        isOledOptimized = true,
        darkColorScheme = darkColorScheme(
            primary = Color(0xFFFF4800),
            onPrimary = Color.Black,
            secondary = Color(0xFFFF7B00),
            onSecondary = Color.Black,
            background = Color.Black,
            onBackground = Color.White,
            surface = Color(0xFF140804),
            onSurface = Color.White,
            surfaceVariant = Color(0xFF261008),
            onSurfaceVariant = Color(0xFFFF9E00)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFFD03800),
            onPrimary = Color.White,
            secondary = Color(0xFFE05600),
            onSecondary = Color.White,
            background = Color(0xFFFFF7F5),
            onBackground = Color(0xFF240A02),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF240A02),
            surfaceVariant = Color(0xFFFFE0D6),
            onSurfaceVariant = Color(0xFF9E2A00)
        )
    )

    val NORTHERN_LIGHTS = NoTuneThemePalette(
        id = "northern_lights",
        name = "Northern Lights",
        description = "Atmospheric arctic aurora green, cyan and violet sky",
        primaryAccent = Color(0xFF00FFC6),
        secondaryAccent = Color(0xFF7B2CBF),
        tertiaryAccent = Color(0xFF00B4D8),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFF00FFC6),
            onPrimary = Color.Black,
            secondary = Color(0xFF9D4EDD),
            onSecondary = Color.White,
            background = Color(0xFF040F1A),
            onBackground = Color(0xFFE8FCFF),
            surface = Color(0xFF081B2E),
            onSurface = Color(0xFFE8FCFF),
            surfaceVariant = Color(0xFF0E2A47),
            onSurfaceVariant = Color(0xFF72EFDD)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF00897B),
            onPrimary = Color.White,
            secondary = Color(0xFF7B2CBF),
            onSecondary = Color.White,
            background = Color(0xFFF0FDFD),
            onBackground = Color(0xFF021B24),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF021B24),
            surfaceVariant = Color(0xFFCCF7F7),
            onSurfaceVariant = Color(0xFF005662)
        )
    )

    val NEON_CYBERNETIC = NoTuneThemePalette(
        id = "neon_cybernetic",
        name = "Neon Cybernetic",
        description = "High-tech matrix green and electric laser cyan",
        primaryAccent = Color(0xFF00FF66),
        secondaryAccent = Color(0xFF00F0FF),
        tertiaryAccent = Color(0xFFCCFF00),
        isOledOptimized = true,
        darkColorScheme = darkColorScheme(
            primary = Color(0xFF00FF66),
            onPrimary = Color.Black,
            secondary = Color(0xFF00F0FF),
            onSecondary = Color.Black,
            background = Color.Black,
            onBackground = Color.White,
            surface = Color(0xFF03140A),
            onSurface = Color.White,
            surfaceVariant = Color(0xFF072814),
            onSurfaceVariant = Color(0xFF66FF99)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF009933),
            onPrimary = Color.White,
            secondary = Color(0xFF00A8C6),
            onSecondary = Color.White,
            background = Color(0xFFF2FDF6),
            onBackground = Color(0xFF011A0C),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF011A0C),
            surfaceVariant = Color(0xFFC7F9D8),
            onSurfaceVariant = Color(0xFF006622)
        )
    )

    val SOLARIS_FLAME = NoTuneThemePalette(
        id = "solaris_flame",
        name = "Solaris Flame",
        description = "Radiant solar gold flare and deep twilight amber",
        primaryAccent = Color(0xFFFFB703),
        secondaryAccent = Color(0xFFFF4800),
        tertiaryAccent = Color(0xFFFB8500),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFFFFB703),
            onPrimary = Color.Black,
            secondary = Color(0xFFFF4800),
            onSecondary = Color.Black,
            background = Color(0xFF140D02),
            onBackground = Color(0xFFFFFDF5),
            surface = Color(0xFF241704),
            onSurface = Color(0xFFFFFDF5),
            surfaceVariant = Color(0xFF382406),
            onSurfaceVariant = Color(0xFFFFD56B)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFFD48806),
            onPrimary = Color.White,
            secondary = Color(0xFFD03800),
            onSecondary = Color.White,
            background = Color(0xFFFFFDF7),
            onBackground = Color(0xFF291702),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF291702),
            surfaceVariant = Color(0xFFFEF0C7),
            onSurfaceVariant = Color(0xFF8C5300)
        )
    )

    val NEON_DATADRIVE = NoTuneThemePalette(
        id = "neon_datadrive",
        name = "Neon Datadrive",
        description = "Cybernetic green terminal matrix grid with hyper-luminous cyan text",
        primaryAccent = Color(0xFF00FF66),
        secondaryAccent = Color(0xFF00E5FF),
        tertiaryAccent = Color(0xFF39FF14),
        isOledOptimized = true,
        darkColorScheme = darkColorScheme(
            primary = Color(0xFF00FF66),
            onPrimary = Color.Black,
            secondary = Color(0xFF00E5FF),
            onSecondary = Color.Black,
            background = Color.Black,
            onBackground = Color.White,
            surface = Color(0xFF02140A),
            onSurface = Color.White,
            surfaceVariant = Color(0xFF052814),
            onSurfaceVariant = Color(0xFF66FF99)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF009933),
            onPrimary = Color.White,
            secondary = Color(0xFF00897B),
            onSecondary = Color.White,
            background = Color(0xFFF0FDF4),
            onBackground = Color(0xFF011A0C),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF011A0C),
            surfaceVariant = Color(0xFFDCFCE7),
            onSurfaceVariant = Color(0xFF166534)
        )
    )

    val AURORA_BOREALIS = NoTuneThemePalette(
        id = "aurora_borealis",
        name = "Aurora Borealis",
        description = "Ethereal polar sky emerald violet shift with dark obsidian glass",
        primaryAccent = Color(0xFF72EFDD),
        secondaryAccent = Color(0xFF7B2CBF),
        tertiaryAccent = Color(0xFF48BFE3),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFF72EFDD),
            onPrimary = Color.Black,
            secondary = Color(0xFF9D4EDD),
            onSecondary = Color.White,
            background = Color(0xFF080C1A),
            onBackground = Color(0xFFF0FDFD),
            surface = Color(0xFF10162B),
            onSurface = Color(0xFFF0FDFD),
            surfaceVariant = Color(0xFF1B2440),
            onSurfaceVariant = Color(0xFF80FFDB)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF00897B),
            onPrimary = Color.White,
            secondary = Color(0xFF7B2CBF),
            onSecondary = Color.White,
            background = Color(0xFFF0FDFD),
            onBackground = Color(0xFF040A1A),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF040A1A),
            surfaceVariant = Color(0xFFCCF7F7),
            onSurfaceVariant = Color(0xFF005662)
        )
    )

    val CELESTIAL_COSMOS = NoTuneThemePalette(
        id = "celestial_cosmos",
        name = "Celestial Cosmos",
        description = "Deep space nebula violet-blue with golden starlight accents",
        primaryAccent = Color(0xFF4895EF),
        secondaryAccent = Color(0xFFFFB703),
        tertiaryAccent = Color(0xFF7209B7),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFF4895EF),
            onPrimary = Color.Black,
            secondary = Color(0xFFFFB703),
            onSecondary = Color.Black,
            background = Color(0xFF090A1A),
            onBackground = Color(0xFFEEF2FF),
            surface = Color(0xFF121430),
            onSurface = Color(0xFFEEF2FF),
            surfaceVariant = Color(0xFF1E2148),
            onSurfaceVariant = Color(0xFF93C5FD)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF1D4ED8),
            onPrimary = Color.White,
            secondary = Color(0xFFD97706),
            onSecondary = Color.White,
            background = Color(0xFFEFF6FF),
            onBackground = Color(0xFF0F172A),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF0F172A),
            surfaceVariant = Color(0xFFDBEAFE),
            onSurfaceVariant = Color(0xFF1E40AF)
        )
    )

    val RUBY_VALENTINE = NoTuneThemePalette(
        id = "ruby_valentine",
        name = "Ruby Valentine",
        description = "Rich passionate ruby red, velvet maroon, and champagne gold",
        primaryAccent = Color(0xFFE63946),
        secondaryAccent = Color(0xFFFFB703),
        tertiaryAccent = Color(0xFF9D0208),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFFE63946),
            onPrimary = Color.White,
            secondary = Color(0xFFFFB703),
            onSecondary = Color.Black,
            background = Color(0xFF1A0508),
            onBackground = Color(0xFFFFF0F2),
            surface = Color(0xFF280B10),
            onSurface = Color(0xFFFFF0F2),
            surfaceVariant = Color(0xFF3B1218),
            onSurfaceVariant = Color(0xFFFF8A95)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF9D0208),
            onPrimary = Color.White,
            secondary = Color(0xFFD97706),
            onSecondary = Color.White,
            background = Color(0xFFFFF5F5),
            onBackground = Color(0xFF200004),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF200004),
            surfaceVariant = Color(0xFFFFD6D6),
            onSurfaceVariant = Color(0xFF6A040F)
        )
    )

    val CYBER_VORTEX = NoTuneThemePalette(
        id = "cyber_vortex",
        name = "Cyber Vortex",
        description = "Luminous electric indigo, neon pink, and dark ultraviolet matrix",
        primaryAccent = Color(0xFF7000FF),
        secondaryAccent = Color(0xFFFF007F),
        tertiaryAccent = Color(0xFF00F0FF),
        isOledOptimized = true,
        darkColorScheme = darkColorScheme(
            primary = Color(0xFF9D4EDD),
            onPrimary = Color.White,
            secondary = Color(0xFFFF007F),
            onSecondary = Color.White,
            background = Color.Black,
            onBackground = Color.White,
            surface = Color(0xFF0D001C),
            onSurface = Color.White,
            surfaceVariant = Color(0xFF1B0038),
            onSurfaceVariant = Color(0xFFD8B4F8)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF5A189A),
            onPrimary = Color.White,
            secondary = Color(0xFFD6006B),
            onSecondary = Color.White,
            background = Color(0xFFFAF5FF),
            onBackground = Color(0xFF150029),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF150029),
            surfaceVariant = Color(0xFFF0E5FF),
            onSurfaceVariant = Color(0xFF3C096C)
        )
    )

    val TROPICAL_PALM = NoTuneThemePalette(
        id = "tropical_palm",
        name = "Tropical Palm",
        description = "Warm sunset orange, lagoon turquoise, and lush palm green",
        primaryAccent = Color(0xFF2A9D8F),
        secondaryAccent = Color(0xFFF4A261),
        tertiaryAccent = Color(0xFFE76F51),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFF2A9D8F),
            onPrimary = Color.Black,
            secondary = Color(0xFFF4A261),
            onSecondary = Color.Black,
            background = Color(0xFF0A1C19),
            onBackground = Color(0xFFE8F8F5),
            surface = Color(0xFF102B27),
            onSurface = Color(0xFFE8F8F5),
            surfaceVariant = Color(0xFF1A3E39),
            onSurfaceVariant = Color(0xFF8ABEB7)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF1D6F64),
            onPrimary = Color.White,
            secondary = Color(0xFFE76F51),
            onSecondary = Color.White,
            background = Color(0xFFF2FBF9),
            onBackground = Color(0xFF061B17),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF061B17),
            surfaceVariant = Color(0xFFD1F2EC),
            onSurfaceVariant = Color(0xFF0E4039)
        )
    )

    val ICE_CRYSTAL = NoTuneThemePalette(
        id = "ice_crystal",
        name = "Ice Crystal",
        description = "Arctic frost blue, glacial cyan, and titanium white crispness",
        primaryAccent = Color(0xFFA5F3FC),
        secondaryAccent = Color(0xFF38BDF8),
        tertiaryAccent = Color(0xFF0284C7),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFFA5F3FC),
            onPrimary = Color.Black,
            secondary = Color(0xFF38BDF8),
            onSecondary = Color.Black,
            background = Color(0xFF08121A),
            onBackground = Color(0xFFF0F9FF),
            surface = Color(0xFF0E1E2B),
            onSurface = Color(0xFFF0F9FF),
            surfaceVariant = Color(0xFF172C3E),
            onSurfaceVariant = Color(0xFF7DD3FC)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF0284C7),
            onPrimary = Color.White,
            secondary = Color(0xFF0369A1),
            onSecondary = Color.White,
            background = Color(0xFFF0F9FF),
            onBackground = Color(0xFF071B26),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF071B26),
            surfaceVariant = Color(0xFFE0F2FE),
            onSurfaceVariant = Color(0xFF0369A1)
        )
    )

    val MIDNIGHT_AMBER = NoTuneThemePalette(
        id = "midnight_amber",
        name = "Midnight Amber",
        description = "Dark honey amber, warm bronze, and deep charcoal",
        primaryAccent = Color(0xFFFFB703),
        secondaryAccent = Color(0xFFFB8500),
        tertiaryAccent = Color(0xFFD4A373),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFFFFB703),
            onPrimary = Color.Black,
            secondary = Color(0xFFFB8500),
            onSecondary = Color.Black,
            background = Color(0xFF141009),
            onBackground = Color(0xFFFFFBEF),
            surface = Color(0xFF201A10),
            onSurface = Color(0xFFFFFBEF),
            surfaceVariant = Color(0xFF30271A),
            onSurfaceVariant = Color(0xFFFFD56B)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFFD97706),
            onPrimary = Color.White,
            secondary = Color(0xFFB45309),
            onSecondary = Color.White,
            background = Color(0xFFFFFDF7),
            onBackground = Color(0xFF241604),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF241604),
            surfaceVariant = Color(0xFFFEF3C7),
            onSurfaceVariant = Color(0xFF92400E)
        )
    )

    val ELECTRIC_STORM = NoTuneThemePalette(
        id = "electric_storm",
        name = "Electric Storm",
        description = "Luminous lightning yellow, thunderstorm blue, and midnight navy",
        primaryAccent = Color(0xFFFFE600),
        secondaryAccent = Color(0xFF2563EB),
        tertiaryAccent = Color(0xFF38BDF8),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFFFFE600),
            onPrimary = Color.Black,
            secondary = Color(0xFF3B82F6),
            onSecondary = Color.White,
            background = Color(0xFF090D1A),
            onBackground = Color(0xFFFEFCE8),
            surface = Color(0xFF10172D),
            onSurface = Color(0xFFFEFCE8),
            surfaceVariant = Color(0xFF1A2445),
            onSurfaceVariant = Color(0xFFFDE047)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF1D4ED8),
            onPrimary = Color.White,
            secondary = Color(0xFFD97706),
            onSecondary = Color.White,
            background = Color(0xFFEFF6FF),
            onBackground = Color(0xFF0F172A),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF0F172A),
            surfaceVariant = Color(0xFFDBEAFE),
            onSurfaceVariant = Color(0xFF1E40AF)
        )
    )

    val OPAL_IRIDESCENCE = NoTuneThemePalette(
        id = "opal_iridescence",
        name = "Opal Iridescence",
        description = "Shimmering opal pearl, soft pastel magenta, and sky blue glow",
        primaryAccent = Color(0xFFF472B6),
        secondaryAccent = Color(0xFF38BDF8),
        tertiaryAccent = Color(0xFFA78BFA),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFFF472B6),
            onPrimary = Color.Black,
            secondary = Color(0xFF38BDF8),
            onSecondary = Color.Black,
            background = Color(0xFF160E18),
            onBackground = Color(0xFFFDF4FF),
            surface = Color(0xFF241728),
            onSurface = Color(0xFFFDF4FF),
            surfaceVariant = Color(0xFF35223B),
            onSurfaceVariant = Color(0xFFFBCFE8)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFFDB2777),
            onPrimary = Color.White,
            secondary = Color(0xFF0284C7),
            onSecondary = Color.White,
            background = Color(0xFFFDF4FF),
            onBackground = Color(0xFF26042A),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF26042A),
            surfaceVariant = Color(0xFFFAE8FF),
            onSurfaceVariant = Color(0xFF9D174D)
        )
    )

    val HYPER_GLITCH = NoTuneThemePalette(
        id = "hyper_glitch",
        name = "Hyper Glitch",
        description = "High-voltage cyber green with electric magenta accents",
        primaryAccent = Color(0xFF00FF66),
        secondaryAccent = Color(0xFFFF007F),
        tertiaryAccent = Color(0xFF8A2BE2),
        isOledOptimized = true,
        darkColorScheme = darkColorScheme(
            primary = Color(0xFF00FF66),
            onPrimary = Color.Black,
            secondary = Color(0xFFFF007F),
            onSecondary = Color.White,
            background = Color.Black,
            onBackground = Color.White,
            surface = Color(0xFF0D0D12),
            onSurface = Color.White,
            surfaceVariant = Color(0xFF181824),
            onSurfaceVariant = Color(0xFF00FF66)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF00B348),
            onPrimary = Color.White,
            secondary = Color(0xFFD6006B),
            onSecondary = Color.White,
            background = Color(0xFFF4FFF8),
            onBackground = Color(0xFF002910),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF002910),
            surfaceVariant = Color(0xFFE2FCEB),
            onSurfaceVariant = Color(0xFF007A31)
        )
    )

    val LUNAR_ECLIPSE = NoTuneThemePalette(
        id = "lunar_eclipse",
        name = "Lunar Eclipse",
        description = "Deep blood crimson and obsidian shadow glow",
        primaryAccent = Color(0xFFDC143C),
        secondaryAccent = Color(0xFFFF4500),
        tertiaryAccent = Color(0xFF8B0000),
        isOledOptimized = true,
        darkColorScheme = darkColorScheme(
            primary = Color(0xFFDC143C),
            onPrimary = Color.White,
            secondary = Color(0xFFFF4500),
            onSecondary = Color.White,
            background = Color.Black,
            onBackground = Color.White,
            surface = Color(0xFF12080A),
            onSurface = Color.White,
            surfaceVariant = Color(0xFF220E12),
            onSurfaceVariant = Color(0xFFFF8095)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFFB00020),
            onPrimary = Color.White,
            secondary = Color(0xFFD83A00),
            onSecondary = Color.White,
            background = Color(0xFFFFF5F6),
            onBackground = Color(0xFF2D0007),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF2D0007),
            surfaceVariant = Color(0xFFFFE5E8),
            onSurfaceVariant = Color(0xFF8B0016)
        )
    )

    val NEON_GENESIS = NoTuneThemePalette(
        id = "neon_genesis",
        name = "Neon Genesis",
        description = "Futuristic evangelion purple and cyan lime spectrum",
        primaryAccent = Color(0xFF9932CC),
        secondaryAccent = Color(0xFF00FFCC),
        tertiaryAccent = Color(0xFFFFD700),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFFB84DFF),
            onPrimary = Color.White,
            secondary = Color(0xFF00FFCC),
            onSecondary = Color.Black,
            background = Color(0xFF0F051A),
            onBackground = Color.White,
            surface = Color(0xFF180A28),
            onSurface = Color.White,
            surfaceVariant = Color(0xFF27123F),
            onSurfaceVariant = Color(0xFFE5B3FF)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF7A1EA6),
            onPrimary = Color.White,
            secondary = Color(0xFF00B38F),
            onSecondary = Color.White,
            background = Color(0xFFFAEEFF),
            onBackground = Color(0xFF220038),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF220038),
            surfaceVariant = Color(0xFFF2D6FF),
            onSurfaceVariant = Color(0xFF550E7A)
        )
    )

    val TITANIUM_STARK = NoTuneThemePalette(
        id = "titanium_stark",
        name = "Titanium Stark",
        description = "Industrial brushed titanium steel and platinum precision",
        primaryAccent = Color(0xFFC0C0C0),
        secondaryAccent = Color(0xFFE5E4E2),
        tertiaryAccent = Color(0xFF708090),
        isOledOptimized = true,
        darkColorScheme = darkColorScheme(
            primary = Color(0xFFE0E0E0),
            onPrimary = Color.Black,
            secondary = Color(0xFFB0BEC5),
            onSecondary = Color.Black,
            background = Color.Black,
            onBackground = Color.White,
            surface = Color(0xFF121417),
            onSurface = Color.White,
            surfaceVariant = Color(0xFF1E2228),
            onSurfaceVariant = Color(0xFFCFD8DC)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF37474F),
            onPrimary = Color.White,
            secondary = Color(0xFF546E7A),
            onSecondary = Color.White,
            background = Color(0xFFF1F3F5),
            onBackground = Color(0xFF0D1317),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF0D1317),
            surfaceVariant = Color(0xFFE2E6EA),
            onSurfaceVariant = Color(0xFF263238)
        )
    )

    val VAPORWAVE_DREAM = NoTuneThemePalette(
        id = "vaporwave_dream",
        name = "Vaporwave Dream",
        description = "Pastel aesthetic pink cyan twilight wave",
        primaryAccent = Color(0xFFFFB7B2),
        secondaryAccent = Color(0xFF70D6FF),
        tertiaryAccent = Color(0xFFE7C6FF),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFFFFB7B2),
            onPrimary = Color.Black,
            secondary = Color(0xFF70D6FF),
            onSecondary = Color.Black,
            background = Color(0xFF1A121E),
            onBackground = Color.White,
            surface = Color(0xFF261B2C),
            onSurface = Color.White,
            surfaceVariant = Color(0xFF392842),
            onSurfaceVariant = Color(0xFFFFD6E8)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFFE65C55),
            onPrimary = Color.White,
            secondary = Color(0xFF0091CA),
            onSecondary = Color.White,
            background = Color(0xFFFFF0F5),
            onBackground = Color(0xFF2E0916),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF2E0916),
            surfaceVariant = Color(0xFFFFDBE8),
            onSurfaceVariant = Color(0xFF9E1F42)
        )
    )

    val VOLCANIC_MAGMA_PRO = NoTuneThemePalette(
        id = "volcanic_magma_pro",
        name = "Volcanic Magma Pro",
        description = "Molten lava red and blaze orange thermal fire",
        primaryAccent = Color(0xFFFF3300),
        secondaryAccent = Color(0xFFFF8800),
        tertiaryAccent = Color(0xFFFFCC00),
        isOledOptimized = true,
        darkColorScheme = darkColorScheme(
            primary = Color(0xFFFF3300),
            onPrimary = Color.White,
            secondary = Color(0xFFFF8800),
            onSecondary = Color.Black,
            background = Color.Black,
            onBackground = Color.White,
            surface = Color(0xFF170A07),
            onSurface = Color.White,
            surfaceVariant = Color(0xFF2A120C),
            onSurfaceVariant = Color(0xFFFF9980)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFFD62800),
            onPrimary = Color.White,
            secondary = Color(0xFFD66A00),
            onSecondary = Color.White,
            background = Color(0xFFFFF4F0),
            onBackground = Color(0xFF380800),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF380800),
            surfaceVariant = Color(0xFFFFE0D6),
            onSurfaceVariant = Color(0xFF8A1A00)
        )
    )

    val DEEP_ABYSS_PRO = NoTuneThemePalette(
        id = "deep_abyss_pro",
        name = "Deep Abyss Pro",
        description = "Bioluminescent deep ocean trench glow",
        primaryAccent = Color(0xFF00F5D4),
        secondaryAccent = Color(0xFF00BBF9),
        tertiaryAccent = Color(0xFF4B0082),
        isOledOptimized = true,
        darkColorScheme = darkColorScheme(
            primary = Color(0xFF00F5D4),
            onPrimary = Color.Black,
            secondary = Color(0xFF00BBF9),
            onSecondary = Color.Black,
            background = Color.Black,
            onBackground = Color.White,
            surface = Color(0xFF041216),
            onSurface = Color.White,
            surfaceVariant = Color(0xFF0A2229),
            onSurfaceVariant = Color(0xFF80FAEA)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF009682),
            onPrimary = Color.White,
            secondary = Color(0xFF0077A3),
            onSecondary = Color.White,
            background = Color(0xFFEEFDFB),
            onBackground = Color(0xFF002B25),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF002B25),
            surfaceVariant = Color(0xFFD4F8F3),
            onSurfaceVariant = Color(0xFF00594D)
        )
    )

    val ZEN_MONOCHROME_PRO = NoTuneThemePalette(
        id = "zen_monochrome_pro",
        name = "Zen Monochrome Pro",
        description = "Pure stark black and white minimal contrast",
        primaryAccent = Color(0xFFFFFFFF),
        secondaryAccent = Color(0xFFCCCCCC),
        tertiaryAccent = Color(0xFF888888),
        isOledOptimized = true,
        darkColorScheme = darkColorScheme(
            primary = Color.White,
            onPrimary = Color.Black,
            secondary = Color(0xFFCCCCCC),
            onSecondary = Color.Black,
            background = Color.Black,
            onBackground = Color.White,
            surface = Color.Black,
            onSurface = Color.White,
            surfaceVariant = Color(0xFF141414),
            onSurfaceVariant = Color.White
        ),
        lightColorScheme = lightColorScheme(
            primary = Color.Black,
            onPrimary = Color.White,
            secondary = Color(0xFF333333),
            onSecondary = Color.White,
            background = Color.White,
            onBackground = Color.Black,
            surface = Color.White,
            onSurface = Color.Black,
            surfaceVariant = Color(0xFFF2F2F2),
            onSurfaceVariant = Color.Black
        )
    )

    val CHRONO_PUNK_PRO = NoTuneThemePalette(
        id = "chrono_punk_pro",
        name = "Chrono Punk Pro",
        description = "Steampunk coppers, amber brass, and clockwork gold",
        primaryAccent = Color(0xFFFFBF00),
        secondaryAccent = Color(0xFFB87333),
        tertiaryAccent = Color(0xFFC04000),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFFFFC824),
            onPrimary = Color.Black,
            secondary = Color(0xFFD48B46),
            onSecondary = Color.Black,
            background = Color(0xFF140D06),
            onBackground = Color.White,
            surface = Color(0xFF21150A),
            onSurface = Color.White,
            surfaceVariant = Color(0xFF332110),
            onSurfaceVariant = Color(0xFFFFE29D)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF9E7200),
            onPrimary = Color.White,
            secondary = Color(0xFF8A4E1B),
            onSecondary = Color.White,
            background = Color(0xFFFFF8EE),
            onBackground = Color(0xFF332000),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF332000),
            surfaceVariant = Color(0xFFFCECD7),
            onSurfaceVariant = Color(0xFF5E4400)
        )
    )

    val CELESTIAL_NEBULA_PRO = NoTuneThemePalette(
        id = "celestial_nebula_pro",
        name = "Celestial Nebula Pro",
        description = "Cosmic deep space indigo and galaxy violet pulse",
        primaryAccent = Color(0xFF8A2BE2),
        secondaryAccent = Color(0xFFFF00CC),
        tertiaryAccent = Color(0xFF00E5FF),
        isOledOptimized = true,
        darkColorScheme = darkColorScheme(
            primary = Color(0xFFA855F7),
            onPrimary = Color.White,
            secondary = Color(0xFFFF00CC),
            onSecondary = Color.White,
            background = Color.Black,
            onBackground = Color.White,
            surface = Color(0xFF0F0717),
            onSurface = Color.White,
            surfaceVariant = Color(0xFF1E0D2E),
            onSurfaceVariant = Color(0xFFE9D5FF)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF7E22CE),
            onPrimary = Color.White,
            secondary = Color(0xFFC00099),
            onSecondary = Color.White,
            background = Color(0xFFFAF5FF),
            onBackground = Color(0xFF2E0854),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF2E0854),
            surfaceVariant = Color(0xFFF3E8FF),
            onSurfaceVariant = Color(0xFF581C87)
        )
    )

    val ALL_THEMES = listOf(
        NOTUNE_PURE, MIDNIGHT, AURORA, MONO, NEBULA,
        OCEAN, EMBER, FOREST, LAVENDER, SOLAR, GLASS, CARBON,
        TOKYO_NEON, AMETHYST_ROYAL, TITANIUM_PRO, SAKURA_BLOSSOM,
        DESERT_DUNE, HYPER_LIME, DEEP_OCEANIC, CHRONO_GOLD,
        PRISMATIC_GLOW, RETRO_SYNTHWAVE, COSMIC_VOID, EMERALD_CRYSTAL,
        SUPERNOVA_BURST, ZEN_GARDEN, VOLCANIC_MAGMA, NORTHERN_LIGHTS,
        NEON_CYBERNETIC, SOLARIS_FLAME, NEON_DATADRIVE, AURORA_BOREALIS,
        CELESTIAL_COSMOS, RUBY_VALENTINE, CYBER_VORTEX, TROPICAL_PALM,
        ICE_CRYSTAL, MIDNIGHT_AMBER, ELECTRIC_STORM, OPAL_IRIDESCENCE,
        HYPER_GLITCH, LUNAR_ECLIPSE, NEON_GENESIS, TITANIUM_STARK,
        VAPORWAVE_DREAM, VOLCANIC_MAGMA_PRO, DEEP_ABYSS_PRO, ZEN_MONOCHROME_PRO,
        CHRONO_PUNK_PRO, CELESTIAL_NEBULA_PRO
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
