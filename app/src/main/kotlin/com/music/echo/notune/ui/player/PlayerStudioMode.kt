package com.music.echo.notune.ui.player

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import echo.music.iad1tya.ui.theme.NothingFont

@Composable
fun PlayerStudioMode(
    songTitle: String = "Husn",
    codecInfo: String = "FLAC • 24-bit / 96 kHz • 2304 kbps",
    onClose: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var eq60Hz by remember { mutableFloatStateOf(0.4f) }
    var eq250Hz by remember { mutableFloatStateOf(0.6f) }
    var eq1kHz by remember { mutableFloatStateOf(0.5f) }
    var eq4kHz by remember { mutableFloatStateOf(0.7f) }
    var eq8kHz by remember { mutableFloatStateOf(0.8f) }

    var bassBoost by remember { mutableFloatStateOf(0.65f) }
    var trebleBoost by remember { mutableFloatStateOf(0.45f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF09090B))
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "NØTUNE STUDIO // AUDIOPHILE PANEL",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = NothingFont,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF27272A)
                ) {
                    Text(
                        text = "HI-RES AUDIO",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont),
                        color = Color(0xFF10B981),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            // Real-time Spectrum Graphic
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "SPECTRUM ANALYZER",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont),
                        color = Color(0xFF71717A)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val barWidth = size.width / 16f
                        val heights = listOf(0.3f, 0.5f, 0.8f, 0.6f, 0.9f, 0.4f, 0.7f, 0.85f, 0.5f, 0.65f, 0.95f, 0.4f, 0.6f, 0.3f, 0.75f, 0.5f)
                        heights.forEachIndexed { index, h ->
                            drawRect(
                                color = if (index % 2 == 0) Color(0xFF38BDF8) else Color(0xFF818CF8),
                                topLeft = Offset(index * barWidth + 2f, size.height * (1f - h)),
                                size = androidx.compose.ui.geometry.Size(barWidth - 4f, size.height * h)
                            )
                        }
                    }
                }
            }

            Text(
                text = codecInfo,
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = NothingFont),
                color = Color(0xFFA1A1AA)
            )

            // 5-Band Parametric Equalizer
            Column {
                Text(
                    text = "5-BAND EQUALIZER",
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont, letterSpacing = 1.sp),
                    color = Color.White
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    EqBandColumn("60Hz", eq60Hz) { eq60Hz = it }
                    EqBandColumn("250Hz", eq250Hz) { eq250Hz = it }
                    EqBandColumn("1kHz", eq1kHz) { eq1kHz = it }
                    EqBandColumn("4kHz", eq4kHz) { eq4kHz = it }
                    EqBandColumn("8kHz", eq8kHz) { eq8kHz = it }
                }
            }

            // DSP Boost Controls
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Bass Boost", style = MaterialTheme.typography.bodySmall, color = Color.White)
                    Text(text = "${(bassBoost * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
                Slider(value = bassBoost, onValueChange = { bassBoost = it })

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Treble Clarity", style = MaterialTheme.typography.bodySmall, color = Color.White)
                    Text(text = "${(trebleBoost * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
                Slider(value = trebleBoost, onValueChange = { trebleBoost = it })
            }

            Button(
                onClick = onClose,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "CLOSE STUDIO PANEL", style = MaterialTheme.typography.labelMedium.copy(fontFamily = NothingFont))
            }
        }
    }
}

@Composable
private fun EqBandColumn(label: String, value: Float, onValueChange: (Float) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color(0xFFA1A1AA))
        Slider(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .height(100.dp)
                .width(36.dp)
        )
    }
}
