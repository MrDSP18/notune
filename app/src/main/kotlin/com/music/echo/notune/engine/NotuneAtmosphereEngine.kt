package com.music.echo.notune.engine

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

data class ArtworkPalette(
    val dominant: Color = Color(0xFF18181B),
    val vibrant: Color = Color(0xFF3F3F46),
    val muted: Color = Color(0xFF27272A),
    val darkVibrant: Color = Color(0xFF09090B),
    val textPrimary: Color = Color(0xFFFAFAFA),
    val textSecondary: Color = Color(0xFFA1A1AA)
)

object NotuneAtmosphereEngine {

    fun generateArtworkGradient(
        palette: ArtworkPalette,
        isOledMode: Boolean = false,
        isDarkTheme: Boolean = true
    ): Brush {
        if (isOledMode) {
            return Brush.verticalGradient(
                colors = listOf(
                    palette.dominant.copy(alpha = 0.3f),
                    Color.Black,
                    Color.Black
                )
            )
        }

        return if (isDarkTheme) {
            Brush.verticalGradient(
                colors = listOf(
                    palette.vibrant.copy(alpha = 0.5f),
                    palette.dominant.copy(alpha = 0.85f),
                    palette.darkVibrant.copy(alpha = 0.98f),
                    Color(0xFF09090B)
                )
            )
        } else {
            Brush.verticalGradient(
                colors = listOf(
                    palette.vibrant.copy(alpha = 0.25f),
                    palette.muted.copy(alpha = 0.15f),
                    Color(0xFFFAFAFA)
                )
            )
        }
    }

    fun getGlassSurfaceColor(
        baseColor: Color = Color(0xFF27272A),
        alpha: Float = 0.65f,
        isOledMode: Boolean = false
    ): Color {
        return if (isOledMode) {
            Color(0xFF09090B).copy(alpha = 0.9f)
        } else {
            baseColor.copy(alpha = alpha)
        }
    }
}
