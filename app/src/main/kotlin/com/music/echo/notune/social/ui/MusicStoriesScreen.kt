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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.music.echo.notune.theme.NoTuneAmbientCanvas
import com.music.echo.notune.theme.NoTuneSurfaceCard
import com.music.echo.viewmodels.SocialFeedViewModel
import echo.music.iad1tya.constants.CardStyleVariant
import echo.music.iad1tya.models.PostContent
import echo.music.iad1tya.models.SocialPost

@Composable
fun MusicStoriesScreen(
    socialFeedViewModel: SocialFeedViewModel = hiltViewModel(),
    onDismiss: () -> Unit = {}
) {
    val posts by socialFeedViewModel.feed.collectAsState()

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
                        text = "SOCIAL ACTIVITY",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.5.sp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text("Cached community activity", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                }
            }

            if (posts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "No social activity is cached on this device yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                return@Column
            }

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(posts, key = { it.id }) { post ->
                    ActivityCard(post)
                }
            }
        }
    }
}

@Composable
private fun ActivityCard(post: SocialPost) {
    NoTuneSurfaceCard(cardStyle = CardStyleVariant.GLASS, modifier = Modifier.fillMaxWidth()) {
        Column {
            Text(post.author.displayName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(post.createdAt.toString(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (post.caption.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(post.caption, style = MaterialTheme.typography.bodyMedium)
            }
            (post.content as? PostContent.Song)?.let { song ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(song.metadata.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(song.metadata.artists.joinToString { it.name }, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
