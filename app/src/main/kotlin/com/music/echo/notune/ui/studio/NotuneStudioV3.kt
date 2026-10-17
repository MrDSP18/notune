package com.music.echo.notune.ui.studio

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.music.echo.notune.design.theme.NotuneDarkUniverse
import com.music.echo.notune.design.theme.NotuneDeepViolet
import com.music.echo.notune.design.theme.NotuneRed
import com.music.echo.notune.design.theme.NotuneSurfaceDark
import com.music.echo.notune.design.theme.NotuneSurfaceVariantDark
import com.music.echo.notune.design.theme.NotuneTextPrimary
import com.music.echo.notune.design.theme.NotuneTextSecondary

enum class StudioPreset(val title: String, val icon: String) {
    ORIGINAL("Original", "🎧"),
    BASS_BOOST("Bass Boost", "🔊"),
    VOCAL("Vocal Clarity", "🎙️"),
    SPATIAL_3D("Spatial 3D", "🌌"),
    NIGHT_MODE("Night Mode", "🌙"),
    STUDIO_PRO("Studio Master", "🎛️")
}

@Composable
fun NotuneStudioV3(
    modifier: Modifier = Modifier
) {
    var activePreset by remember { mutableStateOf(StudioPreset.ORIGINAL) }
    var isProMode by remember { mutableStateOf(false) }
    var bassLevel by remember { mutableStateOf(0.5f) }
    var trebleLevel by remember { mutableStateOf(0.5f) }
    var spatialLevel by remember { mutableStateOf(0.4f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NotuneDarkUniverse)
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "NØ STUDIO",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = NotuneTextPrimary,
                    letterSpacing = 1.5.sp
                )

                // Simple vs Pro Switch
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = NotuneSurfaceDark,
                    modifier = Modifier.clickable { isProMode = !isProMode }
                ) {
                    Text(
                        text = if (isProMode) "PRO MODE" else "SIMPLE MODE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NotuneRed,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!isProMode) {
                // Simple Mode: Preset Grid
                Text(
                    text = "Sound Profiles",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = NotuneTextPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(StudioPreset.values()) { preset ->
                        val isSelected = preset == activePreset
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) NotuneRed else NotuneSurfaceDark,
                            modifier = Modifier
                                .height(80.dp)
                                .clickable { activePreset = preset }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = preset.icon, fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = preset.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else NotuneTextPrimary
                                )
                            }
                        }
                    }
                }
            } else {
                // Pro Mode: Sliders & Headphone Intelligence
                Text(
                    text = "Pro Audio Engine Controls",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = NotuneTextPrimary
                )
                Spacer(modifier = Modifier.height(14.dp))

                AudioControlSlider("Bass Boost", bassLevel) { bassLevel = it }
                AudioControlSlider("Treble Clarity", trebleLevel) { trebleLevel = it }
                AudioControlSlider("3D Spatial Width", spatialLevel) { spatialLevel = it }

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = NotuneSurfaceDark,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "🎧 HEADPHONE INTELLIGENCE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NotuneDeepViolet
                        )
                        Text(
                            text = "Connected: Sony WH-1000XM5",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = NotuneTextPrimary
                        )
                        Text(
                            text = "Auto-applied target curve profile",
                            fontSize = 11.sp,
                            color = NotuneTextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AudioControlSlider(label: String, value: Float, onValueChange: (Float) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 13.sp, color = NotuneTextPrimary)
            Text(text = "${(value * 100).toInt()}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NotuneRed)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            colors = SliderDefaults.colors(
                thumbColor = NotuneRed,
                activeTrackColor = NotuneRed,
                inactiveTrackColor = NotuneSurfaceVariantDark
            )
        )
    }
}
