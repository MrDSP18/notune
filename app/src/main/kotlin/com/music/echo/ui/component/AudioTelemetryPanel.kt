package echo.music.iad1tya.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import echo.music.iad1tya.models.PlaybackState

@Composable
fun AudioTelemetryPanel(
    playbackState: PlaybackState,
    modifier: Modifier = Modifier
) {
    val telemetry = playbackState.telemetry

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TelemetryItem(label = "SOURCE", value = telemetry.mimeType?.substringAfter("/")?.uppercase() ?: "UNKNOWN")
            TelemetryItem(label = "CODEC", value = telemetry.codec ?: "UNKNOWN")
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TelemetryItem(label = "BITRATE", value = telemetry.bitrate?.let { "${it / 1000} kbps" } ?: "---")
            TelemetryItem(label = "SAMPLE RATE", value = telemetry.sampleRate?.let { "${it / 1000f} kHz" } ?: "---")
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TelemetryItem(label = "CHANNELS", value = telemetry.channelCount?.toString() ?: "---")
            TelemetryItem(label = "SESSION ID", value = telemetry.audioSessionId?.toString() ?: "---")
        }
    }
}

@Composable
private fun TelemetryItem(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
