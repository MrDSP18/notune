package com.music.echo.ui.player.lux

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.C
import androidx.media3.common.Player
import coil3.compose.AsyncImage
import echo.music.iad1tya.R
import echo.music.iad1tya.models.MediaMetadata
import echo.music.iad1tya.utils.makeTimeString
import kotlin.math.min

/**
 * luxTune Music Player UI - High-Fidelity Replication
 *
 * Implements the editorial, warm-monochrome visual identity from the reference image:
 * - Serene ivory/stone gradient background with topographic contour curves
 * - Organic asymmetric pebble artwork with concentric contour aura and anchor node
 * - Clean uppercase typography hierarchy
 * - Minimalist timeline and solid charcoal circular play/pause button
 * - Frosted-glass bottom action dock (Lyrics, Queue, More)
 * - Integrated floating lyrics sheet matching Screen 3 of the reference
 */
@Composable
fun LuxPlayerContent(
    mediaMetadata: MediaMetadata,
    position: Long,
    duration: Long,
    isPlaying: Boolean,
    isLiked: Boolean,
    shuffleModeEnabled: Boolean,
    repeatMode: Int,
    bluetoothDeviceName: String? = null,
    lyricsLines: List<String> = emptyList(),
    currentLyricIndex: Int = 0,
    onPlayPauseClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onSeek: (Long) -> Unit,
    onCollapseClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onShuffleClick: () -> Unit,
    onRepeatClick: () -> Unit,
    onLyricsClick: () -> Unit,
    onQueueClick: () -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isSeeking by remember { mutableStateOf(false) }
    var seekPosition by remember { mutableFloatStateOf(0f) }
    var showFloatingLyrics by remember { mutableStateOf(false) }

    val currentPosition = if (isSeeking) seekPosition.toLong() else position
    val progressFraction = if (duration > 0) (currentPosition.toFloat() / duration).coerceIn(0f, 1f) else 0f

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        LuxDesignTokens.BackgroundGradientTop,
                        LuxDesignTokens.BackgroundGradientBottom
                    )
                )
            )
            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top + WindowInsetsSides.Bottom))
    ) {
        val totalHeight = maxHeight
        val totalWidth = maxWidth
        val isCompact = totalHeight < 680.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // ==========================================
            // 1. TOP BAR
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = if (isCompact) 8.dp else 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Collapse Button (v)
                FrostedCircleButton(
                    iconRes = R.drawable.expand_more,
                    contentDescription = "Collapse Player",
                    onClick = onCollapseClick,
                    size = if (isCompact) 40.dp else 46.dp
                )

                // Current Track Header & Elapsed Timestamps
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "CURRENT TRACK",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 2.sp,
                        color = LuxDesignTokens.TextMuted
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = "${makeTimeString(position)} | ${if (duration != C.TIME_UNSET) makeTimeString(duration) else "--:--"}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp,
                        color = LuxDesignTokens.TextMuted
                    )
                }

                // Favorite / Heart Button
                FrostedCircleButton(
                    iconRes = if (isLiked) R.drawable.favorite else R.drawable.favorite_border,
                    contentDescription = if (isLiked) "Remove from favorites" else "Add to favorites",
                    onClick = onFavoriteClick,
                    tint = if (isLiked) LuxDesignTokens.Charcoal else LuxDesignTokens.Charcoal,
                    size = if (isCompact) 40.dp else 46.dp
                )
            }

            Spacer(Modifier.height(if (isCompact) 8.dp else 16.dp))

            // ==========================================
            // 2. CENTRAL ORGANIC PEBBLE ALBUM ARTWORK
            // ==========================================
            val artworkBoxSize = min(
                if (isCompact) totalHeight * 0.38f else totalHeight * 0.42f,
                totalWidth * 0.78f
            )

            Box(
                modifier = Modifier
                    .size(artworkBoxSize)
                    .graphicsLayer { clip = false },
                contentAlignment = Alignment.Center
            ) {
                // Background Topographic Curves, Outer Contour Aura & Anchor Node
                Canvas(modifier = Modifier.fillMaxSize()) {
                    OrganicPebbleShape.drawArtworkAuraAndContours(
                        drawScope = this,
                        boxWidth = size.width,
                        boxHeight = size.height,
                        contourStrokeWidth = 3.2f,
                        auraScale = 1.15f,
                        drawBackgroundTopography = true
                    )
                }

                // Clipped Pebble Artwork
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.86f)
                        .shadow(
                            elevation = 8.dp,
                            shape = OrganicPebbleShape(1f),
                            spotColor = Color(0x33000000),
                            ambientColor = Color(0x22000000)
                        )
                        .clip(OrganicPebbleShape(1f))
                        .background(LuxDesignTokens.Contour)
                ) {
                    AsyncImage(
                        model = mediaMetadata.thumbnailUrl,
                        contentDescription = mediaMetadata.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(Modifier.height(if (isCompact) 10.dp else 20.dp))

            // ==========================================
            // 3. TRACK METADATA (Title & Artist)
            // ==========================================
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = mediaMetadata.title.uppercase(),
                    fontSize = if (isCompact) 19.sp else 22.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    color = LuxDesignTokens.TextPrimary,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .basicMarquee(iterations = Int.MAX_VALUE)
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = mediaMetadata.artist.uppercase(),
                    fontSize = if (isCompact) 11.5.sp else 12.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 2.sp,
                    color = LuxDesignTokens.TextMuted,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(0.85f)
                )
            }

            Spacer(Modifier.height(if (isCompact) 10.dp else 20.dp))

            // ==========================================
            // 4. TIMELINE & SEEK BAR
            // ==========================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            ) {
                // Timestamps Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = makeTimeString(currentPosition),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal,
                        color = LuxDesignTokens.TextMuted
                    )
                    Text(
                        text = if (duration != C.TIME_UNSET) makeTimeString(duration) else "--:--",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal,
                        color = LuxDesignTokens.TextMuted
                    )
                }

                Spacer(Modifier.height(4.dp))

                // Custom Sleek Slider
                Slider(
                    value = progressFraction,
                    onValueChange = { fraction ->
                        isSeeking = true
                        seekPosition = fraction * (if (duration != C.TIME_UNSET) duration else 0L)
                    },
                    onValueChangeFinished = {
                        onSeek(seekPosition.toLong())
                        isSeeking = false
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = LuxDesignTokens.Charcoal,
                        activeTrackColor = LuxDesignTokens.TrackActive,
                        inactiveTrackColor = LuxDesignTokens.TrackInactive,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(28.dp)
                )
            }

            Spacer(Modifier.height(if (isCompact) 6.dp else 16.dp))

            // ==========================================
            // 5. PRIMARY PLAYBACK CONTROLS
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shuffle
                IconButtonBare(
                    iconRes = if (shuffleModeEnabled) R.drawable.shuffle_on else R.drawable.shuffle,
                    contentDescription = "Shuffle",
                    tint = if (shuffleModeEnabled) LuxDesignTokens.Charcoal else LuxDesignTokens.TextMuted,
                    iconSize = 22.dp,
                    onClick = onShuffleClick
                )

                // Previous
                IconButtonBare(
                    iconRes = R.drawable.skip_previous,
                    contentDescription = "Previous Track",
                    tint = LuxDesignTokens.Charcoal,
                    iconSize = 28.dp,
                    onClick = onPreviousClick
                )

                // Play / Pause (Solid Charcoal Circle Button)
                PlayPauseCircularButton(
                    isPlaying = isPlaying,
                    onClick = onPlayPauseClick,
                    size = if (isCompact) 60.dp else 68.dp
                )

                // Next
                IconButtonBare(
                    iconRes = R.drawable.skip_next,
                    contentDescription = "Next Track",
                    tint = LuxDesignTokens.Charcoal,
                    iconSize = 28.dp,
                    onClick = onNextClick
                )

                // Repeat
                IconButtonBare(
                    iconRes = when (repeatMode) {
                        Player.REPEAT_MODE_ONE -> R.drawable.repeat_one
                        Player.REPEAT_MODE_ALL -> R.drawable.repeat
                        else -> R.drawable.repeat
                    },
                    contentDescription = "Repeat",
                    tint = if (repeatMode != Player.REPEAT_MODE_OFF) LuxDesignTokens.Charcoal else LuxDesignTokens.TextMuted,
                    iconSize = 22.dp,
                    onClick = onRepeatClick
                )
            }

            Spacer(Modifier.height(if (isCompact) 10.dp else 22.dp))

            // ==========================================
            // 6. BOTTOM ACTION DOCK (Lyrics, Queue, More)
            // ==========================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isCompact) 62.dp else 70.dp)
                    .clip(RoundedCornerShape(35.dp))
                    .background(LuxDesignTokens.FrostedSurface)
                    .border(1.dp, LuxDesignTokens.FrostedBorder, RoundedCornerShape(35.dp))
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DockActionItem(
                        iconRes = R.drawable.lyrics,
                        label = "Lyrics",
                        isActive = showFloatingLyrics,
                        onClick = {
                            showFloatingLyrics = !showFloatingLyrics
                            onLyricsClick()
                        }
                    )

                    DockActionItem(
                        iconRes = R.drawable.queue_music,
                        label = "Queue",
                        isActive = false,
                        onClick = onQueueClick
                    )

                    DockActionItem(
                        iconRes = R.drawable.more_horiz,
                        label = "More",
                        isActive = false,
                        onClick = onMoreClick
                    )
                }
            }

            Spacer(Modifier.height(if (isCompact) 8.dp else 14.dp))
        }

        // ==========================================
        // 7. FLOATING LYRICS SHEET (Matching Screen 3)
        // ==========================================
        AnimatedVisibility(
            visible = showFloatingLyrics,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            FloatingLyricsCard(
                lyricsLines = lyricsLines,
                currentLyricIndex = currentLyricIndex,
                onClose = { showFloatingLyrics = false }
            )
        }
    }
}

