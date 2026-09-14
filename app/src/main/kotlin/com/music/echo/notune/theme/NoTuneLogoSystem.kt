package com.music.echo.notune.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import echo.music.iad1tya.constants.LogoVariant

@Composable
fun NoTuneLogo(
    variant: LogoVariant = LogoVariant.WORDMARK,
    tint: Color = MaterialTheme.colorScheme.primary,
    secondaryTint: Color = MaterialTheme.colorScheme.secondary,
    modifier: Modifier = Modifier,
    size: Dp = 32.dp
) {
    val onSurface = MaterialTheme.colorScheme.onSurface

    when (variant) {
        LogoVariant.WORDMARK -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
            ) {
                NoTuneSymbolCanvas(tint = tint, secondaryTint = secondaryTint, size = size)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "NØTUNE",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 3.sp
                    ),
                    color = onSurface
                )
            }
        }
        LogoVariant.SYMBOL -> {
            NoTuneSymbolCanvas(tint = tint, secondaryTint = secondaryTint, size = size, modifier = modifier)
        }
        LogoVariant.MINIMAL_SYMBOL -> {
            Canvas(modifier = modifier.size(size)) {
                val center = Offset(this.size.width / 2f, this.size.height / 2f)
                val radius = this.size.minDimension / 2.3f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(tint.copy(alpha = 0.35f), Color.Transparent),
                        center = center,
                        radius = radius * 1.4f
                    ),
                    radius = radius * 1.3f
                )
                drawCircle(color = tint, radius = radius, style = Stroke(width = radius * 0.35f))
                drawLine(
                    color = tint,
                    start = Offset(center.x - radius * 0.85f, center.y + radius * 0.85f),
                    end = Offset(center.x + radius * 0.85f, center.y - radius * 0.85f),
                    strokeWidth = radius * 0.25f
                )
            }
        }
        LogoVariant.MONOGRAM -> {
            Box(contentAlignment = Alignment.Center, modifier = modifier.size(size)) {
                Text(
                    text = "NØ",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    ),
                    color = tint
                )
            }
        }
        LogoVariant.CIRCULAR -> {
            Canvas(modifier = modifier.size(size)) {
                val center = Offset(this.size.width / 2f, this.size.height / 2f)
                val radius = this.size.minDimension / 2f
                drawCircle(
                    brush = Brush.sweepGradient(
                        colors = listOf(tint, secondaryTint, tint)
                    ),
                    radius = radius * 0.75f,
                    style = Stroke(width = radius * 0.22f)
                )
                drawCircle(color = tint, radius = radius * 0.25f)
            }
        }
        LogoVariant.GEOMETRIC -> {
            Canvas(modifier = modifier.size(size)) {
                val w = this.size.width
                val h = this.size.height
                drawRoundRect(
                    brush = Brush.linearGradient(
                        colors = listOf(tint, secondaryTint)
                    ),
                    topLeft = Offset(0f, 0f),
                    size = Size(w, h),
                    cornerRadius = CornerRadius(w * 0.28f)
                )
                drawLine(
                    color = Color.Black,
                    start = Offset(w * 0.25f, h * 0.75f),
                    end = Offset(w * 0.75f, h * 0.25f),
                    strokeWidth = w * 0.16f
                )
            }
        }
        LogoVariant.GLYPH -> {
            Canvas(modifier = modifier.size(size)) {
                val w = this.size.width
                val h = this.size.height
                val path = Path().apply {
                    moveTo(w * 0.2f, h * 0.9f)
                    lineTo(w * 0.2f, h * 0.1f)
                    lineTo(w * 0.8f, h * 0.9f)
                    lineTo(w * 0.8f, h * 0.1f)
                }
                drawPath(
                    path = path,
                    brush = Brush.verticalGradient(listOf(tint, secondaryTint)),
                    style = Stroke(width = w * 0.16f)
                )
            }
        }
        LogoVariant.CYBER_PULSE -> {
            Canvas(modifier = modifier.size(size)) {
                val w = this.size.width
                val h = this.size.height
                val stroke = w * 0.12f
                drawLine(color = tint, start = Offset(w * 0.2f, h * 0.7f), end = Offset(w * 0.2f, h * 0.3f), strokeWidth = stroke)
                drawLine(color = secondaryTint, start = Offset(w * 0.4f, h * 0.9f), end = Offset(w * 0.4f, h * 0.1f), strokeWidth = stroke)
                drawLine(color = tint, start = Offset(w * 0.6f, h * 0.8f), end = Offset(w * 0.6f, h * 0.2f), strokeWidth = stroke)
                drawLine(color = secondaryTint, start = Offset(w * 0.8f, h * 0.6f), end = Offset(w * 0.8f, h * 0.4f), strokeWidth = stroke)
            }
        }
        LogoVariant.HARMONIC_WAVE -> {
            Canvas(modifier = modifier.size(size)) {
                val w = this.size.width
                val h = this.size.height
                val path = Path().apply {
                    moveTo(0f, h * 0.5f)
                    cubicTo(w * 0.25f, 0f, w * 0.25f, h, w * 0.5f, h * 0.5f)
                    cubicTo(w * 0.75f, 0f, w * 0.75f, h, w, h * 0.5f)
                }
                drawPath(path = path, brush = Brush.horizontalGradient(listOf(tint, secondaryTint)), style = Stroke(width = w * 0.14f))
            }
        }
        LogoVariant.VINYL_GROOVES -> {
            Canvas(modifier = modifier.size(size)) {
                val center = Offset(this.size.width / 2f, this.size.height / 2f)
                val r = this.size.minDimension / 2f
                drawCircle(color = tint, radius = r, style = Stroke(width = r * 0.15f))
                drawCircle(color = secondaryTint.copy(alpha = 0.7f), radius = r * 0.7f, style = Stroke(width = r * 0.1f))
                drawCircle(color = tint.copy(alpha = 0.4f), radius = r * 0.4f, style = Stroke(width = r * 0.08f))
                drawCircle(color = tint, radius = r * 0.15f)
            }
        }
        LogoVariant.INFINITE_LOOP -> {
            Canvas(modifier = modifier.size(size)) {
                val w = this.size.width
                val h = this.size.height
                val path = Path().apply {
                    moveTo(w * 0.3f, h * 0.5f)
                    cubicTo(w * 0.1f, h * 0.1f, w * 0.1f, h * 0.9f, w * 0.5f, h * 0.5f)
                    cubicTo(w * 0.9f, h * 0.1f, w * 0.9f, h * 0.9f, w * 0.7f, h * 0.5f)
                }
                drawPath(path = path, brush = Brush.linearGradient(listOf(tint, secondaryTint)), style = Stroke(width = w * 0.14f))
            }
        }
        LogoVariant.ACOUSTIC_MESH -> {
            Canvas(modifier = modifier.size(size)) {
                val center = Offset(this.size.width / 2f, this.size.height / 2f)
                val r = this.size.minDimension / 2f
                val count = 8
                for (i in 0 until count) {
                    val angle = (i * 360f / count) * (Math.PI.toFloat() / 180f)
                    val endX = center.x + r * Math.cos(angle.toDouble()).toFloat()
                    val endY = center.y + r * Math.sin(angle.toDouble()).toFloat()
                    drawLine(color = tint.copy(alpha = 0.8f), start = center, end = Offset(endX, endY), strokeWidth = r * 0.1f)
                }
                drawCircle(color = secondaryTint, radius = r * 0.25f)
            }
        }
        LogoVariant.NEON_ORBIT -> {
            Canvas(modifier = modifier.size(size)) {
                val center = Offset(this.size.width / 2f, this.size.height / 2f)
                val r = this.size.minDimension / 2f
                drawCircle(brush = Brush.sweepGradient(listOf(tint, secondaryTint, tint)), radius = r * 0.9f, style = Stroke(width = r * 0.12f))
                drawCircle(color = tint, radius = r * 0.55f, style = Stroke(width = r * 0.1f))
                drawCircle(color = secondaryTint, radius = r * 0.25f)
            }
        }
        LogoVariant.QUANTUM_WAVE -> {
            Canvas(modifier = modifier.size(size)) {
                val w = this.size.width
                val h = this.size.height
                val path1 = Path().apply {
                    moveTo(0f, h * 0.3f)
                    cubicTo(w * 0.33f, h * 0.9f, w * 0.66f, h * 0.1f, w, h * 0.7f)
                }
                val path2 = Path().apply {
                    moveTo(0f, h * 0.7f)
                    cubicTo(w * 0.33f, h * 0.1f, w * 0.66f, h * 0.9f, w, h * 0.3f)
                }
                drawPath(path1, brush = Brush.horizontalGradient(listOf(tint, secondaryTint)), style = Stroke(width = w * 0.12f))
                drawPath(path2, brush = Brush.horizontalGradient(listOf(secondaryTint, tint)), style = Stroke(width = w * 0.12f))
            }
        }
        LogoVariant.SPECTRUM_RING -> {
            Canvas(modifier = modifier.size(size)) {
                val center = Offset(this.size.width / 2f, this.size.height / 2f)
                val r = this.size.minDimension / 2f
                val rainbow = listOf(tint, secondaryTint, Color(0xFF00F5D4), Color(0xFFFF007F), tint)
                drawCircle(brush = Brush.sweepGradient(rainbow), radius = r * 0.8f, style = Stroke(width = r * 0.2f))
                drawLine(color = Color.White, start = Offset(center.x - r * 0.6f, center.y + r * 0.6f), end = Offset(center.x + r * 0.6f, center.y - r * 0.6f), strokeWidth = r * 0.15f)
            }
        }
        LogoVariant.PRISM_BEAM -> {
            Canvas(modifier = modifier.size(size)) {
                val w = this.size.width
                val h = this.size.height
                val triangle = Path().apply {
                    moveTo(w * 0.5f, h * 0.1f)
                    lineTo(w * 0.9f, h * 0.85f)
                    lineTo(w * 0.1f, h * 0.85f)
                    close()
                }
                drawPath(triangle, brush = Brush.verticalGradient(listOf(tint, secondaryTint)), style = Stroke(width = w * 0.12f))
                drawLine(color = secondaryTint, start = Offset(w * 0.5f, h * 0.1f), end = Offset(w * 0.95f, h * 0.5f), strokeWidth = w * 0.1f)
            }
        }
        LogoVariant.FLUID_ORB -> {
            Canvas(modifier = modifier.size(size)) {
                val center = Offset(this.size.width / 2f, this.size.height / 2f)
                val r = this.size.minDimension / 2f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(tint, secondaryTint, Color.Transparent),
                        center = center,
                        radius = r
                    ),
                    radius = r * 0.95f
                )
                drawCircle(color = Color.White.copy(alpha = 0.8f), radius = r * 0.25f, center = Offset(center.x - r * 0.2f, center.y - r * 0.2f))
            }
        }
        LogoVariant.HYPER_CUBE -> {
            Canvas(modifier = modifier.size(size)) {
                val w = this.size.width
                val h = this.size.height
                val outerBox = Path().apply {
                    moveTo(w * 0.5f, 0f)
                    lineTo(w, h * 0.25f)
                    lineTo(w, h * 0.75f)
                    lineTo(w * 0.5f, h)
                    lineTo(0f, h * 0.75f)
                    lineTo(0f, h * 0.25f)
                    close()
                }
                drawPath(outerBox, brush = Brush.linearGradient(listOf(tint, secondaryTint)), style = Stroke(width = w * 0.1f))
                drawLine(color = tint, start = Offset(w * 0.5f, 0f), end = Offset(w * 0.5f, h * 0.5f), strokeWidth = w * 0.08f)
                drawLine(color = secondaryTint, start = Offset(0f, h * 0.25f), end = Offset(w * 0.5f, h * 0.5f), strokeWidth = w * 0.08f)
                drawLine(color = tint, start = Offset(w, h * 0.25f), end = Offset(w * 0.5f, h * 0.5f), strokeWidth = w * 0.08f)
            }
        }
        LogoVariant.SYNTH_GRID -> {
            Canvas(modifier = modifier.size(size)) {
                val w = this.size.width
                val h = this.size.height
                val gridColor = tint.copy(alpha = 0.6f)
                for (i in 1..3) {
                    val x = w * (i / 4f)
                    drawLine(color = gridColor, start = Offset(x, 0f), end = Offset(x, h), strokeWidth = w * 0.06f)
                }
                for (i in 1..3) {
                    val y = h * (i / 4f)
                    drawLine(color = gridColor, start = Offset(0f, y), end = Offset(w, y), strokeWidth = h * 0.06f)
                }
                drawCircle(color = secondaryTint, radius = w * 0.25f, center = Offset(w * 0.5f, h * 0.5f))
            }
        }
        LogoVariant.AURORA_PULSE -> {
            Canvas(modifier = modifier.size(size)) {
                val center = Offset(this.size.width / 2f, this.size.height / 2f)
                val r = this.size.minDimension / 2f
                val colors = listOf(tint, secondaryTint, Color(0xFF00FFCC), tint)
                drawCircle(brush = Brush.sweepGradient(colors, center), radius = r * 0.9f, style = Stroke(width = r * 0.25f))
                drawCircle(color = Color.White, radius = r * 0.2f, center = center)
            }
        }
        LogoVariant.CYBER_HEX -> {
            Canvas(modifier = modifier.size(size)) {
                val w = this.size.width
                val h = this.size.height
                val hex = Path().apply {
                    moveTo(w * 0.5f, 0f)
                    lineTo(w * 0.93f, h * 0.25f)
                    lineTo(w * 0.93f, h * 0.75f)
                    lineTo(w * 0.5f, h)
                    lineTo(w * 0.07f, h * 0.75f)
                    lineTo(w * 0.07f, h * 0.25f)
                    close()
                }
                drawPath(hex, brush = Brush.linearGradient(listOf(tint, secondaryTint)), style = Stroke(width = w * 0.12f))
                drawCircle(color = tint, radius = w * 0.2f, center = Offset(w * 0.5f, h * 0.5f))
            }
        }
        LogoVariant.NEBULA_CORE -> {
            Canvas(modifier = modifier.size(size)) {
                val center = Offset(this.size.width / 2f, this.size.height / 2f)
                val r = this.size.minDimension / 2f
                drawCircle(
                    brush = Brush.radialGradient(listOf(tint, secondaryTint, Color.Transparent), center = center, radius = r),
                    radius = r
                )
                drawCircle(color = Color.White, radius = r * 0.18f, center = center)
            }
        }
        LogoVariant.INFINITY_FLAME -> {
            Canvas(modifier = modifier.size(size)) {
                val w = this.size.width
                val h = this.size.height
                val flame = Path().apply {
                    moveTo(w * 0.5f, 0f)
                    cubicTo(w * 0.9f, h * 0.4f, w * 0.9f, h * 0.9f, w * 0.5f, h)
                    cubicTo(w * 0.1f, h * 0.9f, w * 0.1f, h * 0.4f, w * 0.5f, 0f)
                }
                drawPath(flame, brush = Brush.verticalGradient(listOf(tint, secondaryTint)))
            }
        }
        LogoVariant.ECHO_RINGS -> {
            Canvas(modifier = modifier.size(size)) {
                val center = Offset(this.size.width / 2f, this.size.height / 2f)
                val r = this.size.minDimension / 2f
                drawCircle(color = tint.copy(alpha = 0.3f), radius = r, style = Stroke(width = r * 0.12f))
                drawCircle(color = tint.copy(alpha = 0.6f), radius = r * 0.7f, style = Stroke(width = r * 0.12f))
                drawCircle(color = secondaryTint, radius = r * 0.4f, style = Stroke(width = r * 0.12f))
                drawCircle(color = tint, radius = r * 0.15f)
            }
        }
        LogoVariant.CELESTIAL_STAR -> {
            Canvas(modifier = modifier.size(size)) {
                val w = this.size.width
                val h = this.size.height
                val center = Offset(w / 2f, h / 2f)
                val star = Path().apply {
                    moveTo(center.x, 0f)
                    lineTo(center.x + w * 0.15f, center.y - h * 0.15f)
                    lineTo(w, center.y)
                    lineTo(center.x + w * 0.15f, center.y + h * 0.15f)
                    lineTo(center.x, h)
                    lineTo(center.x - w * 0.15f, center.y + h * 0.15f)
                    lineTo(0f, center.y)
                    lineTo(center.x - w * 0.15f, center.y - h * 0.15f)
                    close()
                }
                drawPath(star, brush = Brush.linearGradient(listOf(tint, secondaryTint)))
            }
        }
        LogoVariant.CHRONO_DIAL -> {
            Canvas(modifier = modifier.size(size)) {
                val center = Offset(this.size.width / 2f, this.size.height / 2f)
                val r = this.size.minDimension / 2f
                drawCircle(color = tint, radius = r, style = Stroke(width = r * 0.14f))
                drawLine(color = secondaryTint, start = center, end = Offset(center.x, center.y - r * 0.7f), strokeWidth = r * 0.12f)
                drawLine(color = tint, start = center, end = Offset(center.x + r * 0.5f, center.y), strokeWidth = r * 0.12f)
            }
        }
        LogoVariant.PLASMA_BLAST -> {
            Canvas(modifier = modifier.size(size)) {
                val center = Offset(this.size.width / 2f, this.size.height / 2f)
                val r = this.size.minDimension / 2f
                drawCircle(
                    brush = Brush.radialGradient(listOf(Color(0xFFE91E63), tint, secondaryTint, Color.Transparent), center = center, radius = r),
                    radius = r
                )
            }
        }
        LogoVariant.HOLOGRAM_MATRIX -> {
            Canvas(modifier = modifier.size(size)) {
                val w = this.size.width
                val h = this.size.height
                val count = 4
                for (i in 0 until count) {
                    val y = h * (i / (count - 1).toFloat())
                    drawLine(
                        brush = Brush.horizontalGradient(listOf(tint.copy(alpha = 0.2f), secondaryTint, tint.copy(alpha = 0.2f))),
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = h * 0.1f
                    )
                }
            }
        }
        LogoVariant.SOLAR_ECLIPSE -> {
            Canvas(modifier = modifier.size(size)) {
                val center = Offset(this.size.width / 2f, this.size.height / 2f)
                val r = this.size.minDimension / 2f
                drawCircle(
                    brush = Brush.radialGradient(listOf(tint, secondaryTint, Color.Transparent), center = center, radius = r),
                    radius = r
                )
                drawCircle(color = Color(0xFF0F0F14), radius = r * 0.75f, center = Offset(center.x - r * 0.15f, center.y - r * 0.15f))
            }
        }
        LogoVariant.VORTEX_FLOW -> {
            Canvas(modifier = modifier.size(size)) {
                val center = Offset(this.size.width / 2f, this.size.height / 2f)
                val r = this.size.minDimension / 2f
                drawCircle(
                    brush = Brush.sweepGradient(listOf(tint, secondaryTint, Color(0xFF00E5FF), tint), center),
                    radius = r,
                    style = Stroke(width = r * 0.3f)
                )
                drawCircle(color = tint, radius = r * 0.2f)
            }
        }
    }
}

