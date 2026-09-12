package echo.music.iad1tya.ui.component

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import echo.music.iad1tya.R
import echo.music.iad1tya.notune.flow.FlowContextMode
import echo.music.iad1tya.notune.flow.FlowMode

@Composable
fun FlowControlsBar(
    onMoreLikeThis: () -> Unit,
    onLessLikeThis: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FlowQuickActionButton(
            label = "More Like This",
            iconRes = R.drawable.trending_up,
            accentColor = Color(0xFF4CAF50),
            onClick = onMoreLikeThis
        )

        FlowQuickActionButton(
            label = "Less Like This",
            iconRes = R.drawable.remove,
            accentColor = Color(0xFFFF5252),
            onClick = onLessLikeThis
        )
    }
}

@Composable
private fun FlowQuickActionButton(
    label: String,
    iconRes: Int,
    accentColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = label,
                tint = accentColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

@Composable
fun FlowContextSelector(
    selectedContextMode: FlowContextMode,
    onContextModeSelected: (FlowContextMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(
            text = "FLOW VIBE & CONTEXT",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.6f),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)
        ) {
            items(FlowContextMode.values()) { contextMode ->
                val isSelected = contextMode == selectedContextMode
                FilterChip(
                    selected = isSelected,
                    onClick = { onContextModeSelected(contextMode) },
                    label = {
                        Text(
                            text = contextMode.name.lowercase().replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFFFFC00),
                        selectedLabelColor = Color.Black,
                        containerColor = Color.White.copy(alpha = 0.08f),
                        labelColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }
    }
}

@Composable
fun FlowDiscoverySlider(
    discoveryRatio: Float,
    onDiscoveryRatioChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "DISCOVERY RATIO",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.6f)
            )
            Text(
                text = when {
                    discoveryRatio < 0.25f -> "Familiar Favorites"
                    discoveryRatio < 0.6f -> "Balanced Flow"
                    else -> "Deep Discovery"
                },
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFFFFC00)
            )
        }

        Slider(
            value = discoveryRatio,
            onValueChange = onDiscoveryRatioChanged,
            valueRange = 0.1f..0.9f,
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFFFFFC00),
                activeTrackColor = Color(0xFFFFFC00),
                inactiveTrackColor = Color.White.copy(alpha = 0.2f)
            )
        )
    }
}
