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

@Composable
fun AcousticRoomCalibrator(
    onBackClick: () -> Unit = {}
) {
    var selectedProfile by remember { mutableStateOf("Harman In-Ear Target") }
    var standingWaveDampening by remember { mutableStateOf(0.40f) }
    var speakerDistanceMeters by remember { mutableStateOf(1.8f) }

    val profiles = listOf("Harman In-Ear Target", "Flat Reference", "Diffuse Field", "Studio Monitor", "Bass Enhanced")

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
                        text = "ACOUSTIC ROOM CALIBRATION",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.5.sp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Speaker Tuning & Target Curves",
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
                // Profile Selector Card
                NoTuneSurfaceCard(
                    cardStyle = CardStyleVariant.GLASS,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("HEADPHONE TARGET CURVE PROFILE", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(profiles) { prof ->
                            FilterChip(
                                selected = selectedProfile == prof,
                                onClick = { selectedProfile = prof },
                                label = { Text(prof, fontWeight = FontWeight.Bold) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Calibration Sliders Card
                NoTuneSurfaceCard(
                    cardStyle = CardStyleVariant.GLASS,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("ROOM ACOUSTIC STANDING WAVE DAMPENER: ${(standingWaveDampening * 100).toInt()}%", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Slider(value = standingWaveDampening, onValueChange = { standingWaveDampening = it }, valueRange = 0f..1f)

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("SPEAKER LISTENING DISTANCE: ${String.format("%.1f", speakerDistanceMeters)}m", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Slider(value = speakerDistanceMeters, onValueChange = { speakerDistanceMeters = it }, valueRange = 0.5f..5.0f)
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
