package com.music.echo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import echo.music.iad1tya.models.MediaMetadata

@Composable
fun SongBattleScreen(
    songA: MediaMetadata?,
    songB: MediaMetadata?,
    onVote: (winningSongId: String) -> Unit,
    onBack: () -> Unit
) {
    var winnerId by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0C0C0C))
            .padding(24.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "←",
                    color = Color.White,
                    fontSize = 24.sp,
                    modifier = Modifier.clickable { onBack() }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "⚔ SONG BATTLE",
                color = Color(0xFFFF0055),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (songA != null && songB != null) {
                // Song A Card
                BattleCard(
                    songTitle = songA.title,
                    artistName = songA.artists.joinToString { it.name }.ifEmpty { "Unknown Artist" },
                    isSelected = winnerId == songA.id,
                    onSelect = {
                        winnerId = songA.id
                        onVote(songA.id)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "VS",
                    color = Color.Gray,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Song B Card
                BattleCard(
                    songTitle = songB.title,
                    artistName = songB.artists.joinToString { it.name }.ifEmpty { "Unknown Artist" },
                    isSelected = winnerId == songB.id,
                    onSelect = {
                        winnerId = songB.id
                        onVote(songB.id)
                    }
                )
            } else {
                Text(
                    text = "Add songs to start a Song Battle!",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun BattleCard(
    songTitle: String,
    artistName: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .background(
                if (isSelected) Color(0xFF2A0010) else Color(0xFF181818),
                RoundedCornerShape(20.dp)
            )
            .border(
                2.dp,
                if (isSelected) Color(0xFFFF0055) else Color(0xFF333333),
                RoundedCornerShape(20.dp)
            )
            .clickable { onSelect() }
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = songTitle,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = artistName,
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
