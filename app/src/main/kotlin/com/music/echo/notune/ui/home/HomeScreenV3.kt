package com.music.echo.notune.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.music.echo.notune.design.components.NØSoundMark
import com.music.echo.notune.design.components.SoundMarkState
import com.music.echo.notune.design.theme.NotuneDarkUniverse
import com.music.echo.notune.design.theme.NotuneDeepViolet
import com.music.echo.notune.design.theme.NotuneElectricCyan
import com.music.echo.notune.design.theme.NotuneRed
import com.music.echo.notune.design.theme.NotuneSurfaceDark
import com.music.echo.notune.design.theme.NotuneSurfaceVariantDark
import com.music.echo.notune.design.theme.NotuneTextPrimary
import com.music.echo.notune.design.theme.NotuneTextSecondary

data class VibeChip(val label: String, val emoji: String)
data class TrackPreviewItem(val id: String, val title: String, val artist: String, val emoji: String)
data class FriendStatusItem(val name: String, val activity: String, val avatarBg: Color)

@Composable
fun HomeScreenV3(
    isPlaying: Boolean,
    onLaunchFlow: (vibe: String?) -> Unit,
    onOpenAI: () -> Unit,
    onTrackSelect: (TrackPreviewItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var selectedVibe by remember { mutableStateOf<String?>(null) }

    val vibes = remember {
        listOf(
            VibeChip("Energy", "🔥"),
            VibeChip("Chill", "🌙"),
            VibeChip("Love", "❤️"),
            VibeChip("Focus", "🧠"),
            VibeChip("Kuthu", "🪘"),
            VibeChip("Melody", "🎶")
        )
    }

    val continueItems = remember {
        listOf(
            TrackPreviewItem("1", "Munbe Vaa", "A.R. Rahman", "💿"),
            TrackPreviewItem("2", "Katchi Sera", "Sai Abhyankkar", "🎧"),
            TrackPreviewItem("3", "Nira", "Sid Sriram", "🌌"),
            TrackPreviewItem("4", "Vathi Coming", "Anirudh Ravichander", "⚡")
        )
    }

    val friendItems = remember {
        listOf(
            FriendStatusItem("Ravi", "Listening to A.R. Rahman", NotuneRed),
            FriendStatusItem("Arun", "In Midnight Drive Room 🔴", NotuneDeepViolet),
            FriendStatusItem("Kavi", "Listening to Sid Sriram", NotuneElectricCyan)
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NotuneDarkUniverse)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 90.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    NØSoundMark(
                        state = if (isPlaying) SoundMarkState.PLAYING else SoundMarkState.PAUSED,
                        textSize = 26.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "V3",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NotuneRed,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(NotuneRed.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                // AI Sparkle Trigger Button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(NotuneRed, NotuneDeepViolet)
                            )
                        )
                        .clickable { onOpenAI() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "✦", fontSize = 20.sp, color = Color.White)
                }
            }

            // Greeting & Vibe Intro
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = "Good evening",
                    fontSize = 14.sp,
                    color = NotuneTextSecondary
                )
                Text(
                    text = "What's your vibe right now?",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = NotuneTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Vibe Selector Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(vibes) { vibe ->
                    val isSelected = vibe.label == selectedVibe
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) NotuneRed else NotuneSurfaceDark,
                        modifier = Modifier.clickable {
                            selectedVibe = if (isSelected) null else vibe.label
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = vibe.emoji, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = vibe.label,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else NotuneTextPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // HERO CARD: NØ FLOW ▶
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = NotuneSurfaceDark)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    NotuneRed.copy(alpha = 0.35f),
                                    NotuneDeepViolet.copy(alpha = 0.15f),
                                    Color.Transparent
                                ),
                                radius = 600f
                            )
                        )
                        .padding(22.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "YOUR FLOW",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = NotuneRed,
                                letterSpacing = 1.5.sp
                            )
                            Text(
                                text = "ADAPTIVE OS",
                                fontSize = 10.sp,
                                color = NotuneTextSecondary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "NØTUNE understands what you want next.",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = NotuneTextPrimary
                        )
                        Text(
                            text = if (selectedVibe != null) "Seeded with $selectedVibe vibe" else "Continuous context & taste engine",
                            fontSize = 12.sp,
                            color = NotuneTextSecondary
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { onLaunchFlow(selectedVibe) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = NotuneRed),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "PLAY FLOW",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Continue Listening Section
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = "Continue listening",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = NotuneTextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(continueItems) { track ->
                    Surface(
                        modifier = Modifier
                            .width(130.dp)
                            .clickable { onTrackSelect(track) },
                        shape = RoundedCornerShape(16.dp),
                        color = NotuneSurfaceDark
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(106.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(NotuneSurfaceVariantDark),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = track.emoji, fontSize = 40.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = track.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = NotuneTextPrimary,
                                maxLines = 1
                            )
                            Text(
                                text = track.artist,
                                fontSize = 11.sp,
                                color = NotuneTextSecondary,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Friends Listening Ticker
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = "Friends are listening",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = NotuneTextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))

                friendItems.forEach { friend ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(NotuneSurfaceDark)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(friend.avatarBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = friend.name.take(1),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = friend.name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = NotuneTextPrimary
                            )
                            Text(
                                text = friend.activity,
                                fontSize = 11.sp,
                                color = NotuneTextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}
