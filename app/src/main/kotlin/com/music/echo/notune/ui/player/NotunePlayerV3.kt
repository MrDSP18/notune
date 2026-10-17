package com.music.echo.notune.ui.player

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.music.echo.notune.design.components.NØSoundMark
import com.music.echo.notune.design.components.SoundMarkState
import com.music.echo.notune.design.theme.NotuneDarkUniverse
import com.music.echo.notune.design.theme.NotuneDeepViolet
import com.music.echo.notune.design.theme.NotuneRed
import com.music.echo.notune.design.theme.NotuneSurfaceDark
import com.music.echo.notune.design.theme.NotuneSurfaceVariantDark
import com.music.echo.notune.design.theme.NotuneTextPrimary
import com.music.echo.notune.design.theme.NotuneTextSecondary

enum class PlayerModeV3(val label: String) {
    MINIMAL("Minimal"),
    IMMERSIVE("Immersive"),
    LYRICS_3_0("Lyrics 3.0"),
    STUDIO("Studio"),
    SOCIAL("Social"),
    VISUALIZER("Visualizer")
}

@Composable
fun NotunePlayerV3(
    title: String,
    artist: String,
    isPlaying: Boolean,
    progress: Float,
    isFavorite: Boolean,
    onPlayPauseClick: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onOpenQueue: () -> Unit,
    onOpenAIActionWheel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeMode by remember { mutableStateOf(PlayerModeV3.MINIMAL) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NotuneDarkUniverse)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Header: Mode Switcher & Minimize
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NØSoundMark(
                    state = if (isPlaying) SoundMarkState.PLAYING else SoundMarkState.PAUSED,
                    textSize = 20.sp
                )

                // Mode Pills
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    PlayerModeV3.values().take(4).forEach { mode ->
                        val isSelected = mode == activeMode
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) NotuneRed else NotuneSurfaceDark,
                            modifier = Modifier.clickable { activeMode = mode }
                        ) {
                            Text(
                                text = mode.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else NotuneTextSecondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Main Mode Container
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Crossfade(targetState = activeMode, label = "modeFade") { mode ->
                    when (mode) {
                        PlayerModeV3.MINIMAL -> MinimalPlayerContent(
                            title = title,
                            artist = artist,
                            onLongPressArtwork = onOpenAIActionWheel,
                            onDoubleTapFavorite = onFavoriteToggle
                        )
                        PlayerModeV3.IMMERSIVE -> ImmersivePlayerContent(
                            title = title,
                            artist = artist
                        )
                        PlayerModeV3.LYRICS_3_0 -> LyricsPlayerContent(
                            title = title
                        )
                        PlayerModeV3.STUDIO -> StudioPlayerContent()
                        PlayerModeV3.SOCIAL -> SocialPlayerContent()
                        PlayerModeV3.VISUALIZER -> VisualizerPlayerContent()
                    }
                }
            }

            // Bottom Player Controls Surface
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                color = NotuneSurfaceDark
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Track Title & Favorite
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = title,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = NotuneTextPrimary,
                                maxLines = 1
                            )
                            Text(
                                text = artist,
                                fontSize = 13.sp,
                                color = NotuneTextSecondary,
                                maxLines = 1
                            )
                        }
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) NotuneRed else NotuneTextSecondary,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { onFavoriteToggle() }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Progress Slider
                    Slider(
                        value = progress,
                        onValueChange = {},
                        colors = SliderDefaults.colors(
                            thumbColor = NotuneRed,
                            activeTrackColor = NotuneRed,
                            inactiveTrackColor = NotuneSurfaceVariantDark
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Transport Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "Previous",
                            tint = NotuneTextPrimary,
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { onSkipPrevious() }
                        )

                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(NotuneRed)
                                .clickable { onPlayPauseClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play/Pause",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Next",
                            tint = NotuneTextPrimary,
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { onSkipNext() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MinimalPlayerContent(
    title: String,
    artist: String,
    onLongPressArtwork: () -> Unit,
    onDoubleTapFavorite: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    listOf(NotuneRed.copy(alpha = 0.25f), NotuneDeepViolet.copy(alpha = 0.15f))
                )
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { onDoubleTapFavorite() },
                    onLongPress = { onLongPressArtwork() }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(NotuneSurfaceVariantDark),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "💿", fontSize = 90.sp)
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Double-tap to like • Hold for AI Action Wheel",
                fontSize = 11.sp,
                color = NotuneTextSecondary
            )
        }
    }
}

@Composable
private fun ImmersivePlayerContent(title: String, artist: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(NotuneRed.copy(alpha = 0.45f), NotuneDeepViolet.copy(alpha = 0.2f), Color.Transparent),
                    radius = 700f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "KINETIC AMBIENT ARTWORK",
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )
    }
}

@Composable
private fun LyricsPlayerContent(title: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "கண்ணே கலைமானே", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = NotuneRed)
        Text(text = "Kanne Kalaimaane", fontSize = 16.sp, color = NotuneTextPrimary)
        Text(text = "My dear / my beautiful one", fontSize = 14.sp, color = NotuneTextSecondary)
    }
}

@Composable
private fun StudioPlayerContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "NØ STUDIO AUDIO PRESETS", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NotuneTextPrimary)
        Text(text = "Original • Bass Boost • Spatial 3D • Vocal", fontSize = 12.sp, color = NotuneTextSecondary)
    }
}

@Composable
private fun SocialPlayerContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "ROOM SOCIAL REACTIONS", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NotuneTextPrimary)
        Text(text = "❤️ 😂 🔥 😭 🕺", fontSize = 24.sp)
    }
}

@Composable
private fun VisualizerPlayerContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(24.dp))
            .background(NotuneSurfaceVariantDark),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "WINAMP / GEN-Z AUDIO SPECTRUM", fontSize = 13.sp, color = NotuneTextSecondary)
    }
}
