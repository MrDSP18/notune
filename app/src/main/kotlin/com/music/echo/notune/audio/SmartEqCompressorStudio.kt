package com.music.echo.notune.audio

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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

data class EqPreset(val name: String, val bands: List<Float>)

object EqPresets {
    val ALL_PRESETS = listOf(
        EqPreset("Flat", listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)),
        EqPreset("Bass Boost", listOf(9f, 7f, 5f, 2f, 0f, 0f, 0f, 0f, 1f, 2f)),
        EqPreset("Vocal Clarity", listOf(-2f, -1f, 0f, 2f, 4f, 6f, 5f, 3f, 1f, 0f)),
        EqPreset("Electronic", listOf(7f, 6f, 3f, 0f, -2f, 2f, 4f, 6f, 7f, 8f)),
        EqPreset("Hip-Hop", listOf(8f, 7f, 4f, 1f, -1f, 1f, 3f, 4f, 5f, 6f)),
        EqPreset("Rock", listOf(6f, 4f, 2f, 0f, -1f, 1f, 3f, 5f, 6f, 7f)),
        EqPreset("Jazz", listOf(4f, 3f, 1f, 2f, -1f, -1f, 0f, 2f, 3f, 4f)),
        EqPreset("Classical", listOf(5f, 4f, 3f, 2f, -1f, -1f, 0f, 2f, 4f, 5f)),
        EqPreset("Acoustic", listOf(3f, 2f, 1f, 2f, 3f, 3f, 4f, 3f, 2f, 2f)),
        EqPreset("Pop", listOf(2f, 3f, 5f, 4f, 1f, -1f, 2f, 4f, 5f, 6f)),
        EqPreset("Dance", listOf(7f, 6f, 4f, 0f, 0f, 3f, 5f, 6f, 7f, 7f)),
        EqPreset("Heavy Metal", listOf(7f, 6f, 3f, 0f, -2f, 2f, 5f, 7f, 8f, 8f)),
        EqPreset("Lo-Fi", listOf(3f, 2f, 1f, -1f, -2f, -1f, 1f, 2f, 0f, -2f)),
        EqPreset("Club", listOf(5f, 6f, 4f, 2f, 0f, 0f, 2f, 4f, 5f, 6f)),
        EqPreset("Deep House", listOf(8f, 8f, 5f, 2f, 0f, -1f, 1f, 3f, 5f, 6f)),
        EqPreset("EDM", listOf(9f, 8f, 5f, 1f, -1f, 2f, 4f, 6f, 8f, 9f))
    )
}

@Composable
fun SmartEqCompressorStudio(
    onBackClick: () -> Unit = {}
) {
    val frequencies = listOf("31Hz", "63Hz", "125Hz", "250Hz", "500Hz", "1kHz", "2kHz", "4kHz", "8kHz", "16kHz")
    var bandGains by remember { mutableStateOf(EqPresets.ALL_PRESETS.first().bands) }
    var selectedPresetName by remember { mutableStateOf("Flat") }
    var isLimiterEnabled by remember { mutableStateOf(true) }
    var tubeWarmth by remember { mutableStateOf(0.3f) }

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
                        text = "10-BAND PRECISION EQUALIZER",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.5.sp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Multiband Compressor & Tube Warmth",
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
                // Genre Preset Chips Row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(EqPresets.ALL_PRESETS) { preset ->
                        FilterChip(
                            selected = selectedPresetName == preset.name,
                            onClick = {
                                selectedPresetName = preset.name
                                bandGains = preset.bands
                            },
                            label = { Text(preset.name, fontWeight = FontWeight.Bold) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 10-Band Sliders Card
                NoTuneSurfaceCard(
                    cardStyle = CardStyleVariant.GLASS,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("10-BAND GRAPHIC EQUALIZER", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        frequencies.forEachIndexed { index, freq ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "${bandGains[index].toInt()}dB",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))

                                // Vertical Slider Box
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .width(28.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Slider(
                                        value = bandGains[index],
                                        onValueChange = { newVal ->
                                            val updated = bandGains.toMutableList()
                                            updated[index] = newVal
                                            bandGains = updated
                                            selectedPresetName = "Custom"
                                        },
                                        valueRange = -12f..12f,
                                        modifier = Modifier.fillMaxHeight()
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = freq,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 9.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Multiband Limiter & Tube Warmth Card
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
                            Text("Dynamic Peak Limiter", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("Prevents digital clipping distortion", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(checked = isLimiterEnabled, onCheckedChange = { isLimiterEnabled = it })
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Analog Tube Warmth: ${(tubeWarmth * 100).toInt()}%", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Slider(
                        value = tubeWarmth,
                        onValueChange = { tubeWarmth = it },
                        valueRange = 0.0f..1.0f
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
