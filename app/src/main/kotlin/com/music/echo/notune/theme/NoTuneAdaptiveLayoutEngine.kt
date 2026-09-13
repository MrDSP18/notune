package com.music.echo.notune.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class WindowSizeClass {
    COMPACT,   // Phones (< 600dp)
    MEDIUM,    // Foldables / Small Tablets (600dp - 840dp)
    EXPANDED   // Large Tablets / Desktop (> 840dp)
}

@Immutable
data class AdaptiveLayoutConfig(
    val windowSizeClass: WindowSizeClass,
    val isLandscape: Boolean,
    val useNavRail: Boolean,
    val gridColumns: Int,
    val playerDualPane: Boolean,
    val textPaddingMultiplier: Float,
    val contentSpacing: Dp
)

object NoTuneAdaptiveLayoutEngine {

    @Composable
    fun rememberAdaptiveLayoutConfig(
        appLanguageCode: String = "en"
    ): AdaptiveLayoutConfig {
        val configuration = LocalConfiguration.current
        val widthDp = configuration.screenWidthDp
        val heightDp = configuration.screenHeightDp
        val isLandscape = widthDp > heightDp

        val sizeClass = when {
            widthDp < 600 -> WindowSizeClass.COMPACT
            widthDp in 600..840 -> WindowSizeClass.MEDIUM
            else -> WindowSizeClass.EXPANDED
        }

        val useNavRail = sizeClass != WindowSizeClass.COMPACT || isLandscape

        val gridColumns = when (sizeClass) {
            WindowSizeClass.COMPACT -> if (isLandscape) 3 else 2
            WindowSizeClass.MEDIUM -> if (isLandscape) 4 else 3
            WindowSizeClass.EXPANDED -> if (isLandscape) 6 else 4
        }

        val playerDualPane = sizeClass == WindowSizeClass.EXPANDED || (sizeClass == WindowSizeClass.MEDIUM && isLandscape)

        // Languages with longer script representations receive adaptive padding to prevent clipping
        val textPaddingMultiplier = when (appLanguageCode.lowercase()) {
            "ta", "hi", "te", "kn", "ml", "mr", "bn", "gu", "pa", "or" -> 1.25f
            else -> 1.0f
        }

        val contentSpacing = when (sizeClass) {
            WindowSizeClass.COMPACT -> 12.dp
            WindowSizeClass.MEDIUM -> 16.dp
            WindowSizeClass.EXPANDED -> 24.dp
        }

        return AdaptiveLayoutConfig(
            windowSizeClass = sizeClass,
            isLandscape = isLandscape,
            useNavRail = useNavRail,
            gridColumns = gridColumns,
            playerDualPane = playerDualPane,
            textPaddingMultiplier = textPaddingMultiplier,
            contentSpacing = contentSpacing
        )
    }
}
