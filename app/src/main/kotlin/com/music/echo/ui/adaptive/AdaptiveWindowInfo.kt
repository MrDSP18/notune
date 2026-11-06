package echo.music.iad1tya.ui.adaptive

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class WindowWidthClass {
    COMPACT, // < 600dp (standard phones portrait)
    MEDIUM,  // 600dp - 839dp (foldables, landscape phones, small tablets)
    EXPANDED // >= 840dp (large tablets, foldables unfolded, desktop/docked)
}

enum class WindowHeightClass {
    COMPACT, // < 480dp (landscape phones)
    MEDIUM,  // 480dp - 899dp (standard screens)
    EXPANDED // >= 900dp (tall phones, large tablets)
}

enum class WindowOrientation {
    PORTRAIT,
    LANDSCAPE
}

data class AdaptiveWindowInfo(
    val widthClass: WindowWidthClass,
    val heightClass: WindowHeightClass,
    val orientation: WindowOrientation,
    val screenWidthDp: Dp,
    val screenHeightDp: Dp,
    val fontScale: Float,
    val density: Float
) {
    val isCompactPhone: Boolean
        get() = widthClass == WindowWidthClass.COMPACT && orientation == WindowOrientation.PORTRAIT

    val isTablet: Boolean
        get() = widthClass == WindowWidthClass.EXPANDED || (widthClass == WindowWidthClass.MEDIUM && orientation == WindowOrientation.LANDSCAPE)

    val isFoldable: Boolean
        get() = widthClass == WindowWidthClass.MEDIUM

    val isLandscape: Boolean
        get() = orientation == WindowOrientation.LANDSCAPE

    val useBottomNavigation: Boolean
        get() = widthClass == WindowWidthClass.COMPACT

    val useNavigationRail: Boolean
        get() = widthClass == WindowWidthClass.MEDIUM

    val useThreeColumnLayout: Boolean
        get() = widthClass == WindowWidthClass.EXPANDED
}

@Composable
fun rememberAdaptiveWindowInfo(
    availableWidthDp: Dp? = null,
    availableHeightDp: Dp? = null
): AdaptiveWindowInfo {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    val screenWidth = availableWidthDp ?: configuration.screenWidthDp.dp
    val screenHeight = availableHeightDp ?: configuration.screenHeightDp.dp
    val fontScale = configuration.fontScale

    val widthClass = when {
        screenWidth < 600.dp -> WindowWidthClass.COMPACT
        screenWidth < 840.dp -> WindowWidthClass.MEDIUM
        else -> WindowWidthClass.EXPANDED
    }

    val heightClass = when {
        screenHeight < 480.dp -> WindowHeightClass.COMPACT
        screenHeight < 900.dp -> WindowHeightClass.MEDIUM
        else -> WindowHeightClass.EXPANDED
    }

    val orientation = if (screenWidth > screenHeight) {
        WindowOrientation.LANDSCAPE
    } else {
        WindowOrientation.PORTRAIT
    }

    return remember(screenWidth, screenHeight, fontScale, density.density) {
        AdaptiveWindowInfo(
            widthClass = widthClass,
            heightClass = heightClass,
            orientation = orientation,
            screenWidthDp = screenWidth,
            screenHeightDp = screenHeight,
            fontScale = fontScale,
            density = density.density
        )
    }
}