/**
 * Circular frosted button used for Collapse and Favorite actions in the top bar.
 */
@Composable
private fun FrostedCircleButton(
    iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
    size: androidx.compose.ui.unit.Dp = 46.dp,
    tint: Color = LuxDesignTokens.Charcoal
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(stiffness = 500f),
        label = "buttonScale"
    )

    Box(
        modifier = Modifier
            .size(size)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(CircleShape)
            .background(if (isPressed) LuxDesignTokens.FrostedSurfacePressed else LuxDesignTokens.FrostedSurface)
            .border(1.dp, LuxDesignTokens.FrostedBorder, CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )
    }
}

/**
 * Play/Pause prominent solid dark charcoal button.
 */
@Composable
private fun PlayPauseCircularButton(
    isPlaying: Boolean,
    onClick: () -> Unit,
    size: androidx.compose.ui.unit.Dp = 68.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1f,
        animationSpec = spring(stiffness = 600f),
        label = "playScale"
    )

    Box(
        modifier = Modifier
            .size(size)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .shadow(
                elevation = 8.dp,
                shape = CircleShape,
                spotColor = Color(0x40000000),
                ambientColor = Color(0x20000000)
            )
            .clip(CircleShape)
            .background(if (isPressed) LuxDesignTokens.CharcoalPressed else LuxDesignTokens.CharcoalButton)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(if (isPlaying) R.drawable.pause else R.drawable.play),
            contentDescription = if (isPlaying) "Pause" else "Play",
            tint = LuxDesignTokens.White,
            modifier = Modifier.size(28.dp)
        )
    }
}

