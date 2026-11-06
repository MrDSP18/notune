package com.music.echo.ui.theme.tokens

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.runtime.Immutable

@Immutable
data class MotionTokens(
    val instantMs: Int = 0,
    val fastMs: Int = 150,
    val mediumMs: Int = 300,
    val slowMs: Int = 500,
    val transitionMs: Int = 350,
    val easingStandard: Easing = FastOutSlowInEasing,
    val easingDecelerate: Easing = LinearOutSlowInEasing
)
