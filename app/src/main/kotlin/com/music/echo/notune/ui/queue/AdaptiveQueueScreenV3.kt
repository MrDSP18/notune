package com.music.echo.notune.ui.queue

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

data class QueueItemV3(
    val id: String,
    val title: String,
    val artist: String,
    val matchPercentage: Int,
    val matchTag: String,
    val isNowPlaying: Boolean = false,
    val reasonFactors: List<String>
)

@Composable
fun AdaptiveQueueScreenV3(
    nowPlaying: QueueItemV3?,
    upcomingQueue: List<QueueItemV3>,
    onTrackSelect: (QueueItemV3) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedGroundingTrack by remember { mutableStateOf<QueueItemV3?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NotuneDarkUniverse)
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ADAPTIVE QUEUE",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = NotuneTextPrimary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "NØ FLOW CONTINUOUS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NotuneRed,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(NotuneRed.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            // NOW PLAYING ITEM (STRICTLY LOCKED)
            nowPlaying?.let { track ->
                Text(
                    text = "NOW PLAYING (LOCKED)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NotuneTextSecondary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NotuneSurfaceDark),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(NotuneRed),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = track.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = NotuneTextPrimary
                            )
                            Text(
                                text = track.artist,
                                fontSize = 12.sp,
                                color = NotuneTextSecondary
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked Track",
                            tint = NotuneTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Text(
                text = "UP NEXT — CONTINUOUS REPLENISHMENT",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = NotuneTextSecondary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // UPCOMING QUEUE LIST WITH MATCH SCORES & "WHY THIS SONG?"
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                itemsIndexed(upcomingQueue) { index, track ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = NotuneSurfaceDark,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onTrackSelect(track) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = String.format("%02d", index + 1),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = NotuneTextSecondary,
                                modifier = Modifier.width(28.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = track.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NotuneTextPrimary
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = track.artist,
                                        fontSize = 12.sp,
                                        color = NotuneTextSecondary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${track.matchTag} ${track.matchPercentage}%",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (track.matchPercentage > 85) NotuneRed else NotuneDeepViolet
                                    )
                                }
                            }

                            // "Why this song?" Info Button
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Why this song?",
                                tint = NotuneTextSecondary,
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .clickable { selectedGroundingTrack = track }
                                    .padding(2.dp)
                            )
                        }
                    }
                }
            }
        }

        // "Why this song?" AI Grounding Bottom Sheet Dialog
        selectedGroundingTrack?.let { track ->
            WhyThisSongDialog(
                track = track,
                onDismiss = { selectedGroundingTrack = null }
            )
        }
    }
}

@Composable
private fun WhyThisSongDialog(
    track: QueueItemV3,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = NotuneSurfaceDark
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "WHY THIS SONG?",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = NotuneRed,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = track.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = NotuneTextPrimary
                )
                Text(
                    text = "by ${track.artist}",
                    fontSize = 13.sp,
                    color = NotuneTextSecondary
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "NØTUNE chose this because:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = NotuneTextPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))

                track.reasonFactors.forEach { factor ->
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "✓", color = NotuneRed, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = factor, fontSize = 13.sp, color = NotuneTextPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = NotuneSurfaceVariantDark),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Got it", color = NotuneTextPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
