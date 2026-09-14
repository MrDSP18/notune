package com.music.echo.notune.audio

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.music.echo.notune.theme.NoTuneAmbientCanvas
import com.music.echo.notune.theme.NoTuneSurfaceCard
import echo.music.iad1tya.constants.CardStyleVariant

@Composable
fun VocalSeparatorStudio(
    onBackClick: () -> Unit = {}
) {
    var vocalGain by remember { mutableStateOf(1.0f) }
    var drumsGain by remember { mutableStateOf(1.0f) }
    var bassGain by remember { mutableStateOf(1.0f) }
    var melodyGain by remember { mutableStateOf(1.0f) }

    var vocalSolo by remember { mutableStateOf(false) }
    var pitchShiftSemi by remember { mutableStateOf(0) }

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
                        text = "STEM ISOLATION STUDIO",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.5.sp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Real-Time Vocal & Instrument Separator",
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
                // Quick Karaoke Mode Presets
                NoTuneSurfaceCard(
                    cardStyle = CardStyleVariant.GLASS,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "EXPRESS STEM PRESETS",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                vocalGain = 0.0f
                                drumsGain = 1.0f
                                bassGain = 1.0f
                                melodyGain = 1.0f
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("🎤 Karaoke")
                        }

                        Button(
                            onClick = {
                                vocalGain = 1.0f
                                drumsGain = 0.0f
                                bassGain = 0.0f
                                melodyGain = 0.0f
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("🗣️ Acapella")
                        }

                        Button(
                            onClick = {
                                vocalGain = 1.0f
                                drumsGain = 1.0f
                                bassGain = 1.0f
                                melodyGain = 1.0f
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("🔄 Reset")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stem Sliders Card
                NoTuneSurfaceCard(
                    cardStyle = CardStyleVariant.GLASS,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 1. Vocals
                    StemSliderItem(
                        icon = "🎤",
                        name = "Lead Vocals",
                        gain = vocalGain,
                        onGainChange = { vocalGain = it },
                        color = Color(0xFFFF0055)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 2. Drums
                    StemSliderItem(
                        icon = "🥁",
                        name = "Drums & Percussion",
                        gain = drumsGain,
                        onGainChange = { drumsGain = it },
                        color = Color(0xFFFF8800)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3. Bass
                    StemSliderItem(
                        icon = "🎸",
                        name = "Bass Sub-Line",
                        gain = bassGain,
                        onGainChange = { bassGain = it },
                        color = Color(0xFF00E5FF)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 4. Instruments / Melody
                    StemSliderItem(
                        icon = "🎹",
                        name = "Melody & Synth",
                        gain = melodyGain,
                        onGainChange = { melodyGain = it },
                        color = Color(0xFFA855F7)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Pitch Shift Transposer Card
                NoTuneSurfaceCard(
                    cardStyle = CardStyleVariant.GLASS,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "VOICE PITCH TRANSPOSER",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Semitones: ${if (pitchShiftSemi > 0) "+$pitchShiftSemi" else pitchShiftSemi}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black
                    )
                    Slider(
                        value = pitchShiftSemi.toFloat(),
                        onValueChange = { pitchShiftSemi = it.toInt() },
                        valueRange = -12f..12f,
                        steps = 24
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun StemSliderItem(
    icon: String,
    name: String,
    gain: Float,
    onGainChange: (Float) -> Unit,
    color: Color
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("$icon $name", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Text("${(gain * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Black, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Slider(
            value = gain,
            onValueChange = onGainChange,
            valueRange = 0.0f..1.5f,
            colors = SliderDefaults.colors(
                thumbColor = color,
                activeTrackColor = color
            )
        )
    }
}
