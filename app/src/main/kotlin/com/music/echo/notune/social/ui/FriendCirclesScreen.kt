package com.music.echo.notune.social.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.music.echo.notune.social.model.MusicChallenge
import com.music.echo.notune.social.repository.SocialRepository
import com.music.echo.notune.theme.NoTuneAmbientCanvas
import com.music.echo.notune.theme.NoTuneSurfaceCard
import echo.music.iad1tya.constants.CardStyleVariant

@Composable
fun FriendCirclesScreen(
    socialRepository: SocialRepository = remember { SocialRepository() },
    onNavigateToListenTogether: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    val circles by socialRepository.circles.collectAsState()
    val polls by socialRepository.polls.collectAsState()

    val challenges = remember {
        listOf(
            MusicChallenge(1, "Day 1", "Song that reminds you of childhood", "Until I Found You"),
            MusicChallenge(2, "Day 2", "Song that gives you goosebumps every time", "Kesariya"),
            MusicChallenge(3, "Day 3", "One song you never skip", "Starboy")
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
                        text = "FRIEND CIRCLES & POLLS",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.5.sp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text("Squad Groups & Music Challenges", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Squad Circles Section
                item {
                    Text("👥 FRIEND CIRCLES", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }

                items(circles) { circle ->
                    NoTuneSurfaceCard(cardStyle = CardStyleVariant.GLASS, modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(circle.emoji, fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(circle.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text("${circle.membersCount} Members • Shared Queue: ${circle.sharedPlaylistName}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Button(
                                onClick = onNavigateToListenTogether,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("📻 Room", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Interactive Music Polls
                item {
                    Text("🗳️ MUSIC POLLS", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }

                items(polls) { poll ->
                    NoTuneSurfaceCard(cardStyle = CardStyleVariant.GLASS, modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Text(poll.question, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("Created by ${poll.creatorName} • ${poll.totalVotes} votes", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                            Spacer(modifier = Modifier.height(10.dp))

                            poll.options.forEach { opt ->
                                val pct = if (poll.totalVotes > 0) (opt.votes * 100) / poll.totalVotes else 0
                                OutlinedButton(
                                    onClick = { socialRepository.votePoll(poll.id, opt.id) },
                                    enabled = !poll.isVoted,
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(opt.text, fontWeight = FontWeight.Bold)
                                        Text("$pct% (${opt.votes})", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                    }
                }

                // 30-Day Music Challenges
                item {
                    Text("🎲 30-DAY MUSIC CHALLENGES", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }

                items(challenges) { ch ->
                    NoTuneSurfaceCard(cardStyle = CardStyleVariant.GLASS, modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🏆", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("${ch.title}: ${ch.prompt}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text("Your Pick: ${ch.completedSongTitle ?: "Not answered yet"}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
    }
}
