
package echo.music.iad1tya.ui.theme

import android.graphics.Bitmap
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.rememberDynamicColorScheme
import com.materialkolor.score.Score
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

    val colorScheme = when (themePreset) {
        ThemePreset.NOTHING -> {
            rememberDynamicColorScheme(
                seedColor = Color.White,
                isDark = true,
                style = PaletteStyle.Monochrome
            ).copy(
                primary = NothingRed,
                onPrimary = Color.White,
                background = Color.Black,
                surface = Color.Black,
                surfaceVariant = Color.White.copy(alpha = 0.05f),
                outline = Color.White.copy(alpha = 0.1f)
            )
        }
        ThemePreset.CYBERPUNK -> {
            rememberDynamicColorScheme(
                seedColor = Color(0xFFFDE100), // Cyberpunk Yellow
                isDark = true,
                style = PaletteStyle.Vibrant
            ).copy(
                primary = Color(0xFF00FF9F), // Neon Green
                secondary = Color(0xFFFDE100),
                tertiary = Color(0xFF00B3FF),
                background = Color(0xFF0D0221)
            )
        }
        ThemePreset.NEON -> {
            rememberDynamicColorScheme(
                seedColor = Color(0xFFFF00FF),
                isDark = true,
                style = PaletteStyle.Expressive
            ).copy(
                primary = Color(0xFF00FFFF),
                background = Color(0xFF050505)
            )
        }
        ThemePreset.SYNTHWAVE -> {
            rememberDynamicColorScheme(
                seedColor = Color(0xFFFF71CE),
                isDark = true,
                style = PaletteStyle.TonalSpot
            ).copy(
                primary = Color(0xFF01CDFE),
                secondary = Color(0xFF05FFA1),
                background = Color(0xFF241734)
            )
        }
        ThemePreset.RETRO -> {
            rememberDynamicColorScheme(
                seedColor = Color(0xFFE94560),
                isDark = true,
                style = PaletteStyle.Content
            ).copy(
                background = Color(0xFF1A1A2E),
                surface = Color(0xFF16213E)
            )
        }
        ThemePreset.MATRIX -> {
            rememberDynamicColorScheme(
                seedColor = Color(0xFF00FF41),
                isDark = true,
                style = PaletteStyle.Monochrome
            ).copy(
                primary = Color(0xFF00FF41),
                background = Color.Black,
                surface = Color.Black
            )
        }
        ThemePreset.AMOLED_BLACK -> {
            rememberDynamicColorScheme(
                seedColor = customAccentColor,
                isDark = true,
                style = PaletteStyle.TonalSpot
            ).pureBlack(true)
        }
        ThemePreset.MINIMAL_WHITE -> {
            rememberDynamicColorScheme(
                seedColor = Color.Black,
                isDark = false,
                style = PaletteStyle.Monochrome
            )
        }
        ThemePreset.VAPORWAVE -> {
            rememberDynamicColorScheme(
                seedColor = Color(0xFFFF71CE),
                isDark = true,
                style = PaletteStyle.Vibrant
            ).copy(
                primary = Color(0xFF01CDFE),
                background = Color(0xFF241734)
            )
        }
        ThemePreset.NORD -> {
            rememberDynamicColorScheme(
                seedColor = Color(0xFF88C0D0),
                isDark = true,
                style = PaletteStyle.TonalSpot
            ).copy(
                background = Color(0xFF2E3440),
                surface = Color(0xFF3B4252)
            )
        }
        ThemePreset.GLASS -> {
            rememberDynamicColorScheme(
                seedColor = Color.White,
                isDark = true,
                style = PaletteStyle.Monochrome
            ).copy(
                background = Color.Transparent,
                surface = Color.White.copy(alpha = 0.1f)
            )
        }
        else -> {
            rememberDynamicColorScheme(
                seedColor = customAccentColor,
                isDark = darkTheme,
                style = PaletteStyle.TonalSpot
            ).let { if (pureBlack && darkTheme) it.pureBlack(true) else it }
        }
    }

    val (blurIntensity) = rememberPreference(echo.music.iad1tya.constants.BlurIntensityKey, 12f)
    val (glassIntensity) = rememberPreference(echo.music.iad1tya.constants.GlassIntensityKey, 0.05f)

    val typography = when (typographyStyle) {
        TypographyStyle.NOTHING_DOT_MATRIX -> AppTypography // Already set to NothingFont in Type.kt
        TypographyStyle.MONOSPACE -> TypographyMonospace
        TypographyStyle.GEOMETRIC -> TypographyGeometric
        else -> AppTypography
    }

    val shapes = if (themePreset == ThemePreset.NOTHING || themePreset == ThemePreset.RETRO) {
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
