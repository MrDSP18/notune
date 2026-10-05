package com.music.echo.notune.ui.special

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.music.echo.notune.design.theme.NotuneTextPrimary
import com.music.echo.notune.design.theme.NotuneTextSecondary

@Composable
fun NotuneDriveScreenV3(
    title: String,
    artist: String,
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onVoiceCommand: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "NØ DRIVE MODE",
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = NotuneRed,
                letterSpacing = 2.sp
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = title,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    maxLines = 2
                )
                Text(
                    text = artist,
                    fontSize = 18.sp,
                    color = NotuneTextSecondary,
                    maxLines = 1
                )
            }

            // High Contrast Voice Command Button
            Button(
                onClick = onVoiceCommand,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NotuneRed),
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(imageVector = Icons.Default.Mic, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "TAP TO SPEAK", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)
            }

            // Massive Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(56.dp)
                        .clickable { onSkipPrevious() }
                )

                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(NotuneRed)
                        .clickable { onPlayPauseClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(56.dp)
                        .clickable { onSkipNext() }
                )
            }
        }
    }
}

@Composable
fun NotuneSleepScreenV3(
    onStartSleepTimer: (minutes: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMinutes by remember { mutableStateOf(30) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NotuneDarkUniverse)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = NotuneSurfaceDark,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "🌙 NØ SLEEP FLOW", fontSize = 18.sp, fontWeight = FontWeight.Black, color = NotuneTextPrimary)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "Gradual energy decay sleep timer", fontSize = 12.sp, color = NotuneTextSecondary)
                Spacer(modifier = Modifier.height(20.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(15, 30, 45, 60).forEach { mins ->
                        val isSelected = mins == selectedMinutes
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) NotuneRed else NotuneDarkUniverse,
                            modifier = Modifier.clickable { selectedMinutes = mins }
                        ) {
                            Text(
                                text = "${mins}m",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else NotuneTextPrimary,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { onStartSleepTimer(selectedMinutes) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = NotuneRed),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(text = "START SLEEP FLOW", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color.White)
                }
            }
        }
    }
}
