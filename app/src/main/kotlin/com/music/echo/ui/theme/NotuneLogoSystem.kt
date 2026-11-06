package com.music.echo.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import echo.music.iad1tya.constants.LogoStyle

@Composable
fun NotuneLogo(
    logoStyle: LogoStyle = LogoStyle.LOGO_ORIGINAL,
    accentColor: Color = Color(0xFFFF0031),
    textColor: Color = Color.White,
    size: Dp = 32.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        when (logoStyle) {
            LogoStyle.LOGO_ORIGINAL -> {
                // Minimal NØTUNE wordmark / symbol
                Text(
                    text = "NØ",
                    color = accentColor,
                    fontSize = (size.value * 0.6f).sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            LogoStyle.LOGO_DOT_MATRIX -> {
                // Dot matrix grid
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val dotRadius = size.toPx() * 0.08f
                    val step = size.toPx() / 4f
                    for (x in 1..3) {
                        for (y in 1..3) {
                            val active = (x + y) % 2 == 0
                            drawCircle(
                                color = if (active) accentColor else textColor.copy(alpha = 0.2f),
                                radius = dotRadius,
                                center = Offset(x * step, y * step)
                            )
                        }
                    }
                }
            }

            LogoStyle.LOGO_GLITCH, LogoStyle.LOGO_CYBER -> {
                // Cyber terminal logo
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.toPx()
                    val h = size.toPx()
                    drawRect(
                        color = accentColor,
                        topLeft = Offset(w * 0.1f, h * 0.2f),
                        size = Size(w * 0.3f, h * 0.6f)
                    )
                    drawRect(
                        color = textColor,
                        topLeft = Offset(w * 0.5f, h * 0.2f),
                        size = Size(w * 0.4f, h * 0.6f),
                        style = Stroke(width = 3f)
                    )
                }
            }

            LogoStyle.LOGO_MINIMAL -> {
                // Ultra minimal slash
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.toPx()
                    val h = size.toPx()
                    drawLine(
                        color = accentColor,
                        start = Offset(w * 0.2f, h * 0.8f),
                        end = Offset(w * 0.8f, h * 0.2f),
                        strokeWidth = 6f
                    )
                }
            }

            LogoStyle.LOGO_NEON -> {
                // Equalizer logo
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.toPx()
                    val h = size.toPx()
                    val barWidth = w * 0.15f
                    val heights = listOf(0.4f, 0.8f, 0.5f, 0.9f)
                    heights.forEachIndexed { i, heightFactor ->
                        val barHeight = h * heightFactor
                        drawRect(
                            color = if (i % 2 == 0) accentColor else textColor,
                            topLeft = Offset(w * 0.1f + i * (barWidth + w * 0.08f), h - barHeight - h * 0.1f),
                            size = Size(barWidth, barHeight)
                        )
                    }
                }
            }

            LogoStyle.LOGO_OLED -> {
                // Neural waveform logo
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.toPx()
                    val h = size.toPx()
                    val path = Path().apply {
                        moveTo(0f, h * 0.5f)
                        quadraticTo(w * 0.25f, h * 0.1f, w * 0.5f, h * 0.5f)
                        quadraticTo(w * 0.75f, h * 0.9f, w, h * 0.5f)
                    }
                    drawPath(
                        path = path,
                        color = accentColor,
                        style = Stroke(width = 5f)
                    )
                }
            }

            LogoStyle.LOGO_WAVE -> {
                // Circular sound-wave logo
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.toPx() / 2f, size.toPx() / 2f)
                    val maxR = size.toPx() * 0.45f
                    drawCircle(
                        color = accentColor,
                        center = center,
                        radius = maxR,
                        style = Stroke(width = 4f)
                    )
                    drawCircle(
                        color = textColor.copy(alpha = 0.5f),
                        center = center,
                        radius = maxR * 0.65f,
                        style = Stroke(width = 3f)
                    )
                    drawCircle(
                        color = accentColor,
                        center = center,
                        radius = maxR * 0.3f
                    )
                }
            }

            LogoStyle.LOGO_SOLAR -> {
                // Vinyl inspired logo
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.toPx() / 2f, size.toPx() / 2f)
                    drawCircle(color = textColor, center = center, radius = size.toPx() * 0.45f)
                    drawCircle(color = Color.Black, center = center, radius = size.toPx() * 0.2f)
                    drawCircle(color = accentColor, center = center, radius = size.toPx() * 0.08f)
                }
            }

            LogoStyle.LOGO_CARBON -> {
                // Abstract sound identity
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.toPx()
                    val h = size.toPx()
                    val path = Path().apply {
                        moveTo(w * 0.1f, h * 0.1f)
                        lineTo(w * 0.9f, h * 0.5f)
                        lineTo(w * 0.1f, h * 0.9f)
                        close()
                    }
                    drawPath(path = path, color = accentColor)
                }
            }

            else -> {
                // Default NØ symbol
                Text(
                    text = "NØTUNE",
                    color = accentColor,
                    fontSize = (size.value * 0.35f).sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
