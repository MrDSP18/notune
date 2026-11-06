package com.music.echo.notune.ui.flow

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import echo.music.iad1tya.ui.theme.NothingFont

@Composable
fun MusicFlowControl(
    modifier: Modifier = Modifier
) {
    var selectedPreset by remember { mutableStateOf("CALM → FOCUS") }
    val presets = listOf("CALM → FOCUS", "FOCUS → ENERGY", "ENERGY → CHILL", "CHILL → SLEEP")

    var energyLevel by remember { mutableFloatStateOf(0.7f) }
    var vocalFocus by remember { mutableFloatStateOf(0.5f) }
    var moodIntensity by remember { mutableFloatStateOf(0.8f) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "MUSIC FLOW CONTROL",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = NothingFont,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(presets) { preset ->
                    val isSelected = selectedPreset == preset
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedPreset = preset },
                        label = {
                            Text(
                                text = preset,
                                style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont)
                            )
                        },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            FlowSlider("Energy Output", energyLevel) { energyLevel = it }
            FlowSlider("Vocal Density", vocalFocus) { vocalFocus = it }
            FlowSlider("Atmosphere Depth", moodIntensity) { moodIntensity = it }
        }
    }
}

@Composable
private fun FlowSlider(label: String, value: Float, onValueChange: (Float) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.White)
            Text(text = "${(value * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
        }
        Slider(value = value, onValueChange = onValueChange)
    }
}
