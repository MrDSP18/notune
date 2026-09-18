package com.music.echo.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

enum class CinemaTheme(val label: String, val bgColors: List<Color>, val accentColor: Color) {
    MINIMAL("Minimal", listOf(Color(0xFF111111), Color(0xFF222222)), Color(0xFFFFFFFF)),
    CYBER("Cyber", listOf(Color(0xFF0D0221), Color(0xFF00F0FF)), Color(0xFFFF007F)),
    NEON("Neon", listOf(Color(0xFF120024), Color(0xFF490066)), Color(0xFF00FFCC)),
    SPACE("Space", listOf(Color(0xFF050515), Color(0xFF151535)), Color(0xFF9D4EDD)),
    DARK("Dark", listOf(Color(0xFF050505), Color(0xFF181818)), Color(0xFF888888)),
    RETRO("Retro", listOf(Color(0xFF2B1055), Color(0xFF7597DE)), Color(0xFFFF9E00)),
    DREAM("Dream", listOf(Color(0xFF1A0B2E), Color(0xFF3B154C)), Color(0xFFFF70A6))
}

@Composable
fun MusicCinemaVisualizer(
    playbackState: echo.music.iad1tya.models.PlaybackState,
    modifier: Modifier = Modifier
) {
    var currentTheme by remember { mutableStateOf(CinemaTheme.CYBER) }
    val isPlaying = playbackState.isPlaying
    
    val visualizerEngine = remember { echo.music.iad1tya.notune.audio.ProceduralVisualizerEngine() }
    val bars = visualizerEngine.rememberWaveformFrame(playbackState)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(currentTheme.bgColors)
            )
    ) {
        // Animated Waveform Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            val barWidth = width / bars.size
            bars.forEachIndexed { i, bar ->
                val barHeight = height * bar.heightFactor
                drawRect(
                    color = currentTheme.accentColor.copy(alpha = bar.opacity),
                    topLeft = Offset(i * barWidth + 4f, height / 2 - barHeight / 2),
                    size = androidx.compose.ui.geometry.Size(barWidth - 8f, barHeight)
                )
            }
        }

        // Truthful Labeling
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "VISUALIZER: PROCEDURAL",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Theme Selector Header
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "NØTUNE CINEMA",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                CinemaTheme.values().forEach { theme ->
                    val isSelected = theme == currentTheme
                    Text(
                        text = theme.label,
                        color = if (isSelected) currentTheme.accentColor else Color.Gray,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .clickable { currentTheme = theme }
                    )
                }
            }
        }
    }
}
