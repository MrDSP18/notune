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
                NoTuneSymbolCanvas(tint = tint, size = size)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "NØTUNE",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    ),
                    color = onSurface
                )
            }
        }
        LogoVariant.SYMBOL -> {
            NoTuneSymbolCanvas(tint = tint, size = size, modifier = modifier)
        }
        LogoVariant.MINIMAL_SYMBOL -> {
            Canvas(modifier = modifier.size(size)) {
                val center = Offset(this.size.width / 2f, this.size.height / 2f)
                val radius = this.size.minDimension / 2.5f
                drawCircle(color = tint, radius = radius, style = Stroke(width = radius * 0.35f))
                drawLine(
                    color = tint,
                    start = Offset(center.x - radius * 0.8f, center.y + radius * 0.8f),
                    end = Offset(center.x + radius * 0.8f, center.y - radius * 0.8f),
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
                drawCircle(color = tint.copy(alpha = 0.15f), radius = radius)
                drawCircle(color = tint, radius = radius * 0.65f, style = Stroke(width = radius * 0.25f))
                drawCircle(color = tint, radius = radius * 0.2f)
            }
        }
        LogoVariant.GEOMETRIC -> {
            Canvas(modifier = modifier.size(size)) {
                val w = this.size.width
                val h = this.size.height
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(0f, 0f),
                    size = Size(w, h),
                    cornerRadius = CornerRadius(w * 0.25f)
                )
                drawLine(
                    color = Color.Black,
                    start = Offset(w * 0.25f, h * 0.75f),
                    end = Offset(w * 0.75f, h * 0.25f),
                    strokeWidth = w * 0.15f
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
                drawPath(path = path, color = tint, style = Stroke(width = w * 0.16f))
            }
        }
    }
}

@Composable
private fun NoTuneSymbolCanvas(
    tint: Color,
    size: Dp,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(size)) {
        val center = Offset(this.size.width / 2f, this.size.height / 2f)
        val radius = this.size.minDimension / 2.2f

        drawCircle(
            color = tint,
            radius = radius,
            style = Stroke(width = radius * 0.3f)
        )
        drawLine(
            color = tint,
            start = Offset(center.x - radius * 0.9f, center.y + radius * 0.9f),
            end = Offset(center.x + radius * 0.9f, center.y - radius * 0.9f),
            strokeWidth = radius * 0.22f
        )
    }
}
