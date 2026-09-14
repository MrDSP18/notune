package com.music.echo.notune.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import echo.music.iad1tya.constants.CardStyleVariant

@Composable
fun NoTuneSurfaceCard(
    modifier: Modifier = Modifier,
    cardStyle: CardStyleVariant = CardStyleVariant.GLASS,
    shape: Shape = RoundedCornerShape(24.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val surfaceColor = MaterialTheme.colorScheme.surface
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant

    val infiniteTransition = rememberInfiniteTransition(label = "holographic_border")
    val borderOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "borderOffset"
    )

    val clickableModifier = if (onClick != null) modifier.clickable { onClick() } else modifier

    when (cardStyle) {
        CardStyleVariant.GLASS -> {
            Box(
                modifier = clickableModifier
                    .clip(shape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                surfaceVariant.copy(alpha = 0.65f),
                                surfaceColor.copy(alpha = 0.40f)
                            )
                        )
                    )
                    .border(
                        width = 1.2.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                primaryColor.copy(alpha = 0.5f),
                                secondaryColor.copy(alpha = 0.2f),
                                Color.White.copy(alpha = 0.1f)
                            )
                        ),
                        shape = shape
                    )
                    .padding(16.dp)
            ) {
                Column { content() }
            }
        }

        CardStyleVariant.NEUMORPHIC -> {
            Box(
                modifier = clickableModifier
                    .shadow(elevation = 8.dp, shape = shape, spotColor = Color.Black.copy(alpha = 0.6f))
                    .clip(shape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                surfaceVariant.copy(alpha = 0.95f),
                                surfaceColor
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.12f),
                        shape = shape
                    )
                    .padding(16.dp)
            ) {
                Column { content() }
            }
        }

        CardStyleVariant.BORDERED -> {
            Box(
                modifier = clickableModifier
                    .clip(shape)
                    .background(surfaceColor)
                    .border(
                        width = 1.5.dp,
                        color = primaryColor.copy(alpha = 0.6f),
                        shape = shape
                    )
                    .padding(16.dp)
            ) {
                Column { content() }
            }
        }

        CardStyleVariant.SOFT -> {
            Card(
                modifier = clickableModifier,
                shape = shape,
                colors = CardDefaults.cardColors(containerColor = surfaceVariant.copy(alpha = 0.8f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) { content() }
            }
        }

        CardStyleVariant.ELEVATED -> {
            Card(
                modifier = clickableModifier,
                shape = shape,
                colors = CardDefaults.cardColors(containerColor = surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) { content() }
            }
        }

        CardStyleVariant.CLEAN -> {
            Box(
                modifier = clickableModifier
                    .clip(shape)
                    .background(surfaceVariant.copy(alpha = 0.4f))
                    .padding(16.dp)
            ) {
                Column { content() }
            }
        }
    }
}
