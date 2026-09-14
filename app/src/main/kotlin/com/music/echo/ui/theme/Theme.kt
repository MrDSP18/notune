
package echo.music.iad1tya.ui.theme

import android.graphics.Bitmap
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import com.materialkolor.score.Score
import com.music.echo.ui.theme.ThemeEngine
import echo.music.iad1tya.constants.AccentColorKey
import echo.music.iad1tya.constants.ThemePreset
import echo.music.iad1tya.constants.ThemePresetKey
import echo.music.iad1tya.constants.TypographyStyle
import echo.music.iad1tya.constants.TypographyStyleKey
import echo.music.iad1tya.utils.rememberPreference
import echo.music.iad1tya.utils.rememberEnumPreference

val DefaultThemeColor = Color(0xFFFFFFFF)
val NothingRed = Color(0xFFFF0031)

@Composable
fun notuneTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    pureBlack: Boolean = false,
    content: @Composable () -> Unit,
) {
    val themePreset by rememberEnumPreference(ThemePresetKey, ThemePreset.NOTHING)
    val typographyStyle by rememberEnumPreference(TypographyStyleKey, TypographyStyle.NOTHING_DOT_MATRIX)
    val customAccentColorInt by rememberPreference(AccentColorKey, NothingRed.toArgb())
    val customAccentColor = Color(customAccentColorInt)

    val config = ThemeEngine.getThemeConfig(themePreset, customAccentColor)

    val colorScheme = darkColorScheme(
        primary = config.primary,
        onPrimary = config.onPrimary,
        background = if (pureBlack) Color.Black else config.background,
        surface = if (pureBlack) Color.Black else config.surface,
        surfaceVariant = config.surfaceVariant,
        outline = config.outline,
        onBackground = Color.White,
        onSurface = Color.White,
        surfaceContainer = if (pureBlack) Color.Black else config.surface,
        surfaceContainerHigh = if (pureBlack) Color.Black else config.surface,
        surfaceContainerLow = if (pureBlack) Color.Black else config.surface
    )

    val typography = when (typographyStyle) {
        TypographyStyle.NOTHING_DOT_MATRIX -> AppTypography
        TypographyStyle.MONOSPACE -> TypographyMonospace
        TypographyStyle.GEOMETRIC -> TypographyGeometric
        else -> AppTypography
    }

    val shapes = if (themePreset == ThemePreset.NOTHING || themePreset == ThemePreset.NOTUNE_PURE) {
        Shapes(
            extraSmall = RoundedCornerShape(0.dp),
            small = RoundedCornerShape(2.dp),
            medium = RoundedCornerShape(4.dp),
            large = RoundedCornerShape(8.dp),
            extraLarge = RoundedCornerShape(12.dp)
        )
    } else {
        MaterialTheme.shapes
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = shapes,
        content = content
    )
}

fun Bitmap.extractThemeColor(): Color {
    val colorsToPopulation = Palette.from(this)
        .maximumColorCount(8)
        .generate()
        .swatches
        .associate { it.rgb to it.population }
    val rankedColors = Score.score(colorsToPopulation)
    return Color(rankedColors.first())
}

fun Bitmap.extractGradientColors(): List<Color> {
    val extractedColors = Palette.from(this)
        .maximumColorCount(64)
        .generate()
        .swatches
        .associate { it.rgb to it.population }

    val orderedColors = Score.score(extractedColors, 2, 0xff4285f4.toInt(), true)
        .sortedByDescending { Color(it).luminance() }

    return if (orderedColors.size >= 2)
        listOf(Color(orderedColors[0]), Color(orderedColors[1]))
    else
        listOf(Color(0xFF595959), Color(0xFF0D0D0D))
}

fun ColorScheme.pureBlack(apply: Boolean) =
    if (apply) copy(
        surface = Color.Black,
        background = Color.Black,
        surfaceContainer = Color.Black,
        surfaceContainerHigh = Color.Black,
        surfaceContainerLow = Color.Black,
        surfaceContainerLowest = Color.Black,
        surfaceContainerHighest = Color.Black
    ) else this

val ColorSaver = object : Saver<Color, Int> {
    override fun restore(value: Int): Color = Color(value)
    override fun SaverScope.save(value: Color): Int = value.toArgb()
}