/**
 * Clean bare icon button for Shuffle, Previous, Next, Repeat.
 */
@Composable
private fun IconButtonBare(
    iconRes: Int,
    contentDescription: String,
    tint: Color,
    iconSize: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        animationSpec = spring(stiffness = 600f),
        label = "bareScale"
    )

    Box(
        modifier = Modifier
            .size(48.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(iconSize)
        )
    }
}

/**
 * Individual action in the bottom frosted dock (Lyrics, Queue, More).
 */
@Composable
private fun DockActionItem(
    iconRes: Int,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1f,
        label = "dockScale"
    )

    val contentColor = if (isActive) LuxDesignTokens.Charcoal else LuxDesignTokens.TextMuted

    Column(
        modifier = Modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.height(3.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Medium,
            color = contentColor
        )
    }
}

/**
 * Floating lyrics sheet replicating Screen 3 in the reference image.
 */
@Composable
private fun FloatingLyricsCard(
    lyricsLines: List<String>,
    currentLyricIndex: Int,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(290.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(12.dp, RoundedCornerShape(28.dp), spotColor = Color(0x33000000))
            .clip(RoundedCornerShape(28.dp))
            .background(LuxDesignTokens.FrostedSurface)
            .border(1.dp, LuxDesignTokens.FrostedBorder, RoundedCornerShape(28.dp))
            .padding(20.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header with Close X
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Lyrics",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = LuxDesignTokens.Charcoal
                )

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0x12000000))
                        .clickable(onClick = onClose),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.close),
                        contentDescription = "Close Lyrics",
                        tint = LuxDesignTokens.Charcoal,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // Lyrics Content
            if (lyricsLines.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No synced lyrics available",
                        fontSize = 14.sp,
                        color = LuxDesignTokens.TextMuted
                    )
                }
            } else {
                val listState = rememberLazyListState()
                LaunchedEffect(currentLyricIndex) {
                    if (currentLyricIndex in lyricsLines.indices) {
                        listState.animateScrollToItem(
                            (currentLyricIndex - 1).coerceAtLeast(0)
                        )
                    }
                }

                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    itemsIndexed(lyricsLines) { index, line ->
                        val isActive = index == currentLyricIndex
                        Text(
                            text = line,
                            fontSize = if (isActive) 18.sp else 15.sp,
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                            color = if (isActive) LuxDesignTokens.Charcoal else LuxDesignTokens.TextSecondary,
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }
    }
}
