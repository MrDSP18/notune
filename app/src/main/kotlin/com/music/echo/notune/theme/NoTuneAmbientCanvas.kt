package com.music.echo.notune.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun NoTuneAmbientCanvas(
    modifier: Modifier = Modifier,
    isPlaying: Boolean = true,
    accentColor: Color = MaterialTheme.colorScheme.primary,
    secondaryColor: Color = MaterialTheme.colorScheme.secondary,
    tertiaryColor: Color = MaterialTheme.colorScheme.tertiary
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ambient_canvas_transition")

    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isPlaying) 8000 else 20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val scalePulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scalePulse"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // 1. Fluid Ambient Gradient Orbs
        val orb1Center = Offset(
            x = width * (0.3f + 0.2f * sin(phase)),
            y = height * (0.25f + 0.15f * cos(phase * 0.7f))
        )
        val orb2Center = Offset(
            x = width * (0.7f + 0.2f * cos(phase * 0.8f)),
            y = height * (0.65f + 0.2f * sin(phase * 0.9f))
        )
        val orb3Center = Offset(
            x = width * (0.5f + 0.3f * sin(phase * 1.2f)),
            y = height * (0.85f + 0.1f * cos(phase * 1.1f))
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(accentColor.copy(alpha = 0.35f), Color.Transparent),
                center = orb1Center,
                radius = width * 0.6f * scalePulse
            ),
            center = orb1Center,
            radius = width * 0.6f * scalePulse
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(secondaryColor.copy(alpha = 0.30f), Color.Transparent),
                center = orb2Center,
                radius = width * 0.55f * (2f - scalePulse)
            ),
            center = orb2Center,
            radius = width * 0.55f * (2f - scalePulse)
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(tertiaryColor.copy(alpha = 0.25f), Color.Transparent),
                center = orb3Center,
                radius = width * 0.50f * scalePulse
            ),
            center = orb3Center,
            radius = width * 0.50f * scalePulse
        )

        // 2. Bioluminescent Particle Constellation
        val particleCount = 24
        for (i in 0 until particleCount) {
            val pAngle = phase + i * (2 * Math.PI / particleCount).toFloat()
            val radiusX = width * (0.15f + 0.35f * (i % 5) / 5f)
            val radiusY = height * (0.15f + 0.35f * (i % 7) / 7f)

            val px = width / 2f + radiusX * cos(pAngle)
            val py = height / 2f + radiusY * sin(pAngle * 1.3f)
            val particleAlpha = 0.2f + 0.6f * (0.5f + 0.5f * sin(pAngle * 2f))

            drawCircle(
                color = if (i % 2 == 0) accentColor.copy(alpha = particleAlpha) else secondaryColor.copy(alpha = particleAlpha),
                radius = (3 + (i % 4) * 2).dp.toPx(),
                center = Offset(px.toFloat(), py.toFloat())
            )
        }

        // 3. Dynamic Sine Wave Spectrum Overlay
        val path = Path()
        val wavePoints = 60
        val waveHeight = 28.dp.toPx() * (if (isPlaying) 1f else 0.3f)
        val centerY = height * 0.92f

        path.moveTo(0f, centerY)
        for (i in 0..wavePoints) {
            val x = (width / wavePoints) * i
            val normX = x / width
            val angle = phase * 2f + normX * 4 * Math.PI
            val y = centerY + sin(angle).toFloat() * waveHeight * sin(normX * Math.PI).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.lineTo(width, height)
        path.lineTo(0f, height)
        path.close()

        drawPath(
            path = path,
            brush = Brush.verticalGradient(
                colors = listOf(accentColor.copy(alpha = 0.20f), Color.Transparent),
                startY = centerY - waveHeight,
                endY = height
            )
        )

        drawPath(
            path = path,
            color = accentColor.copy(alpha = 0.45f),
            style = Stroke(width = 2.dp.toPx())
        )
    }
}