@Composable
private fun NoTuneSymbolCanvas(
    tint: Color,
    secondaryTint: Color = MaterialTheme.colorScheme.secondary,
    size: Dp,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(size)) {
        val center = Offset(this.size.width / 2f, this.size.height / 2f)
        val radius = this.size.minDimension / 2.2f

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(tint.copy(alpha = 0.35f), Color.Transparent),
                center = center,
                radius = radius * 1.5f
            ),
            radius = radius * 1.4f
        )

        drawCircle(
            brush = Brush.linearGradient(
                colors = listOf(tint, secondaryTint),
                start = Offset(0f, 0f),
                end = Offset(this.size.width, this.size.height)
            ),
            radius = radius,
            style = Stroke(width = radius * 0.28f)
        )

        drawLine(
            brush = Brush.linearGradient(
                colors = listOf(tint, secondaryTint)
            ),
            start = Offset(center.x - radius * 0.95f, center.y + radius * 0.95f),
            end = Offset(center.x + radius * 0.95f, center.y - radius * 0.95f),
            strokeWidth = radius * 0.22f
        )

        drawCircle(
            color = tint,
            radius = radius * 0.18f,
            center = Offset(center.x - radius * 0.35f, center.y - radius * 0.35f)
        )
        drawCircle(
            color = secondaryTint,
            radius = radius * 0.18f,
            center = Offset(center.x + radius * 0.35f, center.y + radius * 0.35f)
        )
    }
}

