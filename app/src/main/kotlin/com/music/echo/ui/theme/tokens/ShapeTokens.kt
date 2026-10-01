package com.music.echo.ui.theme.tokens

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class ShapeTokens(
    val none: Shape = RectangleShape,
    val extraSmall: Shape = RoundedCornerShape(2.dp),
    val small: Shape = RoundedCornerShape(6.dp),
    val medium: Shape = RoundedCornerShape(12.dp),
    val large: Shape = RoundedCornerShape(20.dp),
    val extraLarge: Shape = RoundedCornerShape(28.dp),
    val full: Shape = CircleShape,
    val cardShape: Shape = RoundedCornerShape(12.dp),
    val artworkCornerRadius: Dp = 12.dp
)
