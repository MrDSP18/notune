package com.music.echo.notune.audio

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.music.echo.notune.theme.NoTuneAmbientCanvas
import com.music.echo.notune.theme.NoTuneSurfaceCard
import echo.music.iad1tya.constants.CardStyleVariant
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun Spatial3dAudioStudio(
    onBackClick: () -> Unit = {}
) {
    var azimuthAngle by remember { mutableStateOf(45f) }
    var distanceRadius by remember { mutableStateOf(0.7f) }
    var elevationAngle by remember { mutableStateOf(0f) }
    var is8dOrbitEnabled by remember { mutableStateOf(false) }
    var orbitSpeed by remember { mutableStateOf(1.0f) }
    var roomReverb by remember { mutableStateOf(0.35f) }

    val infiniteTransition = rememberInfiniteTransition(label = "8d_orbit")
    val orbitPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = (6000 / orbitSpeed).toInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbitPhase"
    )

    val currentAzimuth = if (is8dOrbitEnabled) (azimuthAngle + orbitPhase) % 360f else azimuthAngle

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        NoTuneAmbientCanvas(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                IconButton(onClick = onBackClick) {
                    Text("←", fontSize = 24.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "3D BINAURAL SOUNDSTAGE",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.5.sp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Spatial Audio & 8D Panner Studio",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                // Interactive 360 Soundstage Canvas
                NoTuneSurfaceCard(
                    cardStyle = CardStyleVariant.GLASS,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "DRAG SOUND SOURCE IN 360° SPACE",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.Black.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(Unit) {
                                    detectDragGestures { change, _ ->
                                        val center = Offset(size.width / 2f, size.height / 2f)
                                        val touch = change.position
                                        val dx = touch.x - center.x
                                        val dy = touch.y - center.y
                                        val rad = atan2(dy, dx)
                                        azimuthAngle = (Math.toDegrees(rad.toDouble()).toFloat() + 360f) % 360f
                                        val dist = (Math.hypot(dx.toDouble(), dy.toDouble()) / (size.width / 2f)).toFloat()
                                        distanceRadius = dist.coerceIn(0.1f, 0.95f)
                                    }
                                }
                        ) {
                            val centerX = size.width / 2f
                            val centerY = size.height / 2f
                            val maxR = size.width.coerceAtMost(size.height) * 0.42f

                            // Orbit circles
                            drawCircle(color = Color.White.copy(alpha = 0.08f), radius = maxR * 0.33f, style = Stroke(1.dp.toPx()))
                            drawCircle(color = Color.White.copy(alpha = 0.12f), radius = maxR * 0.66f, style = Stroke(1.dp.toPx()))
                            drawCircle(color = Color.White.copy(alpha = 0.18f), radius = maxR, style = Stroke(1.5.dp.toPx()))

                            // Listener Head Symbol
                            drawCircle(
                                brush = Brush.radialGradient(listOf(Color(0xFF00E5FF), Color(0xFF0077A3))),
                                radius = 16.dp.toPx(),
                                center = Offset(centerX, centerY)
                            )

                            // Sound Source Position
                            val rad = Math.toRadians(currentAzimuth.toDouble())
                            val sourceR = maxR * distanceRadius
                            val sourceX = centerX + (sourceR * cos(rad)).toFloat()
                            val sourceY = centerY + (sourceR * sin(rad)).toFloat()

                            // Sound propagation waves
                            drawCircle(
                                color = Color(0xFFFF0031).copy(alpha = 0.25f),
                                radius = 32.dp.toPx(),
                                center = Offset(sourceX, sourceY)
                            )

                            drawCircle(
                                brush = Brush.radialGradient(listOf(Color(0xFFFF0031), Color(0xFFFF8800))),
                                radius = 14.dp.toPx(),
                                center = Offset(sourceX, sourceY)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Azimuth: ${currentAzimuth.toInt()}° • Distance: ${(distanceRadius * 100).toInt()}%",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Controls Card
                NoTuneSurfaceCard(
                    cardStyle = CardStyleVariant.GLASS,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text("8D Orbital Auto-Pan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("360° continuous rotation", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = is8dOrbitEnabled,
                            onCheckedChange = { is8dOrbitEnabled = it }
                        )
                    }

                    if (is8dOrbitEnabled) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Orbit Speed: ${String.format("%.1f", orbitSpeed)}x", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        Slider(
                            value = orbitSpeed,
                            onValueChange = { orbitSpeed = it },
                            valueRange = 0.2f..3.0f
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Acoustic Room Reverb", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Slider(
                        value = roomReverb,
                        onValueChange = { roomReverb = it },
                        valueRange = 0.0f..1.0f
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Vertical Elevation: ${elevationAngle.toInt()}°", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Slider(
                        value = elevationAngle,
                        onValueChange = { elevationAngle = it },
                        valueRange = -90f..90f
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
