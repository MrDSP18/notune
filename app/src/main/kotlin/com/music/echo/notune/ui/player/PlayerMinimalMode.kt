package com.music.echo.notune.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import echo.music.iad1tya.ui.theme.NothingFont

@Composable
fun PlayerMinimalMode(
    songTitle: String = "Husn",
    artistName: String = "Anuv Jain",
    isPlaying: Boolean = false,
    onPlayPause: () -> Unit = {},
    onExitMinimal: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(32.dp)
    ) {
        // Exit Minimal Button
        IconButton(
            onClick = onExitMinimal,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Text(
                text = "[ EXIT MINIMAL ]",
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont),
                color = Color(0xFF71717A)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = songTitle,
                style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = artistName,
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFFA1A1AA)
            )

            Spacer(modifier = Modifier.height(60.dp))

            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable { onPlayPause() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.Black,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}
