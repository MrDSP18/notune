package com.music.echo.ui.player

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.music.echo.notune.theme.AudioResolutionBadge
import com.music.echo.notune.theme.NoTuneLogo
import com.music.echo.notune.theme.NoTuneTelemetryHeader
import com.music.echo.notune.theme.NothingRed
import echo.music.iad1tya.models.MediaMetadata
import echo.music.iad1tya.models.TechnicalTelemetry
import echo.music.iad1tya.models.PlaybackState
import echo.music.iad1tya.R
import echo.music.iad1tya.ui.theme.NothingFont
import echo.music.iad1tya.notune.audio.ProceduralVisualizerEngine
import echo.music.iad1tya.utils.rememberEnumPreference
import echo.music.iad1tya.constants.AnimationLevelKey
import echo.music.iad1tya.constants.AnimationLevel

@Composable
fun StitchPlayerContent(
    playbackState: PlaybackState,
    onPlayPauseClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onSeek: (Long) -> Unit,
    onLyricsClick: () -> Unit = {},
    onQueueClick: () -> Unit = {},
    onEqClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val mediaMetadata = playbackState.currentSong ?: return
    val telemetry = playbackState.telemetry
    val isPlaying = playbackState.isPlaying
    val position = playbackState.position
    val duration = playbackState.duration

    val animationLevel by rememberEnumPreference(AnimationLevelKey, AnimationLevel.FULL)
    val visualizerEngine = remember { ProceduralVisualizerEngine() }
    val waveformFrame = visualizerEngine.rememberWaveformFrame(playbackState, animationLevel)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF131313))
            .padding(horizontal = 24.dp)
            .padding(top = 16.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Top Telemetry Bar
        NoTuneTelemetryHeader(section = "AUDIO_ENGINE", status = telemetry.bufferState)
        
        Spacer(Modifier.height(24.dp))

        // Technical Album Art Container with Waveform
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.2f)
                .border(1.dp, Color(0xFF27272A), RoundedCornerShape(4.dp))
                .padding(12.dp)
        ) {
            AsyncImage(
                model = mediaMetadata.thumbnailUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(2.dp)),
                contentScale = ContentScale.Crop,
                alpha = 0.4f // Dimmed for waveform visibility
            )
            
            // Procedural Waveform Overlay
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val barWidth = width / waveformFrame.size
                
                waveformFrame.forEachIndexed { i, bar ->
                    val barHeight = height * 0.6f * bar.heightFactor
                    drawRect(
                        color = NothingRed.copy(alpha = bar.opacity),
                        topLeft = Offset(i * barWidth + 2f, height / 2 - barHeight / 2),
                        size = androidx.compose.ui.geometry.Size(barWidth - 4f, barHeight)
                    )
                }
            }
            
            // Truthful Audio Labels
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    AudioResolutionBadge(format = telemetry.codec)
                    telemetry.sampleRate?.let { sampleRate ->
                         BadgeNode(text = "${sampleRate / 1000f}kHz")
                    }
                }
            }
            
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "VISUALIZER: PROCEDURAL",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = NothingFont,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 7.sp,
                        letterSpacing = 1.sp
                    )
                )
            }

            // Buffer Progress Overlay
            if (telemetry.bufferPercent < 100) {
                LinearProgressIndicator(
                    progress = { telemetry.bufferPercent / 100f },
                    modifier = Modifier.fillMaxWidth().height(1.dp).align(Alignment.BottomCenter),
                    color = NothingRed.copy(alpha = 0.5f),
                    trackColor = Color.Transparent
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Metadata section
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            playbackState.aiDjCommentary?.let { commentary ->
                Text(
                    text = commentary.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = NothingFont,
                        color = NothingRed,
                        fontSize = 10.sp,
                        letterSpacing = 1.sp
                    ),
                    modifier = Modifier.padding(bottom = 4.dp).basicMarquee()
                )
            }

            Text(
                text = mediaMetadata.title.uppercase(),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = NothingFont,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = Color.White
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(Modifier.height(4.dp))
            
            Text(
                text = mediaMetadata.artists.joinToString { it.name }.uppercase(),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = NothingFont,
                    color = NothingRed,
                    letterSpacing = 1.5.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(Modifier.height(32.dp))

        // Seek Bar (Industrial Style)
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatDuration(position),
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont, color = Color.White.copy(alpha = 0.5f))
                )
                Text(
                    text = formatDuration(duration),
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont, color = Color.White.copy(alpha = 0.5f))
                )
            }
            
            Slider(
                value = if (duration > 0) position.toFloat() / duration else 0f,
                onValueChange = { onSeek((it * duration).toLong()) },
                colors = SliderDefaults.colors(
                    thumbColor = NothingRed,
                    activeTrackColor = NothingRed,
                    inactiveTrackColor = Color(0xFF27272A)
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(16.dp))

        // Up Next Track Preview Card
        val nextSong = playbackState.queue.getOrNull(playbackState.queueIndex + 1)
        if (nextSong != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                    .clickable { onNextClick() }
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AsyncImage(
                    model = nextSong.thumbnailUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "UP NEXT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = NothingFont,
                            color = NothingRed,
                            fontSize = 8.sp,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = nextSong.title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = nextSong.artists.joinToString { it.name },
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.6f)
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Icon(
                    painter = painterResource(R.drawable.skip_next),
                    contentDescription = "Skip to Next",
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(Modifier.weight(1f))

        // Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPreviousClick) {
                Icon(
                    painter = painterResource(R.drawable.skip_previous),
                    contentDescription = "Previous Song",
                    modifier = Modifier.size(36.dp),
                    tint = Color.White
                )
            }

            Box(
                modifier = Modifier
                    .size(76.dp)
                    .border(2.dp, NothingRed, RoundedCornerShape(38.dp))
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onPlayPauseClick,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(NothingRed, RoundedCornerShape(38.dp))
                ) {
                    Icon(
                        painter = painterResource(if (isPlaying) R.drawable.pause else R.drawable.play),
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        modifier = Modifier.size(36.dp),
                        tint = Color.White
                    )
                }
            }

            IconButton(onClick = onNextClick) {
                Icon(
                    painter = painterResource(R.drawable.skip_next),
                    contentDescription = "Next Song",
                    modifier = Modifier.size(36.dp),
                    tint = Color.White
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        // Technical Log / Telemetry Data (Truthful)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.02f), RoundedCornerShape(2.dp))
                .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(2.dp))
                .padding(12.dp)
        ) {
            TechnicalLogItem(label = "BITRATE", value = telemetry.bitrate?.let { "${it / 1000} KBPS" } ?: "UNKNOWN")
            TechnicalLogItem(label = "SAMPLING", value = telemetry.sampleRate?.let { "${it / 1000f} KHZ" } ?: "UNKNOWN")
            TechnicalLogItem(label = "CHANNELS", value = telemetry.channelCount?.toString() ?: "UNKNOWN")
            TechnicalLogItem(label = "OUTPUT", value = (playbackState.audioDevice ?: "INTERNAL_SPEAKER").uppercase())
            TechnicalLogItem(label = "IO_SYNC", value = telemetry.syncDrift)
        }

        Spacer(Modifier.height(32.dp))

        // Technical Utility Bar (High Accessibility)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            UtilityNode(label = "LYRICS", icon = R.drawable.lyrics, onClick = onLyricsClick)
            UtilityNode(label = "QUEUE", icon = R.drawable.queue_music, onClick = onQueueClick)
            UtilityNode(label = "EQ", icon = R.drawable.tune, onClick = onEqClick)
            UtilityNode(label = "SHARE", icon = R.drawable.share, onClick = onShareClick)
        }

        Spacer(Modifier.height(24.dp))

        // Brand Footer
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            NoTuneLogo(size = 16.dp)
            Text(
                text = "NØTUNE // AUDIO_OS",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = NothingFont,
                    color = Color.White.copy(alpha = 0.3f),
                    letterSpacing = 2.sp
                )
            )
        }
    }
}

@Composable
private fun BadgeNode(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(2.dp))
            .background(Color(0xFF18181B))
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = NothingFont,
                color = Color(0xFF69D6E2),
                fontSize = 8.sp,
                letterSpacing = 1.sp
            )
        )
    }
}

@Composable
private fun UtilityNode(
    label: String,
    icon: Int,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(2.dp))
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = Color.White.copy(alpha = 0.6f)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = NothingFont,
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 8.sp,
                letterSpacing = 1.sp
            )
        )
    }
}

@Composable
private fun TechnicalLogItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = NothingFont,
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 8.sp,
                letterSpacing = 1.sp
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = NothingFont,
                color = Color(0xFF69D6E2),
                fontSize = 8.sp,
                letterSpacing = 1.sp
            )
        )
    }
}

private fun formatDuration(millis: Long): String {
    val seconds = (millis / 1000) % 60
    val minutes = (millis / (1000 * 60)) % 60
    return String.format(java.util.Locale.US, "%02d:%02d", minutes, seconds)
}
