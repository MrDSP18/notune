package com.music.echo.notune.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.music.echo.notune.ui.theme.NotuneColors
import kotlin.math.cos
import kotlin.math.sin

/**
 * States for NØTUNE AI Glyph Visualization.
 */
enum class NotuneGlyphState {
    IDLE,
    ONLINE,
    LISTENING,
    THINKING,
    SEARCHING,
    EXECUTING,
    WAITING,
    ERROR
}

/**
 * Reactive NØTUNE Glyph Canvas Widget.
 * Animates geometric ring segments, corner crosshairs, and dot-matrix pulses
 * reacting to playback, energy, volume, and AI activity.
 */
@Composable
fun NotuneGlyph(
    modifier: Modifier = Modifier,
    isPlaying: Boolean = false,
    energyLevel: Float = 0.5f,
    glyphState: NotuneGlyphState = NotuneGlyphState.ONLINE,
    content: (@Composable () -> Unit)? = null
) {
    val transition = rememberInfiniteTransition(label = "glyph_animation")

    val pulseScale by transition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isPlaying) (800 / energyLevel.coerceAtLeast(0.5f)).toInt() else 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val rotateAngle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (glyphState == NotuneGlyphState.THINKING) 1500 else 10000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotate_angle"
    )

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = minOf(size.width, size.height) / 2f - 16.dp.toPx()

            // 1. Draw outer subtle grid ticks
            val numTicks = 36
            for (i in 0 until numTicks) {
                val angle = Math.toRadians((i * 10).toDouble())
                val innerR = radius - (if (i % 3 == 0) 8.dp.toPx() else 4.dp.toPx())
                val outerR = radius
                val p1 = Offset((center.x + innerR * cos(angle)).toFloat(), (center.y + innerR * sin(angle)).toFloat())
                val p2 = Offset((center.x + outerR * cos(angle)).toFloat(), (center.y + outerR * sin(angle)).toFloat())

                val tickColor = if (i % 9 == 0) NotuneColors.NothingRed else NotuneColors.MutedGray.copy(alpha = 0.5f)
                drawLine(
                    color = tickColor,
                    start = p1,
                    end = p2,
                    strokeWidth = 1.5.dp.toPx()
                )
            }

            // 2. Draw reactive Glyph Arc Segments
            val activeColor = when (glyphState) {
                NotuneGlyphState.ERROR -> NotuneColors.NothingRed
                NotuneGlyphState.THINKING -> NotuneColors.NothingRedGlow
                NotuneGlyphState.EXECUTING -> NotuneColors.DotMatrixGreen
                NotuneGlyphState.SEARCHING -> NotuneColors.DotMatrixBlue
                else -> if (isPlaying) NotuneColors.PureWhite else NotuneColors.SecondaryGray
            }

            // Arc Segment 1: Top-Left
            drawArc(
                color = activeColor,
                startAngle = rotateAngle,
                sweepAngle = 70f,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            // Arc Segment 2: Bottom-Right
            drawArc(
                color = activeColor.copy(alpha = 0.8f),
                startAngle = rotateAngle + 180f,
                sweepAngle = 70f,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            // 3. Inner pulsing core ring
            val innerRadius = radius * 0.4f * pulseScale
            drawCircle(
                color = if (isPlaying || glyphState != NotuneGlyphState.IDLE) NotuneColors.NothingRed.copy(alpha = 0.25f) else Color.Transparent,
                radius = innerRadius,
                center = center
            )

            // Corner Crosshairs
            val crosshairLen = 12.dp.toPx()
            drawLine(NotuneColors.NothingRed, Offset(12.dp.toPx(), 12.dp.toPx()), Offset(12.dp.toPx() + crosshairLen, 12.dp.toPx()), strokeWidth = 2.dp.toPx())
            drawLine(NotuneColors.NothingRed, Offset(12.dp.toPx(), 12.dp.toPx()), Offset(12.dp.toPx(), 12.dp.toPx() + crosshairLen), strokeWidth = 2.dp.toPx())
        }

        content?.invoke()
    }
}
