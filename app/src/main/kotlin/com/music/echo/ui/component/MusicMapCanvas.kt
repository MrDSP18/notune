package com.music.echo.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MusicMapCanvas(
    genres: List<String> = listOf("Pop", "Rock", "Indie", "Hip-Hop", "Electronic", "Jazz"),
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF080808))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.width.coerceAtMost(size.height) * 0.35f

            // Draw genre connection constellation lines
            genres.indices.forEach { index ->
                val angle = (2 * Math.PI / genres.size) * index
                val x = center.x + radius * Math.cos(angle).toFloat()
                val y = center.y + radius * Math.sin(angle).toFloat()

                drawLine(
                    color = Color(0xFF333333),
                    start = center,
                    end = Offset(x, y),
                    strokeWidth = 2f
                )

                drawCircle(
                    color = Color(0xFFFF0055),
                    radius = 16f,
                    center = Offset(x, y)
                )
            }

            drawCircle(
                color = Color.White,
                radius = 24f,
                center = center
            )
        }

        Text(
            text = "NØTUNE MUSIC MAP",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 32.dp)
        )
    }
}
