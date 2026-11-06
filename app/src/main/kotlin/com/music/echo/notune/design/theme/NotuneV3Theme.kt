package com.music.echo.notune.design.theme

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils

// NØTUNE V3 Color Tokens
val NotuneRed = Color(0xFFFF0031)
val NotuneDeepViolet = Color(0xFF7C3AED)
val NotuneElectricCyan = Color(0xFF06B6D4)
val NotuneDarkUniverse = Color(0xFF090A0F)
val NotuneSurfaceDark = Color(0xFF12141D)
val NotuneSurfaceVariantDark = Color(0xFF1A1D2A)
val NotuneGlassBorder = Color(0x33FFFFFF)
val NotuneTextPrimary = Color(0xFFF3F4F6)
val NotuneTextSecondary = Color(0xFF9CA3AF)
val NotuneTextMuted = Color(0xFF6B7280)

@Immutable
data class AmbientPalette(
    val dominant: Color = NotuneRed,
    val secondary: Color = NotuneDeepViolet,
    val backgroundGlow: Color = NotuneDarkUniverse,
    val isDark: Boolean = true
)

val LocalAmbientPalette = staticCompositionLocalOf { AmbientPalette() }

object ArtworkColorExtractor {
    fun extractPalette(primaryColorHex: String?, fallbackColor: Color = NotuneRed): AmbientPalette {
        if (primaryColorHex.isNullOrEmpty()) return AmbientPalette(dominant = fallbackColor)
        return try {
            val parsed = Color(android.graphics.Color.parseColor(primaryColorHex))
            val hsl = FloatArray(3)
            ColorUtils.colorToHSL(parsed.toArgb(), hsl)
            
            // Generate ambient secondary hue shifted by 40 deg
            val secondaryHsl = hsl.clone()
            secondaryHsl[0] = (secondaryHsl[0] + 40f) % 360f
            val secondaryColor = Color(ColorUtils.HSLToColor(secondaryHsl))
            
            // Darkened glow
            val darkHsl = hsl.clone()
            darkHsl[2] = (darkHsl[2] * 0.15f).coerceIn(0.05f, 0.2f)
            val glowColor = Color(ColorUtils.HSLToColor(darkHsl))
            
            AmbientPalette(
                dominant = parsed,
                secondary = secondaryColor,
                backgroundGlow = glowColor
            )
        } catch (e: Exception) {
            AmbientPalette(dominant = fallbackColor)
        }
    }
}

private val V3DarkColorScheme = darkColorScheme(
    primary = NotuneRed,
    secondary = NotuneDeepViolet,
    tertiary = NotuneElectricCyan,
    background = NotuneDarkUniverse,
    surface = NotuneSurfaceDark,
    surfaceVariant = NotuneSurfaceVariantDark,
    onBackground = NotuneTextPrimary,
    onSurface = NotuneTextPrimary,
    onSurfaceVariant = NotuneTextSecondary
)

object NotuneV3Motion {
    val SpringSnappy = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMediumLow
    )
    val SpringSmooth = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessLow
    )
}

@Composable
fun NotuneV3Theme(
    ambientPalette: AmbientPalette = AmbientPalette(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalAmbientPalette provides ambientPalette) {
        MaterialTheme(
            colorScheme = V3DarkColorScheme,
            content = content
        )
    }
}
