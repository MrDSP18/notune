package com.music.echo.notune.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun NoTuneLogo(
    size: Dp = 48.dp,
    color: Color = Color.White,
    accentColor: Color = Color(0xFFFF0031),
    variant: echo.music.iad1tya.constants.LogoVariant = echo.music.iad1tya.constants.LogoVariant.SYMBOL
) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        
        // Define key points for the stylized 'N'
        val p1 = Offset(w * 0.30f, h * 0.75f)
        val p2 = Offset(w * 0.30f, h * 0.25f)
        val p3 = Offset(w * 0.70f, h * 0.75f)
        val p4 = Offset(w * 0.70f, h * 0.25f)

        // Draw segmented 'N'
        val strokeW = w * 0.08f
        
        // Left Vertical
        drawLine(color, p1, p2, strokeW, cap = StrokeCap.Butt)
        // Connecting Diagonal
        drawLine(color, p2, p3, strokeW, cap = StrokeCap.Butt)
        // Right Vertical
        drawLine(color, p3, p4, strokeW, cap = StrokeCap.Butt)

        // The 'Ø' Slash - Signature technical element
        drawLine(
            color = accentColor,
            start = Offset(w * 0.20f, h * 0.65f),
            end = Offset(w * 0.80f, h * 0.35f),
            strokeWidth = w * 0.12f,
            cap = StrokeCap.Square
        )
        
        // Technical Orbit / Aiming Brackets
        val bSize = w * 0.18f
        val bStroke = w * 0.03f
        
        // Top-Left bracket
        drawLine(accentColor, Offset(w*0.1f, w*0.1f + bSize), Offset(w*0.1f, w*0.1f), bStroke)
        drawLine(accentColor, Offset(w*0.1f, w*0.1f), Offset(w*0.1f + bSize, w*0.1f), bStroke)
        
        // Bottom-Right bracket
        drawLine(accentColor, Offset(w*0.9f, h*0.9f - bSize), Offset(w*0.9f, h*0.9f), bStroke)
        drawLine(accentColor, Offset(w*0.9f, h*0.9f), Offset(w*0.9f - bSize, h*0.9f), bStroke)
        
        // Pulse dots
        drawCircle(color = accentColor, radius = w * 0.04f, center = Offset(w * 0.5f, h * 0.5f))
    }
}
