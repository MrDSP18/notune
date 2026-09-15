package com.music.echo.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import echo.music.iad1tya.constants.ThemePreset

data class NotuneThemeConfig(
    val primary: Color,
    val onPrimary: Color,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val outline: Color,
    val isDark: Boolean = true,
    val name: String
)

object ThemeEngine {
    fun getThemeConfig(preset: ThemePreset, customAccent: Color = Color(0xFFFF0031)): NotuneThemeConfig {
        return when (preset) {
            ThemePreset.NOTHING, ThemePreset.NOTUNE_PURE -> NotuneThemeConfig(
                primary = Color(0xFFFF002E),
                onPrimary = Color.White,
                background = Color(0xFF131313),
                surface = Color(0xFF09090B),
                surfaceVariant = Color(0xFF18181B),
                outline = Color(0xFF27272A),
                name = "NØTUNE STITCH & TUNE"
            )
            ThemePreset.MIDNIGHT -> NotuneThemeConfig(
                primary = Color(0xFF6366F1),
                onPrimary = Color.White,
                background = Color(0xFF030712),
                surface = Color(0xFF0F172A),
                surfaceVariant = Color.White.copy(alpha = 0.06f),
                outline = Color(0xFF334155),
                name = "MIDNIGHT"
            )
            ThemePreset.AURORA -> NotuneThemeConfig(
                primary = Color(0xFF48CAE4),
                onPrimary = Color.Black,
                background = Color(0xFF0B132B),
                surface = Color(0xFF1C2541),
                surfaceVariant = Color(0xFF06D6A0).copy(alpha = 0.1f),
                outline = Color(0xFF48CAE4).copy(alpha = 0.3f),
                name = "AURORA"
            )
            ThemePreset.MONO -> NotuneThemeConfig(
                primary = Color.White,
                onPrimary = Color.Black,
                background = Color.Black,
                surface = Color(0xFF121212),
                surfaceVariant = Color.White.copy(alpha = 0.08f),
                outline = Color.White.copy(alpha = 0.2f),
                name = "MONO"
            )
            ThemePreset.NEBULA -> NotuneThemeConfig(
                primary = Color(0xFFFF007F),
                onPrimary = Color.White,
                background = Color(0xFF0D0221),
                surface = Color(0xFF190B38),
                surfaceVariant = Color(0xFF7B2CBF).copy(alpha = 0.15f),
                outline = Color(0xFFFF007F).copy(alpha = 0.4f),
                name = "NEBULA"
            )
            ThemePreset.OCEAN -> NotuneThemeConfig(
                primary = Color(0xFF00F5D4),
                onPrimary = Color.Black,
                background = Color(0xFF020914),
                surface = Color(0xFF0A192F),
                surfaceVariant = Color(0xFF00F5D4).copy(alpha = 0.08f),
                outline = Color(0xFF00F5D4).copy(alpha = 0.25f),
                name = "OCEAN"
            )
            ThemePreset.EMBER -> NotuneThemeConfig(
                primary = Color(0xFFFF3B00),
                onPrimary = Color.White,
                background = Color(0xFF0A0404),
                surface = Color(0xFF190C0C),
                surfaceVariant = Color(0xFFFF3B00).copy(alpha = 0.12f),
                outline = Color(0xFFFF3B00).copy(alpha = 0.35f),
                name = "EMBER"
            )
            ThemePreset.FOREST -> NotuneThemeConfig(
                primary = Color(0xFF10B981),
                onPrimary = Color.Black,
                background = Color(0xFF05100A),
                surface = Color(0xFF0D2116),
                surfaceVariant = Color(0xFF10B981).copy(alpha = 0.1f),
                outline = Color(0xFF10B981).copy(alpha = 0.3f),
                name = "FOREST"
            )
            ThemePreset.LAVENDER -> NotuneThemeConfig(
                primary = Color(0xFFC084FC),
                onPrimary = Color.Black,
                background = Color(0xFF0F081D),
                surface = Color(0xFF1E1035),
                surfaceVariant = Color(0xFFC084FC).copy(alpha = 0.12f),
                outline = Color(0xFFC084FC).copy(alpha = 0.35f),
                name = "LAVENDER"
            )
            ThemePreset.SOLAR -> NotuneThemeConfig(
                primary = Color(0xFFF59E0B),
                onPrimary = Color.Black,
                background = Color(0xFF0C0A00),
                surface = Color(0xFF1F1A05),
                surfaceVariant = Color(0xFFF59E0B).copy(alpha = 0.12f),
                outline = Color(0xFFF59E0B).copy(alpha = 0.35f),
                name = "SOLAR"
            )
            ThemePreset.GLASS -> NotuneThemeConfig(
                primary = Color(0xFF38BDF8),
                onPrimary = Color.Black,
                background = Color(0xFF050B14),
                surface = Color(0xFF0F172A).copy(alpha = 0.7f),
                surfaceVariant = Color.White.copy(alpha = 0.1f),
                outline = Color.White.copy(alpha = 0.25f),
                name = "GLASS"
            )
            ThemePreset.CARBON -> NotuneThemeConfig(
                primary = Color(0xFFDC2626),
                onPrimary = Color.White,
                background = Color(0xFF080808),
                surface = Color(0xFF141414),
                surfaceVariant = Color.White.copy(alpha = 0.06f),
                outline = Color(0xFF383838),
                name = "CARBON"
            )
            ThemePreset.TOKYO_NEON, ThemePreset.CYBERPUNK -> NotuneThemeConfig(
                primary = Color(0xFF00F0FF),
                onPrimary = Color.Black,
                background = Color(0xFF090014),
                surface = Color(0xFF18002E),
                surfaceVariant = Color(0xFFFF0055).copy(alpha = 0.15f),
                outline = Color(0xFF00F0FF).copy(alpha = 0.4f),
                name = "TOKYO NEON"
            )
            ThemePreset.AMETHYST_ROYAL -> NotuneThemeConfig(
                primary = Color(0xFFA855F7),
                onPrimary = Color.White,
                background = Color(0xFF120327),
                surface = Color(0xFF230948),
                surfaceVariant = Color(0xFFA855F7).copy(alpha = 0.12f),
                outline = Color(0xFFA855F7).copy(alpha = 0.35f),
                name = "AMETHYST ROYAL"
            )
            ThemePreset.TITANIUM_PRO -> NotuneThemeConfig(
                primary = Color(0xFF3B82F6),
                onPrimary = Color.White,
                background = Color(0xFF121316),
                surface = Color(0xFF1F2128),
                surfaceVariant = Color.White.copy(alpha = 0.08f),
                outline = Color(0xFF4B5563),
                name = "TITANIUM PRO"
            )
            ThemePreset.SAKURA_BLOSSOM -> NotuneThemeConfig(
                primary = Color(0xFFF472B6),
                onPrimary = Color.Black,
                background = Color(0xFF14080E),
                surface = Color(0xFF28111D),
                surfaceVariant = Color(0xFFF472B6).copy(alpha = 0.12f),
                outline = Color(0xFFF472B6).copy(alpha = 0.35f),
                name = "SAKURA BLOSSOM"
            )
            ThemePreset.DESERT_DUNE -> NotuneThemeConfig(
                primary = Color(0xFFD97706),
                onPrimary = Color.Black,
                background = Color(0xFF140D07),
                surface = Color(0xFF26190E),
                surfaceVariant = Color(0xFFD97706).copy(alpha = 0.12f),
                outline = Color(0xFFD97706).copy(alpha = 0.35f),
                name = "DESERT DUNE"
            )
            ThemePreset.HYPER_LIME -> NotuneThemeConfig(
                primary = Color(0xFF84CC16),
                onPrimary = Color.Black,
                background = Color(0xFF000000),
                surface = Color(0xFF0E1602),
                surfaceVariant = Color(0xFF84CC16).copy(alpha = 0.15f),
                outline = Color(0xFF84CC16).copy(alpha = 0.4f),
                name = "HYPER LIME"
            )
            ThemePreset.DEEP_OCEANIC -> NotuneThemeConfig(
                primary = Color(0xFF14B8A6),
                onPrimary = Color.Black,
                background = Color(0xFF010C1E),
                surface = Color(0xFF051C3D),
                surfaceVariant = Color(0xFF14B8A6).copy(alpha = 0.12f),
                outline = Color(0xFF14B8A6).copy(alpha = 0.35f),
                name = "DEEP OCEANIC"
            )
            ThemePreset.CHRONO_GOLD -> NotuneThemeConfig(
                primary = Color(0xFFEAB308),
                onPrimary = Color.Black,
                background = Color(0xFF060606),
                surface = Color(0xFF191604),
                surfaceVariant = Color(0xFFEAB308).copy(alpha = 0.12f),
                outline = Color(0xFFEAB308).copy(alpha = 0.35f),
                name = "CHRONO GOLD"
            )
            else -> NotuneThemeConfig(
                primary = customAccent,
                onPrimary = Color.White,
                background = Color.Black,
                surface = Color(0xFF101010),
                surfaceVariant = Color.White.copy(alpha = 0.05f),
                outline = Color.White.copy(alpha = 0.12f),
                name = "CUSTOM"
            )
        }
    }
}
