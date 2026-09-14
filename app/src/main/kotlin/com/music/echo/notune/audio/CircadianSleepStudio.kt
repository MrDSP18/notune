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
fun CircadianSleepStudio(
    onBackClick: () -> Unit = {}
) {
    var isBinauralEnabled by remember { mutableStateOf(false) }
    var beatFrequencyHz by remember { mutableStateOf(3.5f) } // Delta wave
    var whiteNoiseVol by remember { mutableStateOf(0.2f) }
    var rainVol by remember { mutableStateOf(0.4f) }
    var sleepTimerMinutes by remember { mutableStateOf(30) }

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
                        text = "CIRCADIAN SLEEP & SOUNDSCAPE",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.5.sp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Binaural Beats & Sleep Timer",
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
                            Text("Binaural Beats Generator", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("Deep Sleep & REM Delta Frequencies", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(checked = isBinauralEnabled, onCheckedChange = { isBinauralEnabled = it })
                    }

                    if (isBinauralEnabled) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Target Frequency: ${String.format("%.1f", beatFrequencyHz)} Hz (${if (beatFrequencyHz <= 4.0f) "Delta Deep Sleep" else "Theta Relaxation"})",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                        Slider(
                            value = beatFrequencyHz,
                            onValueChange = { beatFrequencyHz = it },
                            valueRange = 0.5f..8.0f
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                NoTuneSurfaceCard(
                    cardStyle = CardStyleVariant.GLASS,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("ACOUSTIC SOUNDSCAPE MIXER", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("🌧️ Gentle Rain Volume: ${(rainVol * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    Slider(value = rainVol, onValueChange = { rainVol = it }, valueRange = 0.0f..1.0f)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("💨 Pink & Brown Ambient Noise: ${(whiteNoiseVol * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    Slider(value = whiteNoiseVol, onValueChange = { whiteNoiseVol = it }, valueRange = 0.0f..1.0f)
                }

                Spacer(modifier = Modifier.height(16.dp))

                NoTuneSurfaceCard(
                    cardStyle = CardStyleVariant.GLASS,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("CIRCADIAN SLEEP TIMER", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("$sleepTimerMinutes Minutes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                    Slider(
                        value = sleepTimerMinutes.toFloat(),
                        onValueChange = { sleepTimerMinutes = it.toInt() },
                        valueRange = 5f..120f,
                        steps = 22
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
