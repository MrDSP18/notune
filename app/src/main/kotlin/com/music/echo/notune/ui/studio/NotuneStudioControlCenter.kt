package com.music.echo.notune.ui.studio

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.music.echo.notune.engine.EnvironmentProfile
import com.music.echo.ui.theme.NotuneThemeCatalog
import echo.music.iad1tya.constants.ThemePreset
import echo.music.iad1tya.ui.theme.NothingFont

@Composable
fun NotuneStudioControlCenter(
    modifier: Modifier = Modifier
) {
    var selectedThemeIndex by remember { mutableIntStateOf(0) }
    var selectedOsIndex by remember { mutableIntStateOf(0) }
    var selectedProfile by remember { mutableStateOf(EnvironmentProfile.NORMAL) }

    var gestureSensitivity by remember { mutableFloatStateOf(0.8f) }
    var hapticIntensity by remember { mutableFloatStateOf(0.6f) }

    val themePresets = ThemePreset.entries
    val currentPreset = themePresets[selectedThemeIndex % themePresets.size]
    val currentTokens = NotuneThemeCatalog.getTokens(currentPreset)
    val osProfiles = listOf("Pixel", "Samsung", "Nothing", "Xiaomi", "OnePlus", "NØTUNE Original")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "NØTUNE STUDIO // CONTROL CENTER",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = NothingFont,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // LIVE PREVIEW CARD
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = currentTokens.colors.surfaceVariant
                ),
                border = BorderStroke(
                    1.5.dp,
                    currentTokens.colors.primary
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "LIVE PREVIEW",
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont),
                            color = currentTokens.colors.primary
                        )
                        Text(
                            text = osProfiles[selectedOsIndex].uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont),
                            color = currentTokens.colors.textPrimary
                        )
                    }

                    Column {
                        Text(
                            text = "Husn • Anuv Jain",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                            color = currentTokens.colors.textPrimary
                        )
                        Text(
                            text = "Theme: ${currentTokens.name}",
                            style = MaterialTheme.typography.bodySmall,
                            color = currentTokens.colors.textSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Theme Selector Row
            Text(
                text = "THEME ENGINE (10 THEMES)",
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont),
                color = Color(0xFF71717A)
            )
            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(themePresets.size) { idx ->
                    val preset = themePresets[idx]
                    FilterChip(
                        selected = selectedThemeIndex == idx,
                        onClick = { selectedThemeIndex = idx },
                        label = { Text(text = preset.name, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // OS Personality Selector
            Text(
                text = "OS ECOSYSTEM ADAPTATION",
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont),
                color = Color(0xFF71717A)
            )
            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(osProfiles.size) { idx ->
                    FilterChip(
                        selected = selectedOsIndex == idx,
                        onClick = { selectedOsIndex = idx },
                        label = { Text(text = osProfiles[idx], style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Gesture & Haptic Calibration
            Text(
                text = "GESTURE SENSITIVITY",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White
            )
            Slider(value = gestureSensitivity, onValueChange = { gestureSensitivity = it })

            Text(
                text = "HAPTIC FEEDBACK INTENSITY",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White
            )
            Slider(value = hapticIntensity, onValueChange = { hapticIntensity = it })
        }
    }
}