@Composable
fun NoTuneLogo(
    style: echo.music.iad1tya.constants.LogoStyle,
    tint: Color = MaterialTheme.colorScheme.primary,
    secondaryTint: Color = MaterialTheme.colorScheme.secondary,
    modifier: Modifier = Modifier,
    size: Dp = 32.dp
) {
    val variant = when (style) {
        echo.music.iad1tya.constants.LogoStyle.LOGO_ORIGINAL -> LogoVariant.WORDMARK
        echo.music.iad1tya.constants.LogoStyle.LOGO_DOT_MATRIX -> LogoVariant.SYMBOL
        echo.music.iad1tya.constants.LogoStyle.LOGO_GLITCH -> LogoVariant.CYBER_PULSE
        echo.music.iad1tya.constants.LogoStyle.LOGO_MINIMAL -> LogoVariant.MINIMAL_SYMBOL
        echo.music.iad1tya.constants.LogoStyle.LOGO_NEON -> LogoVariant.NEON_ORBIT
        echo.music.iad1tya.constants.LogoStyle.LOGO_OLED -> LogoVariant.GLYPH
        echo.music.iad1tya.constants.LogoStyle.LOGO_CYBER -> LogoVariant.CYBER_HEX
        echo.music.iad1tya.constants.LogoStyle.LOGO_WAVE -> LogoVariant.QUANTUM_WAVE
        echo.music.iad1tya.constants.LogoStyle.LOGO_SOLAR -> LogoVariant.SPECTRUM_RING
        echo.music.iad1tya.constants.LogoStyle.LOGO_CARBON -> LogoVariant.SYNTH_GRID
        echo.music.iad1tya.constants.LogoStyle.LOGO_TITANIUM -> LogoVariant.HYPER_CUBE
        echo.music.iad1tya.constants.LogoStyle.LOGO_ABSTRACT -> LogoVariant.ACOUSTIC_MESH
    }
    NoTuneLogo(variant = variant, tint = tint, secondaryTint = secondaryTint, modifier = modifier, size = size)
}


