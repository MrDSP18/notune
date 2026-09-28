package echo.music.iad1tya.ui.adaptive

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class NotuneColors(
    val background: Color = Color(0xFF000000),
    val surface: Color = Color(0xFF0D0D0D),
    val surfaceVariant: Color = Color(0xFF171717),
    val surfaceBorder: Color = Color(0xFF262626),
    val primary: Color = Color(0xFFFF0031), // NØTUNE Red
    val onPrimary: Color = Color(0xFFFFFFFF),
    val textPrimary: Color = Color(0xFFF5F5F5),
    val textSecondary: Color = Color(0xFFA3A3A3),
    val textTertiary: Color = Color(0xFF737373),
    val accentSignal: Color = Color(0xFF00E676), // Subtle green for active state
    val accentWarning: Color = Color(0xFFFFAB00),
    val telemetryBg: Color = Color(0xFF121212),
    val telemetryGrid: Color = Color(0xFF1E1E1E)
)

@Immutable
data class NotuneSpacing(
    val xxs: Dp = 2.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 24.dp,
    val xxl: Dp = 32.dp,
    val xxxl: Dp = 48.dp
)

@Immutable
data class NotuneTouchTarget(
    val minimum: Dp = 48.dp,
    val standard: Dp = 56.dp,
    val large: Dp = 64.dp
)

val LocalNotuneColors = staticCompositionLocalOf { NotuneColors() }
val LocalNotuneSpacing = staticCompositionLocalOf { NotuneSpacing() }
val LocalNotuneTouchTarget = staticCompositionLocalOf { NotuneTouchTarget() }

object NotuneDesignSystem {
    val colors: NotuneColors
        @Composable
        get() = LocalNotuneColors.current

    val spacing: NotuneSpacing
        @Composable
        get() = LocalNotuneSpacing.current

    val touchTarget: NotuneTouchTarget
        @Composable
        get() = LocalNotuneTouchTarget.current
}
