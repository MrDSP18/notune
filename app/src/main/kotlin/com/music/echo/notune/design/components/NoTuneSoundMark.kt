package com.music.echo.notune.design.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.music.echo.notune.design.theme.NotuneRed
import com.music.echo.notune.design.theme.NotuneTextPrimary

enum class SoundMarkState {
    PAUSED,       // NØ
    PLAYING,      // N∿
    DOWNLOADING,  // N◌
    AI_ACTIVE,    // N✦
    ROOM_MODE,    // N◎
    RECORDING     // N◉
}

@Composable
fun NØSoundMark(
    state: SoundMarkState = SoundMarkState.PAUSED,
    modifier: Modifier = Modifier,
    textSize: TextUnit = 24.sp,
    accentColor: Color = NotuneRed,
    textColor: Color = NotuneTextPrimary
) {
    Row(
        modifier = modifier.padding(vertical = 4.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "N",
            fontSize = textSize,
            fontWeight = FontWeight.Black,
            color = textColor
        )
        Spacer(modifier = Modifier.width(2.dp))
        
        val symbolSize = (textSize.value * 0.9f).dp
        
        when (state) {
            SoundMarkState.PAUSED -> {
                Text(
                    text = "Ø",
                    fontSize = textSize,
                    fontWeight = FontWeight.Black,
                    color = accentColor
                )
            }
            SoundMarkState.PLAYING -> {
                AnimatedWaveformSymbol(size = symbolSize, color = accentColor)
            }
            SoundMarkState.DOWNLOADING -> {
                AnimatedPulseRingSymbol(size = symbolSize, color = accentColor)
            }
            SoundMarkState.AI_ACTIVE -> {
                AnimatedSparkleSymbol(size = symbolSize, color = accentColor)
            }
            SoundMarkState.ROOM_MODE -> {
                AnimatedConcentricRippleSymbol(size = symbolSize, color = accentColor)
            }
            SoundMarkState.RECORDING -> {
                AnimatedRecordingDotSymbol(size = symbolSize, color = accentColor)
            }
        }
    }
}

@Composable
private fun AnimatedWaveformSymbol(size: Dp, color: Color) {
    val transition = rememberInfiniteTransition(label = "wave")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Canvas(modifier = Modifier.size(size)) {
        val w = size.toPx()
        val h = size.toPx()
        val path = Path()
        val midY = h / 2f
        
        path.moveTo(0f, midY)
        for (x in 0..w.toInt()) {
            val normX = x / w
            val y = midY + (kotlin.math.sin((normX * 3 * kotlin.math.PI) + phase) * (h * 0.35f)).toFloat()
            path.lineTo(x.toFloat(), y)
        }
        
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 3.dp.toPx())
        )
    }
}

@Composable
private fun AnimatedPulseRingSymbol(size: Dp, color: Color) {
    val transition = rememberInfiniteTransition(label = "pulse")
    val radiusRatio by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "radius"
    )

    Canvas(modifier = Modifier.size(size)) {
        val center = Offset(size.toPx() / 2f, size.toPx() / 2f)
        drawCircle(
            color = color,
            radius = (size.toPx() / 2f) * radiusRatio,
            style = Stroke(width = 2.5.dp.toPx())
        )
    }
}

@Composable
private fun AnimatedSparkleSymbol(size: Dp, color: Color) {
    val transition = rememberInfiniteTransition(label = "sparkle")
    val alpha by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Text(
        text = "✦",
        fontSize = (size.value).sp,
        fontWeight = FontWeight.Bold,
        color = color.copy(alpha = alpha)
    )
}

@Composable
private fun AnimatedConcentricRippleSymbol(size: Dp, color: Color) {
    val transition = rememberInfiniteTransition(label = "ripple")
    val alphaInner by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alphaInner"
    )

    Canvas(modifier = Modifier.size(size)) {
        val center = Offset(size.toPx() / 2f, size.toPx() / 2f)
        val maxR = size.toPx() / 2f
        drawCircle(
            color = color,
            radius = maxR * 0.4f,
            style = Stroke(width = 2.dp.toPx())
        )
        drawCircle(
            color = color.copy(alpha = alphaInner),
            radius = maxR * 0.85f,
            style = Stroke(width = 2.dp.toPx())
        )
    }
}

@Composable
private fun AnimatedRecordingDotSymbol(size: Dp, color: Color) {
    val transition = rememberInfiniteTransition(label = "recording")
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "recordingAlpha"
    )

    Canvas(modifier = Modifier.size(size)) {
        val center = Offset(size.toPx() / 2f, size.toPx() / 2f)
        drawCircle(
            color = color.copy(alpha = alpha),
            radius = (size.toPx() / 2f) * 0.65f
        )
    }
}
