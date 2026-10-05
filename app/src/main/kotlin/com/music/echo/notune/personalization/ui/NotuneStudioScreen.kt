package com.music.echo.notune.personalization.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.music.echo.notune.personalization.repository.NotuneCustomizationConfig
import com.music.echo.notune.personalization.repository.NotuneGeometryPreset
import com.music.echo.notune.personalization.repository.NotuneThemePreset
import com.music.echo.notune.theme.NoTuneAmbientCanvas
import echo.music.iad1tya.ui.theme.NothingFont

@Composable
fun NotuneStudioScreen(
    navController: NavController,
    config: NotuneCustomizationConfig,
    onConfigUpdated: (NotuneCustomizationConfig) -> Unit,
    onExportThemeJson: () -> String,
    onImportThemeJson: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    var selectedSection by remember { mutableStateOf("IDENTITY") }
    var jsonDialogVisible by remember { mutableStateOf(false) }
    var importJsonText by remember { mutableStateOf("") }
    var exportJsonText by remember { mutableStateOf("") }

    val sections = listOf(
        "IDENTITY", "COLORS", "TYPOGRAPHY", "GEOMETRY",
        "EFFECTS", "PLAYER", "NAVIGATION", "COMPONENTS", "MOTION", "ACCESSIBILITY"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        NoTuneAmbientCanvas(modifier = Modifier.fillMaxSize(), isPlaying = true)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "NØTUNE STUDIO",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = NothingFont,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Complete Application Design & Engine Customization",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Row {
                    OutlinedButton(
                        onClick = {
                            exportJsonText = onExportThemeJson()
                            jsonDialogVisible = true
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Text("EXPORT JSON", style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont))
                    }
                }
            }

            // Section Selector Tabs
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sections) { sec ->
                    val isSelected = sec == selectedSection
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { selectedSection = sec }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = sec,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = NothingFont,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Settings Column
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                when (selectedSection) {
                    "IDENTITY" -> {
                        Text("BRAND IDENTITY & LOGO SYSTEM", style = MaterialTheme.typography.labelMedium.copy(fontFamily = NothingFont))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("NØTUNE Red #FF0031 remains the canonical industrial dot-matrix design identity.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                    "COLORS" -> {
                        Text("THEME PRESETS", style = MaterialTheme.typography.labelMedium.copy(fontFamily = NothingFont))
                        Spacer(modifier = Modifier.height(8.dp))
                        NotuneThemePreset.values().forEach { preset ->
                            val isSel = config.theme.preset == preset
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSel) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent)
                                    .clickable {
                                        val newTheme = config.theme.copy(preset = preset)
                                        onConfigUpdated(config.copy(theme = newTheme))
                                    }
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(preset.name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                if (isSel) Text("ACTIVE", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary))
                            }
                        }
                    }
                    "GEOMETRY" -> {
                        Text("GEOMETRY & SHAPE SYSTEM", style = MaterialTheme.typography.labelMedium.copy(fontFamily = NothingFont))
                        Spacer(modifier = Modifier.height(8.dp))
                        NotuneGeometryPreset.values().forEach { geo ->
                            val isSel = config.geometry.preset == geo
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSel) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent)
                                    .clickable {
                                        val newGeo = config.geometry.copy(preset = geo)
                                        onConfigUpdated(config.copy(geometry = newGeo))
                                    }
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(geo.name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                if (isSel) Text("ACTIVE", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary))
                            }
                        }
                    }
                    "PLAYER" -> {
                        Text("PLAYER COMPONENT VISIBILITY", style = MaterialTheme.typography.labelMedium.copy(fontFamily = NothingFont))
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Show Album Artwork")
                            Switch(checked = config.player.showArtwork, onCheckedChange = {
                                onConfigUpdated(config.copy(player = config.player.copy(showArtwork = it)))
                            })
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Show Live Lyrics Button")
                            Switch(checked = config.player.showLyrics, onCheckedChange = {
                                onConfigUpdated(config.copy(player = config.player.copy(showLyrics = it)))
                            })
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Show Audio Visualizer")
                            Switch(checked = config.player.showVisualizer, onCheckedChange = {
                                onConfigUpdated(config.copy(player = config.player.copy(showVisualizer = it)))
                            })
                        }
                    }
                    "COMPONENTS" -> {
                        Text("HOME DASHBOARD COMPONENTS", style = MaterialTheme.typography.labelMedium.copy(fontFamily = NothingFont))
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Show Recently Played")
                            Switch(checked = config.components.showRecentlyPlayed, onCheckedChange = {
                                onConfigUpdated(config.copy(components = config.components.copy(showRecentlyPlayed = it)))
                            })
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Show Music DNA Card")
                            Switch(checked = config.components.showMusicDna, onCheckedChange = {
                                onConfigUpdated(config.copy(components = config.components.copy(showMusicDna = it)))
                            })
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Show Listening Rooms")
                            Switch(checked = config.components.showRooms, onCheckedChange = {
                                onConfigUpdated(config.copy(components = config.components.copy(showRooms = it)))
                            })
                        }
                    }
                    else -> {
                        Text("CUSTOMIZATION SECTION: $selectedSection", style = MaterialTheme.typography.labelMedium.copy(fontFamily = NothingFont))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Configure options for $selectedSection.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
            }
        }
    }

    if (jsonDialogVisible) {
        AlertDialog(
            onDismissRequest = { jsonDialogVisible = false },
            title = { Text("NØTUNE THEME JSON", style = MaterialTheme.typography.titleMedium.copy(fontFamily = NothingFont)) },
            text = {
                Column {
                    Text("Exported Theme Configuration:", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    OutlinedTextField(
                        value = exportJsonText,
                        onValueChange = { exportJsonText = it },
                        modifier = Modifier.fillMaxWidth().height(180.dp),
                        readOnly = false
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (exportJsonText.isNotBlank()) {
                        onImportThemeJson(exportJsonText)
                    }
                    jsonDialogVisible = false
                }) {
                    Text("IMPORT JSON")
                }
            },
            dismissButton = {
                TextButton(onClick = { jsonDialogVisible = false }) {
                    Text("CLOSE")
                }
            }
        )
    }
}
