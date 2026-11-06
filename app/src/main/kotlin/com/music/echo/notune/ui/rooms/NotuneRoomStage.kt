package com.music.echo.notune.ui.rooms

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import echo.music.iad1tya.ui.theme.NothingFont

data class RoomMember(val name: String, val isHost: Boolean = false)

@Composable
fun NotuneRoomStage(
    roomTitle: String = "NIGHT DRIVE CHILL",
    hostName: String = "DSP",
    members: List<RoomMember> = listOf(
        RoomMember("DSP", true),
        RoomMember("Arun", false),
        RoomMember("Karthi", false),
        RoomMember("Priya", false)
    ),
    currentSong: String = "Husn - Anuv Jain",
    modifier: Modifier = Modifier
) {
    val reactions = listOf("🔥", "❤️", "🎵", "👏", "🚀", "✨")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Live Badge & Room Name
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "LIVE ROOM",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = NothingFont,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.Red
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF27272A)
                ) {
                    Text(
                        text = "${members.size} LISTENERS",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont),
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = roomTitle,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Active Members Avatars
            Text(
                text = "LISTENERS IN ROOM",
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont),
                color = Color(0xFF71717A)
            )

            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(members) { member ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (member.isHost) MaterialTheme.colorScheme.primaryContainer else Color(0xFF27272A)
                    ) {
                        Text(
                            text = if (member.isHost) "👑 ${member.name} (DJ)" else "👤 ${member.name}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (member.isHost) MaterialTheme.colorScheme.onPrimaryContainer else Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Now Playing in Room
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF27272A))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ROOM PLAYBACK // SYNCED",
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = currentSong,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }

                    Button(
                        onClick = {},
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(text = "VOTE NEXT", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Reaction Bar
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(reactions) { reaction ->
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF27272A),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = reaction, fontSize = 18.sp)
                        }
                    }
                }
            }
        }
    }
}
