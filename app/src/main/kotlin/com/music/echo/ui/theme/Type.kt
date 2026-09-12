
package echo.music.iad1tya.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import echo.music.iad1tya.R

val NothingFont = FontFamily(
    Font(resId = R.font.bbh_bartle_regular, weight = FontWeight.Normal)
)

val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = NothingFont,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = NothingFont,
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = NothingFont,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = NothingFont,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = NothingFont,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = NothingFont,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = NothingFont,
        fontWeight = FontWeight.Normal, 
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp 
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = NothingFont,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

val TypographyMonospace = Typography(
    displayLarge = TextStyle(fontFamily = FontFamily.Monospace),
    headlineLarge = TextStyle(fontFamily = FontFamily.Monospace),
    titleLarge = TextStyle(fontFamily = FontFamily.Monospace),
    bodyLarge = TextStyle(fontFamily = FontFamily.Monospace),
    labelLarge = TextStyle(fontFamily = FontFamily.Monospace)
)

val TypographyGeometric = Typography(
    displayLarge = TextStyle(fontFamily = FontFamily.SansSerif),
    headlineLarge = TextStyle(fontFamily = FontFamily.SansSerif),
    titleLarge = TextStyle(fontFamily = FontFamily.SansSerif),
    bodyLarge = TextStyle(fontFamily = FontFamily.SansSerif),
    labelLarge = TextStyle(fontFamily = FontFamily.SansSerif)
)

val TypographyFuturistic = Typography(
    displayLarge = TextStyle(fontFamily = FontFamily.Cursive), // Placeholder for actual futuristic font
    bodyLarge = TextStyle(fontFamily = FontFamily.Cursive)
)

val TypographyTechBold = Typography(
    displayLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.ExtraBold),
    bodyLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold)
)

val TypographyElegantThin = Typography(
    displayLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.ExtraLight),
    bodyLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Light)
)

val TypographyRetroPixel = Typography(
    displayLarge = TextStyle(fontFamily = FontFamily.Monospace),
    bodyLarge = TextStyle(fontFamily = FontFamily.Monospace)
)

val TypographyIndustrialWide = Typography(
    displayLarge = TextStyle(fontFamily = FontFamily.SansSerif, letterSpacing = 2.sp),
    bodyLarge = TextStyle(fontFamily = FontFamily.SansSerif, letterSpacing = 1.sp)
)

val TypographyMinimalist = Typography(
    displayLarge = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Light),
    bodyLarge = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal)
)

val TypographyClassicStylish = Typography(
    displayLarge = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Medium),
    bodyLarge = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Normal)
)
