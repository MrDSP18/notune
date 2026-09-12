package com.music.echo.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DriveModeScreen(
    songTitle: String,
    artistName: String,
    isPlaying: Boolean,
    onPlayPauseToggle: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onExitDriveMode: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp)
    ) {
        // Exit Drive Mode Button Top Left
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .background(Color(0xFF222222), RoundedCornerShape(12.dp))
                .clickable { onExitDriveMode() }
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(text = "✕ EXIT DRIVE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        // Center Now Playing Banner
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🚗 DRIVE MODE",
                color = Color(0xFFFF0055),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = songTitle,
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = artistName,
                color = Color.Gray,
                fontSize = 22.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Large Touch Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Previous
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color(0xFF222222), CircleShape)
                        .clickable { onPrevious() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "⏮", color = Color.White, fontSize = 32.sp)
                }

                // Play / Pause
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .background(Color(0xFFFF0055), CircleShape)
                        .clickable { onPlayPauseToggle() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = if (isPlaying) "⏸" else "▶", color = Color.White, fontSize = 44.sp)
                }

                // Next
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color(0xFF222222), CircleShape)
                        .clickable { onNext() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "⏭", color = Color.White, fontSize = 32.sp)
                }
            }
        }
    }
}
