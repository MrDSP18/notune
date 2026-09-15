package com.music.echo.notune.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

// Crimson Vivid Accent per Stitch & Tune Music OS spec
val CrimsonVivid = Color(0xFFFF002E)
val TertiaryCyan = Color(0xFF69D6E2)
val ZincDeep = Color(0xFF09090B)
val BorderHairline = Color(0xFF27272A)
val SurfaceContainerLowest = Color(0xFF0E0E0E)

@Composable
fun SystemTelemetryHeader(
    title: String = "NØTUNE // OS v1.0",
    subtitle: String = "Home",
    modifier: Modifier = Modifier
) {
    var currentTime by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        val sdf = SimpleDateFormat("HH:mm:ss 'UTC'", Locale.US)
        while (true) {
            currentTime = sdf.format(Date())
            kotlinx.coroutines.delay(1000)
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF131313).copy(alpha = 0.9f))
            .border(1.dp, BorderHairline)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(CrimsonVivid)
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 1.2.sp
                    )
                )
                Text(
                    text = subtitle.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        color = Color.Gray,
                        letterSpacing = 1.5.sp
                    )
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(SurfaceContainerLowest)
                    .border(1.dp, BorderHairline, RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "SYS:SYNC",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        color = TertiaryCyan,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(TertiaryCyan)
            )
        }
    }
}

@Composable
fun AudioResolutionBadge(
    sampleRate: String = "AUTO",
    bitrate: String = "SRC FMT",
    format: String = "STANDARD",
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(3.dp))
                .background(SurfaceContainerLowest)
                .border(0.8.dp, BorderHairline, RoundedCornerShape(3.dp))
                .padding(horizontal = 5.dp, vertical = 2.dp)
        ) {
            Text(
                text = sampleRate,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    color = TertiaryCyan,
                    fontWeight = FontWeight.Bold
                )
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(3.dp))
                .background(CrimsonVivid)
                .padding(horizontal = 5.dp, vertical = 2.dp)
        ) {
            Text(
                text = format,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )
        }
        Text(
            text = bitrate,
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                color = Color.Gray
            )
        )
    }
}

@Composable
fun VibeRadarCard(
    modifier: Modifier = Modifier,
    stateText: String = "SPECTRUM ACTIVE",
    energyPercent: String = "DYNAMIC",
    attentionState: String = "AUTO-EQ",
    repeatRate: String = "LOW [0.12]"
) {
    var selectedModeIndex by remember { mutableIntStateOf(0) }
    val modes = listOf("FFT SPECTRUM", "BEAT DYNAMIC", "NEURAL WAVE")
    val currentMode = modes[selectedModeIndex % modes.size]

    val infiniteTransition = rememberInfiniteTransition(label = "vibe_wave")

    val bar1Height by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 0.95f,
        animationSpec = infiniteRepeatable(tween(750, easing = LinearEasing), RepeatMode.Reverse), label = "b1"
    )
    val bar2Height by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 0.15f,
        animationSpec = infiniteRepeatable(tween(600, easing = LinearEasing), RepeatMode.Reverse), label = "b2"
    )
    val bar3Height by infiniteTransition.animateFloat(
        initialValue = 0.35f, targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(1050, easing = LinearEasing), RepeatMode.Reverse), label = "b3"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ZincDeep)
            .border(1.dp, BorderHairline, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Vibe Radar // $currentMode",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(SurfaceContainerLowest)
                        .border(0.8.dp, BorderHairline, RoundedCornerShape(4.dp))
                        .clickable { selectedModeIndex++ }
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "MODE ↻",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            color = CrimsonVivid,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            // Real-Time Interactive Animated Spectrum Bars
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(SurfaceContainerLowest)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                val multiplier = when (selectedModeIndex % 3) {
                    1 -> 1.2f
                    2 -> 0.85f
                    else -> 1.0f
                }
                listOf(
                    bar1Height * multiplier,
                    bar2Height * multiplier,
                    bar3Height * multiplier,
                    bar1Height * 0.8f * multiplier,
                    bar2Height * 1.1f * multiplier,
                    bar3Height * 0.6f * multiplier,
                    bar1Height * multiplier,
                    bar2Height * multiplier,
                    bar3Height * multiplier,
                    bar2Height * 0.9f * multiplier,
                    bar1Height * 1.05f * multiplier
                ).forEach { factor ->
                    val clamped = factor.coerceIn(0.12f, 1.0f)
                    Box(
                        modifier = Modifier
                            .width(5.dp)
                            .fillMaxHeight(clamped)
                            .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                            .background(
                                if (clamped > 0.65f) CrimsonVivid else TertiaryCyan
                            )
                    )
                }
            }

            // Telemetry Grid (Procedural Audio Spectrum Attributes)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TelemetryStatBox(label = "SPECTRUM", value = energyPercent, color = Color.White, modifier = Modifier.weight(1f))
                TelemetryStatBox(label = "AUDIO DYN", value = attentionState, color = TertiaryCyan, modifier = Modifier.weight(1f))
                TelemetryStatBox(label = "REPEAT RT", value = repeatRate, color = CrimsonVivid, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun TelemetryStatBox(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(SurfaceContainerLowest)
            .border(0.8.dp, BorderHairline, RoundedCornerShape(6.dp))
            .padding(6.dp)
    ) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 8.sp,
                    color = Color.Gray
                )
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = color,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}
