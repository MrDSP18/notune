package echo.music.iad1tya.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import echo.music.iad1tya.ui.adaptive.NotuneDesignSystem

val EqualizerPresets = listOf("Flat", "Bass Boost", "Vocal", "Rock", "Electronic", "Classical", "Podcast", "Custom")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioLabSheet(
    onDismissRequest: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedPreset by remember { mutableStateOf("Flat") }
    var bassBoostValue by remember { mutableFloatStateOf(0f) }
    var crossfadeSeconds by remember { mutableFloatStateOf(0f) }
    var playbackSpeed by remember { mutableFloatStateOf(1.0f) }
    var isReplayGainEnabled by remember { mutableStateOf(true) }
    var isSkipSilenceEnabled by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = NotuneDesignSystem.colors.surface,
        contentColor = NotuneDesignSystem.colors.textPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "AUDIO LAB 2.0",
                style = NotuneDesignSystem.typography.title,
                color = NotuneDesignSystem.colors.primary,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "DSP Pipeline & Audio Signal Processing",
                style = NotuneDesignSystem.typography.bodySmall,
                color = NotuneDesignSystem.colors.textSecondary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Preset Selector
            Text(
                text = "EQUALIZER PRESETS",
                style = NotuneDesignSystem.typography.label,
                color = NotuneDesignSystem.colors.textPrimary,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(EqualizerPresets) { preset ->
                    val isSelected = preset == selectedPreset
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isSelected) NotuneDesignSystem.colors.primary else NotuneDesignSystem.colors.surfaceVariant)
                            .border(1.dp, if (isSelected) NotuneDesignSystem.colors.primary else NotuneDesignSystem.colors.surfaceBorder, RoundedCornerShape(4.dp))
                            .clickable { selectedPreset = preset }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = preset.uppercase(),
                            style = NotuneDesignSystem.typography.button,
                            color = if (isSelected) NotuneDesignSystem.colors.onPrimary else NotuneDesignSystem.colors.textPrimary,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Bass Boost Slider
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "BASS BOOST",
                        style = NotuneDesignSystem.typography.label,
                        color = NotuneDesignSystem.colors.textPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "${(bassBoostValue * 100).toInt()}%",
                        style = NotuneDesignSystem.typography.telemetry,
                        color = NotuneDesignSystem.colors.primary
                    )
                }
                Slider(
                    value = bassBoostValue,
                    onValueChange = { bassBoostValue = it },
                    colors = SliderDefaults.colors(
                        thumbColor = NotuneDesignSystem.colors.primary,
                        activeTrackColor = NotuneDesignSystem.colors.primary,
                        inactiveTrackColor = NotuneDesignSystem.colors.surfaceVariant
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Crossfade Slider
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "CROSSFADE",
                        style = NotuneDesignSystem.typography.label,
                        color = NotuneDesignSystem.colors.textPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "${crossfadeSeconds.toInt()} SEC",
                        style = NotuneDesignSystem.typography.telemetry,
                        color = NotuneDesignSystem.colors.primary
                    )
                }
                Slider(
                    value = crossfadeSeconds,
                    onValueChange = { crossfadeSeconds = it },
                    valueRange = 0f..12f,
                    colors = SliderDefaults.colors(
                        thumbColor = NotuneDesignSystem.colors.primary,
                        activeTrackColor = NotuneDesignSystem.colors.primary,
                        inactiveTrackColor = NotuneDesignSystem.colors.surfaceVariant
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Playback Speed Slider
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "PLAYBACK SPEED",
                        style = NotuneDesignSystem.typography.label,
                        color = NotuneDesignSystem.colors.textPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = String.format("%.2fx", playbackSpeed),
                        style = NotuneDesignSystem.typography.telemetry,
                        color = NotuneDesignSystem.colors.primary
                    )
                }
                Slider(
                    value = playbackSpeed,
                    onValueChange = { playbackSpeed = it },
                    valueRange = 0.5f..2.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = NotuneDesignSystem.colors.primary,
                        activeTrackColor = NotuneDesignSystem.colors.primary,
                        inactiveTrackColor = NotuneDesignSystem.colors.surfaceVariant
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Toggles: ReplayGain & Silence Skip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "REPLAYGAIN NORMALIZATION",
                        style = NotuneDesignSystem.typography.label,
                        color = NotuneDesignSystem.colors.textPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Equalize loudness across tracks",
                        style = NotuneDesignSystem.typography.bodySmall,
                        color = NotuneDesignSystem.colors.textSecondary
                    )
                }
                Switch(
                    checked = isReplayGainEnabled,
                    onCheckedChange = { isReplayGainEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = NotuneDesignSystem.colors.onPrimary,
                        checkedTrackColor = NotuneDesignSystem.colors.primary,
                        uncheckedThumbColor = NotuneDesignSystem.colors.textSecondary,
                        uncheckedTrackColor = NotuneDesignSystem.colors.surfaceVariant
                    )
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "SKIP SILENCE",
                        style = NotuneDesignSystem.typography.label,
                        color = NotuneDesignSystem.colors.textPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Bypass lead-in and tail silence automatically",
                        style = NotuneDesignSystem.typography.bodySmall,
                        color = NotuneDesignSystem.colors.textSecondary
                    )
                }
                Switch(
                    checked = isSkipSilenceEnabled,
                    onCheckedChange = { isSkipSilenceEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = NotuneDesignSystem.colors.onPrimary,
                        checkedTrackColor = NotuneDesignSystem.colors.primary,
                        uncheckedThumbColor = NotuneDesignSystem.colors.textSecondary,
                        uncheckedTrackColor = NotuneDesignSystem.colors.surfaceVariant
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
