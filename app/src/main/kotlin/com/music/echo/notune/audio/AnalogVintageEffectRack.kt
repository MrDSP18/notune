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
fun AnalogVintageEffectRack(
    onBackClick: () -> Unit = {}
) {
    var vinylCrackle by remember { mutableStateOf(0.35f) }
    var tapeFlutter by remember { mutableStateOf(0.20f) }
    var tubeWarmth by remember { mutableStateOf(0.40f) }
    var cassetteHiss by remember { mutableStateOf(0.15f) }

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
                        text = "ANALOG VINTAGE EFFECT RACK",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.5.sp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Vinyl Dust, Tape Wow & Tube Saturation",
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
                    Text("📀 VINYL CRACKLE & DUST NOISE: ${(vinylCrackle * 100).toInt()}%", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Slider(value = vinylCrackle, onValueChange = { vinylCrackle = it }, valueRange = 0f..1f)

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("📼 TAPE WOW & FLUTTER: ${(tapeFlutter * 100).toInt()}%", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Slider(value = tapeFlutter, onValueChange = { tapeFlutter = it }, valueRange = 0f..1f)

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("🔥 VACUUM TUBE SATURATION: ${(tubeWarmth * 100).toInt()}%", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Slider(value = tubeWarmth, onValueChange = { tubeWarmth = it }, valueRange = 0f..1f)

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("📻 CASSETTE DECK HISS: ${(cassetteHiss * 100).toInt()}%", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Slider(value = cassetteHiss, onValueChange = { cassetteHiss = it }, valueRange = 0f..1f)
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
