package com.music.echo.notune.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Technical typographic system for NØTUNE.
 * Combines monospace/dot-matrix technical styles for telemetry, numbers, and status
 * with clean sans-serif styles for track titles and body readability.
 */
object NotuneTypography {
    val DotMatrixHeader = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        letterSpacing = 2.sp,
        color = NotuneColors.PureWhite
    )

    val DotMatrixLarge = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        letterSpacing = 1.5.sp,
        color = NotuneColors.PureWhite
    )

    val DotMatrixMedium = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 1.sp,
        color = NotuneColors.SecondaryGray
    )

    val DotMatrixSmall = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        letterSpacing = 0.8.sp,
        color = NotuneColors.MutedGray
    )

    val TechnicalHeader = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        letterSpacing = 1.sp,
        color = NotuneColors.PureWhite
    )

    val TrackTitle = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        letterSpacing = 0.3.sp,
        color = NotuneColors.PureWhite
    )

    val ArtistName = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        letterSpacing = 0.2.sp,
        color = NotuneColors.SecondaryGray
    )

    val TerminalPrompt = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        letterSpacing = 0.5.sp,
        color = NotuneColors.NothingRedGlow
    )
}
