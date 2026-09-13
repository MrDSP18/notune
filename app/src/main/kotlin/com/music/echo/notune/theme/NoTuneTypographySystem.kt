package com.music.echo.notune.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import echo.music.iad1tya.constants.FontFamilyStyle
import echo.music.iad1tya.ui.theme.AppTypography

object NoTuneTypographySystem {

    /**
     * Resolves the FontFamily based on selected style.
     */
    fun resolveFontFamily(style: FontFamilyStyle): FontFamily {
        return when (style) {
            FontFamilyStyle.SANS -> FontFamily.SansSerif
            FontFamilyStyle.ROUNDED -> FontFamily.Default
            FontFamilyStyle.MONO -> FontFamily.Monospace
            FontFamilyStyle.DISPLAY -> FontFamily.SansSerif
            FontFamilyStyle.EDITORIAL -> FontFamily.Serif
            FontFamilyStyle.COMPACT -> FontFamily.SansSerif
            FontFamilyStyle.SOFT -> FontFamily.Default
            FontFamilyStyle.MODERN -> FontFamily.SansSerif
            FontFamilyStyle.CLASSIC -> FontFamily.Serif
            FontFamilyStyle.TECHNICAL -> FontFamily.Monospace
            FontFamilyStyle.GEOMETRIC -> FontFamily.SansSerif
            FontFamilyStyle.SERIF -> FontFamily.Serif
            FontFamilyStyle.CONDENSED -> FontFamily.SansSerif
            FontFamilyStyle.BOLD_MINIMAL -> FontFamily.SansSerif
            FontFamilyStyle.EXPRESSIVE -> FontFamily.Cursive
        }
    }

    /**
     * Generates an adaptive Material3 Typography set configured with the user's font style and scale factor.
     */
    fun buildTypography(
        fontStyle: FontFamilyStyle = FontFamilyStyle.SANS,
        scaleFactor: Float = 1.0f
    ): Typography {
        val family = resolveFontFamily(fontStyle)
        val base = AppTypography

        fun scale(style: TextStyle): TextStyle {
            return style.copy(
                fontFamily = family,
                fontSize = (style.fontSize.value * scaleFactor).sp,
                lineHeight = if (style.lineHeight.isSp) (style.lineHeight.value * scaleFactor).sp else style.lineHeight
            )
        }

        return Typography(
            displayLarge = scale(base.displayLarge),
            displayMedium = scale(base.displayMedium),
            displaySmall = scale(base.displaySmall),
            headlineLarge = scale(base.headlineLarge),
            headlineMedium = scale(base.headlineMedium),
            headlineSmall = scale(base.headlineSmall),
            titleLarge = scale(base.titleLarge),
            titleMedium = scale(base.titleMedium),
            titleSmall = scale(base.titleSmall),
            bodyLarge = scale(base.bodyLarge),
            bodyMedium = scale(base.bodyMedium),
            bodySmall = scale(base.bodySmall),
            labelLarge = scale(base.labelLarge),
            labelMedium = scale(base.labelMedium),
            labelSmall = scale(base.labelSmall)
        )
    }
}
