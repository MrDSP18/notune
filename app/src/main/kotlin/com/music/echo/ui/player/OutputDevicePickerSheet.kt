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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import echo.music.iad1tya.R
import echo.music.iad1tya.ui.adaptive.NotuneDesignSystem

data class AudioDeviceItem(
    val id: String,
    val name: String,
    val typeDescription: String,
    val isConnected: Boolean,
    val iconRes: Int
)

val SampleAudioDevices = listOf(
    AudioDeviceItem("speaker_internal", "THIS PHONE", "Internal Speaker", true, R.drawable.ic_widget_play),
    AudioDeviceItem("bluetooth_headset", "BLUETOOTH HEADPHONES", "Wireless Audio", false, R.drawable.ic_widget_play),
    AudioDeviceItem("aux_wire", "WIRED HEADPHONES", "3.5mm / USB-C Audio", false, R.drawable.ic_widget_play),
    AudioDeviceItem("cast_device", "SMART CAST SPEAKER", "Network Cast Endpoint", false, R.drawable.ic_widget_play)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutputDevicePickerSheet(
    onDismissRequest: () -> Unit,
    onDeviceSelected: (AudioDeviceItem) -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedDeviceId by remember { mutableStateOf("speaker_internal") }

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
        ) {
            Text(
                text = "AUDIO OUTPUT DEVICE",
                style = NotuneDesignSystem.typography.title,
                color = NotuneDesignSystem.colors.primary,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Select Audio Destination",
                style = NotuneDesignSystem.typography.bodySmall,
                color = NotuneDesignSystem.colors.textSecondary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(SampleAudioDevices) { device ->
                    val isSelected = device.id == selectedDeviceId
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) NotuneDesignSystem.colors.surfaceVariant else NotuneDesignSystem.colors.surface)
                            .border(1.dp, if (isSelected) NotuneDesignSystem.colors.primary else NotuneDesignSystem.colors.surfaceBorder, RoundedCornerShape(6.dp))
                            .clickable {
                                selectedDeviceId = device.id
                                onDeviceSelected(device)
                                onDismissRequest()
                            }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) NotuneDesignSystem.colors.primary else NotuneDesignSystem.colors.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(device.iconRes),
                                contentDescription = null,
                                tint = if (isSelected) NotuneDesignSystem.colors.onPrimary else NotuneDesignSystem.colors.textPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = device.name,
                                style = NotuneDesignSystem.typography.label,
                                color = NotuneDesignSystem.colors.textPrimary,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = device.typeDescription,
                                style = NotuneDesignSystem.typography.bodySmall,
                                color = NotuneDesignSystem.colors.textSecondary
                            )
                        }

                        if (isSelected) {
                            Text(
                                text = "ACTIVE",
                                style = NotuneDesignSystem.typography.telemetry,
                                color = NotuneDesignSystem.colors.primary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
