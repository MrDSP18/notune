package com.music.echo.notune.social.ui

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.music.echo.notune.social.dna.MusicDnaAnalyzer
import com.music.echo.notune.theme.NoTuneAmbientCanvas
import com.music.echo.notune.theme.NoTuneSurfaceCard
import echo.music.iad1tya.R
import echo.music.iad1tya.constants.CardStyleVariant

@Composable
fun CollaborativePlaylistScreen(
    playlistName: String = "Midnight Chill & Heartbreak Anthems",
    onDismiss: () -> Unit = {}
) {
    val context = LocalContext.current
    val dnaAnalyzer = remember { MusicDnaAnalyzer() }
    val dna = remember {
        dnaAnalyzer.analyzePlaylistDna(
            trackTitles = listOf("Until I Found You", "Kesariya", "Starboy", "Sad Night"),
            genres = listOf("Pop", "Indie", "R&B")
        )
    }

    var collaborators by remember { mutableStateOf(listOf("You (Owner)", "Aarav Sharma (Editor)", "Priya Ananth (Editor)")) }
    var tracks by remember {
        mutableStateOf(
            listOf(
                Triple("1", "Until I Found You", "Stephen Sanchez"),
                Triple("2", "Kesariya", "Arijit Singh"),
                Triple("3", "Starboy", "The Weeknd"),
                Triple("4", "Arabic Kuthu", "Anirudh Ravichander")
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        NoTuneAmbientCanvas(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                IconButton(onClick = onDismiss) {
                    Text("✕", fontSize = 20.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "COLLABORATIVE PLAYLIST",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.5.sp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(playlistName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Playlist DNA Card
                item {
                    NoTuneSurfaceCard(cardStyle = CardStyleVariant.GLASS, modifier = Modifier.fillMaxWidth()) {
                        Text("🧬 PLAYLIST MUSICAL DNA", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(dna.primaryMood, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            DnaBadge("💔 Heartbreak", "${dna.heartbreakPercentage}%", Color(0xFFFF4081))
                            DnaBadge("🌙 Night", "${dna.nightPercentage}%", Color(0xFF7C4DFF))
                            DnaBadge("🔥 Energy", "${dna.energyPercentage}%", Color(0xFFFF6D00))
                            DnaBadge("🎸 Indie", "${dna.indiePercentage}%", Color(0xFF00E676))
                        }
                    }
                }

                // Collaborators Section
                item {
                    NoTuneSurfaceCard(cardStyle = CardStyleVariant.GLASS, modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("👥 COLLABORATORS (${collaborators.size})", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            TextButton(onClick = { collaborators = collaborators + "Friend #${collaborators.size + 1} (Editor)" }) {
                                Text("➕ Invite Editor", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            collaborators.forEach { name ->
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(name, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Action Buttons
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, "🎶 Check out our collaborative playlist '$playlistName' on NØTUNE!\nhttps://music.youtube.com/playlist?list=LP_COLLAB_MIX")
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share Collaborative Playlist"))
                            },
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("🔗 Share Playlist", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                Toast.makeText(context, "Playlist Remixed & Duplicated!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("⚡ Remix / Duplicate", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                // Songs List
                items(tracks) { (id, title, artist) ->
                    NoTuneSurfaceCard(cardStyle = CardStyleVariant.GLASS, modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🎵", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(artist, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text("::", fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DnaBadge(label: String, valText: String, accentColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(valText, fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium, color = accentColor)
        Text(label, style = MaterialTheme.typography.labelSmall, fontSize = 10.sp)
    }
}
