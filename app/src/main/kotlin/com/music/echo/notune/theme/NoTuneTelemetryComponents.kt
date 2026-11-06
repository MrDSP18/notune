package com.music.echo.notune.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import echo.music.iad1tya.ui.theme.NothingFont

val NothingRed = Color(0xFFFF0031)
val TelemetryZinc = Color(0xFF71717A)
val DeepZinc = Color(0xFF18181B)

@Composable
fun NoTuneTelemetryHeader(
    section: String,
    status: String = "ACTIVE",
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(NothingRed)
            )
            Text(
                text = "${section.uppercase()} // ${status.uppercase()}",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = NothingFont,
                    color = NothingRed,
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
        
        Text(
            text = "SYS_NODE: 0x${section.hashCode().toString(16).takeLast(2).uppercase()}",
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = NothingFont,
                color = TelemetryZinc,
                letterSpacing = 1.sp
            )
        )
    }
}

@Composable
fun AudioResolutionBadge(
    format: String? = null,
    modifier: Modifier = Modifier
) {
    val displayFormat = format ?: "STANDARD // 44.1kHz"
    
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(2.dp))
            .background(DeepZinc)
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = displayFormat.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = NothingFont,
                color = Color(0xFF69D6E2), // Tertiary color from design
                fontSize = 9.sp,
                letterSpacing = 1.sp
            )
        )
    }
}

@Composable
fun SystemTelemetryHeader(
    title: String,
    subtitle: String,
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
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val infiniteTransition = rememberInfiniteTransition(label = "telemetry_ping")
                val alpha by infiniteTransition.animateFloat(
                    initialValue = 0.4f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(800),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "alpha"
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(NothingRed.copy(alpha = alpha))
                )
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = NothingFont,
                        color = Color.White,
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            
            Text(
                text = "SYS_READY",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = NothingFont,
                    color = Color(0xFF69D6E2),
                    letterSpacing = 1.sp
                )
            )
        }
        
        Spacer(Modifier.height(2.dp))
        
        Text(
            text = subtitle.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = NothingFont,
                color = TelemetryZinc,
                letterSpacing = 1.sp,
                fontSize = 10.sp
            ),
            modifier = Modifier.padding(start = 16.dp)
        )
        
        Spacer(Modifier.height(8.dp))
        HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
    }
}

@Composable
fun VibeRadarCard(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "vibe_radar")
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.03f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF27272A))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "[PROCEDURAL AUDIO SPECTRUM]",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = NothingFont,
                        color = NothingRed,
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "SYNC_ACTIVE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = NothingFont,
                        color = Color(0xFF69D6E2),
                        fontSize = 9.sp
                    )
                )
            }
            
            Spacer(Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth().height(48.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                val heights = listOf(0.4f, 0.8f, 0.5f, 0.9f, 0.3f, 0.7f, 0.5f, 1.0f, 0.4f, 0.6f, 0.8f, 0.3f, 0.7f, 0.9f, 0.4f, 0.5f)
                
                heights.forEachIndexed { index, baseH ->
                    val heightFactor by infiniteTransition.animateFloat(
                        initialValue = baseH * 0.5f,
                        targetValue = baseH,
                        animationSpec = infiniteRepeatable(
                            animation = tween(durationMillis = 400 + (index * 100), easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "bar_$index"
                    )
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(heightFactor)
                            .background(
                                Brush.verticalGradient(
                                    listOf(NothingRed, NothingRed.copy(alpha = 0.3f))
                                )
                            )
                    )
                }
            }
            
            Spacer(Modifier.height(8.dp))
            
            Text(
                text = "REAL-TIME ACOUSTIC ANALYSIS // NODE_STABLE",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = NothingFont,
                    color = TelemetryZinc,
                    fontSize = 8.sp,
                    letterSpacing = 1.sp
                )
            )
        }
    }
}

@Composable
fun NoTuneQuickActionMatrix(
    onActionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val actions = listOf(
        QuickActionItem("NEURAL_AGENT", "Ask NØTUNE", echo.music.iad1tya.R.drawable.ic_echo_brain),
        QuickActionItem("SYNC_ROOM", "Listen Together", echo.music.iad1tya.R.drawable.radio),
        QuickActionItem("DNA_CHECK", "Vibe Check", echo.music.iad1tya.R.drawable.biotech),
        QuickActionItem("FLOW_OS", "Toggle Flow", echo.music.iad1tya.R.drawable.tune),
        QuickActionItem("PLAYLIST_LAB", "Playlist Lab", echo.music.iad1tya.R.drawable.sparks),
        QuickActionItem("SYSTEM_CORE", "Settings", echo.music.iad1tya.R.drawable.settings)
    )

    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Text(
            text = "COMMAND_MATRIX // ACCESSIBLE_NODES",
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = NothingFont,
                color = TelemetryZinc,
                letterSpacing = 1.sp,
                fontSize = 10.sp
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        echo.music.iad1tya.ui.component.ChipsRow(
            chips = actions.map { it.id to it.label },
            currentValue = null,
            onValueUpdate = { id -> onActionClick(id ?: "") }
        )
        
        Spacer(Modifier.height(8.dp))
        
        // High visibility grid for primary features
        Row(
            modifier = Modifier.fillMaxWidth().height(100.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MatrixTile(
                item = actions[0],
                onClick = { onActionClick(actions[0].id) },
                modifier = Modifier.weight(1f),
                color = NothingRed
            )
            MatrixTile(
                item = actions[1],
                onClick = { onActionClick(actions[1].id) },
                modifier = Modifier.weight(1f),
                color = Color(0xFF69D6E2)
            )
        }
    }
}

private data class QuickActionItem(val id: String, val label: String, val icon: Int)

@Composable
private fun MatrixTile(
    item: QuickActionItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Color.White
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxHeight(),
        shape = RoundedCornerShape(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            Icon(
                painter = painterResource(item.icon),
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp).align(Alignment.TopStart)
            )
            
            Column(modifier = Modifier.align(Alignment.BottomStart)) {
                Text(
                    text = item.id,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = NothingFont,
                        color = color.copy(alpha = 0.7f),
                        fontSize = 8.sp
                    )
                )
                Text(
                    text = item.label.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = NothingFont,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
            }
        }
    }
}
