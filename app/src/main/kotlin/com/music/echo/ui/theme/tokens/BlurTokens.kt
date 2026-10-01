package com.music.echo.ui.theme.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class BlurTokens(
    val none: Dp = 0.dp,
    val subtle: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val heavy: Dp = 32.dp,
    val glass: Dp = 24.dp
)
