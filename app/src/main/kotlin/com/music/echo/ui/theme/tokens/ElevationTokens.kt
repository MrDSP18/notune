package com.music.echo.ui.theme.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class ElevationTokens(
    val none: Dp = 0.dp,
    val flat: Dp = 0.dp,
    val subtle: Dp = 2.dp,
    val medium: Dp = 6.dp,
    val high: Dp = 12.dp,
    val float: Dp = 20.dp
)
