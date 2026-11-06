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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.unit.sp
import com.music.echo.notune.player.SongIntelligenceInfo
import echo.music.iad1tya.R
import echo.music.iad1tya.ui.adaptive.NotuneDesignSystem

data class AiActionItem(
    val id: String,
    val title: String,
    val description: String,
    val iconRes: Int
)

val StandardAiActions = listOf(
    AiActionItem("more_like_this", "MORE LIKE THIS", "Queue more songs with this sound and vibe", R.drawable.sparks),
    AiActionItem("change_vibe", "CHANGE THE VIBE", "Shift tempo, mood, or energy style", R.drawable.sparks),
    AiActionItem("surprise_me", "SURPRISE ME", "Introduce a fresh discovery from your taste profile", R.drawable.sparks),
    AiActionItem("less_like_this", "LESS LIKE THIS", "Reduce frequency of this style in current session", R.drawable.sparks),
    AiActionItem("keep_style", "KEEP THIS STYLE", "Lock current sound profile for the next 10 tracks", R.drawable.sparks),
    AiActionItem("similar_artists", "FIND SIMILAR ARTISTS", "Discover artists sharing this musical DNA", R.drawable.artist),
    AiActionItem("explain_song", "EXPLAIN THIS SONG", "View acoustic breakdown & recommendation reason", R.drawable.info),
    AiActionItem("add_similar", "ADD SIMILAR SONGS", "Append 5 matching tracks to queue", R.drawable.sparks),
    AiActionItem("build_playlist", "BUILD AI PLAYLIST", "Generate a custom playlist around this track", R.drawable.playlist_add)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotuneAiPlayerSheet(
    onDismissRequest: () -> Unit,
    intelligenceInfo: SongIntelligenceInfo = SongIntelligenceInfo(),
    isAiDjEnabled: Boolean = false,
    onToggleAiDj: (Boolean) -> Unit = {},
    onActionSelected: (AiActionItem) -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showWhyThisSong by remember { mutableStateOf(false) }

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
            // Header: Title & AI DJ Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ASK NØTUNE AI",
                        style = NotuneDesignSystem.typography.title,
                        color = NotuneDesignSystem.colors.primary,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Contextual Intelligence Controls",
                        style = NotuneDesignSystem.typography.bodySmall,
                        color = NotuneDesignSystem.colors.textSecondary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "AI DJ",
                        style = NotuneDesignSystem.typography.label,
                        color = NotuneDesignSystem.colors.textPrimary,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Switch(
                        checked = isAiDjEnabled,
                        onCheckedChange = onToggleAiDj,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = NotuneDesignSystem.colors.onPrimary,
                            checkedTrackColor = NotuneDesignSystem.colors.primary,
                            uncheckedThumbColor = NotuneDesignSystem.colors.textSecondary,
                            uncheckedTrackColor = NotuneDesignSystem.colors.surfaceVariant
                        )
                    )
                }
            }

            // Why This Song Factual Explanation Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(NotuneDesignSystem.colors.surfaceVariant)
                    .border(1.dp, NotuneDesignSystem.colors.surfaceBorder, RoundedCornerShape(8.dp))
                    .clickable { showWhyThisSong = !showWhyThisSong }
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "WHY THIS SONG?",
                            style = NotuneDesignSystem.typography.label,
                            color = NotuneDesignSystem.colors.primary,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            painter = painterResource(R.drawable.more_vert),
                            contentDescription = null,
                            tint = NotuneDesignSystem.colors.textSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = intelligenceInfo.primaryReason,
                        style = NotuneDesignSystem.typography.body,
                        color = NotuneDesignSystem.colors.textPrimary
                    )

                    if (showWhyThisSong) {
                        Spacer(modifier = Modifier.height(8.dp))
                        intelligenceInfo.secondaryReasons.forEach { reason ->
                            Text(
                                text = "• $reason",
                                style = NotuneDesignSystem.typography.caption,
                                color = NotuneDesignSystem.colors.textSecondary,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Energy: ${(intelligenceInfo.energyLevel * 100).toInt()}% | Tempo: ${intelligenceInfo.tempoBpm} BPM | Genre: ${intelligenceInfo.genre}",
                            style = NotuneDesignSystem.typography.telemetry,
                            color = NotuneDesignSystem.colors.primary,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // AI Action Items List
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(StandardAiActions) { action ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(NotuneDesignSystem.colors.surfaceVariant.copy(alpha = 0.6f))
                            .clickable {
                                onActionSelected(action)
                                onDismissRequest()
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(NotuneDesignSystem.colors.surface)
                                .border(1.dp, NotuneDesignSystem.colors.surfaceBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(action.iconRes),
                                contentDescription = null,
                                tint = NotuneDesignSystem.colors.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = action.title,
                                style = NotuneDesignSystem.typography.label,
                                color = NotuneDesignSystem.colors.textPrimary,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = action.description,
                                style = NotuneDesignSystem.typography.bodySmall,
                                color = NotuneDesignSystem.colors.textSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
