package com.music.echo.notune.audio

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.music.echo.ui.theme.tokens.LocalThemeTokens

@Composable
fun AudioLabTechnicalDashboard(
    codec: String = "Opus / AAC",
    sampleRate: String = "48.0 kHz",
    bitrate: String = "320 kbps",
    channels: String = "Stereo 2.0",
    outputDevice: String = "Device Speaker / A2DP",
    replayGain: String = "-1.2 dB",
    modifier: Modifier = Modifier
) {
    val tokens = LocalThemeTokens.current

    Surface(
        shape = tokens.shapes.cardShape,
        color = tokens.colors.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, tokens.colors.outline),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "TECHNICAL AUDIO METRICS",
                    style = tokens.typography.labelLarge.copy(fontSize = 11.sp),
                    color = tokens.colors.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "● LIVE AUDIO LAB",
                    style = tokens.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp),
                    color = tokens.colors.accent
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                AudioMetricBadge(label = "CODEC", value = codec, modifier = Modifier.weight(1f))
                AudioMetricBadge(label = "SAMPLE RATE", value = sampleRate, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                AudioMetricBadge(label = "BITRATE", value = bitrate, modifier = Modifier.weight(1f))
                AudioMetricBadge(label = "CHANNELS", value = channels, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                AudioMetricBadge(label = "OUTPUT DEVICE", value = outputDevice, modifier = Modifier.weight(1f))
                AudioMetricBadge(label = "REPLAYGAIN", value = replayGain, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun AudioMetricBadge(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val tokens = LocalThemeTokens.current
    Box(
        modifier = modifier
            .background(tokens.colors.card, shape = RoundedCornerShape(6.dp))
            .border(1.dp, tokens.colors.outline, shape = RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Column {
            Text(
                text = label,
                style = tokens.typography.bodySmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                color = tokens.colors.textSecondary
            )
            Text(
                text = value,
                style = tokens.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace, fontSize = 12.sp, fontWeight = FontWeight.Bold),
                color = tokens.colors.textPrimary
            )
        }
    }
}
