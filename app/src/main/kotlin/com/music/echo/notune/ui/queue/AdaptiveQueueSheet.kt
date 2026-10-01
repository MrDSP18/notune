package com.music.echo.notune.ui.queue

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import echo.music.iad1tya.ui.theme.NothingFont

data class QueueItemModel(
    val title: String,
    val artist: String,
    val matchPercentage: Int
)

@Composable
fun AdaptiveQueueSheet(
    queueItems: List<QueueItemModel> = listOf(
        QueueItemModel("Husn", "Anuv Jain", 94),
        QueueItemModel("Baarishein", "Anuv Jain", 91),
        QueueItemModel("Choo Lo", "The Local Train", 87),
        QueueItemModel("Aftab", "The Local Train", 84)
    ),
    onVibeChange: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedVibe by remember { mutableStateOf("Normal") }
    val vibes = listOf("😌 Calm", "🔥 Energy", "🌙 Night", "💔 Emotional", "🎧 Focus")

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
                text = "UP NEXT // ADAPTIVE QUEUE",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = NothingFont,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "CHANGE THE VIBE",
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont),
                color = Color(0xFF71717A)
            )

            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(vibes) { vibe ->
                    val isSelected = selectedVibe == vibe
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedVibe = vibe
                            onVibeChange(vibe)
                        },
                        label = { Text(text = vibe, style = MaterialTheme.typography.labelMedium) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(queueItems) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF27272A).copy(alpha = 0.5f))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = item.artist,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFA1A1AA),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "${item.matchPercentage}% MATCH",
                                style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
