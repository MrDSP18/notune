package com.music.echo.notune.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.People
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.music.echo.notune.design.theme.NotuneDarkUniverse
import com.music.echo.notune.design.theme.NotuneGlassBorder
import com.music.echo.notune.design.theme.NotuneRed
import com.music.echo.notune.design.theme.NotuneSurfaceDark
import com.music.echo.notune.design.theme.NotuneTextPrimary
import com.music.echo.notune.design.theme.NotuneTextSecondary

enum class V3Destination(val title: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.Home),
    DISCOVER("Discover", Icons.Default.Explore),
    CREATE("+", Icons.Default.Add),
    SOCIAL("Social", Icons.Default.People),
    LIBRARY("Library", Icons.Default.LibraryMusic)
}

@Composable
fun NotuneV3NavigationBar(
    currentDestination: V3Destination,
    onDestinationSelected: (V3Destination) -> Unit,
    onCreateOptionSelected: (CreateOption) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCreateSheet by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            shape = RoundedCornerShape(28.dp),
            color = NotuneSurfaceDark.copy(alpha = 0.92f),
            tonalElevation = 8.dp,
            shadowElevation = 12.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                V3Destination.values().forEach { destination ->
                    if (destination == V3Destination.CREATE) {
                        // Central Prominent + Button
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(NotuneRed)
                                .clickable { showCreateSheet = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Create",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    } else {
                        val isSelected = destination == currentDestination
                        val tint = if (isSelected) NotuneRed else NotuneTextSecondary
                        
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onDestinationSelected(destination) }
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = destination.title,
                                tint = tint,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = destination.title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = tint
                            )
                        }
                    }
                }
            }
        }

        // Create Modal Overlay
        if (showCreateSheet) {
            CreateModalSheet(
                onDismiss = { showCreateSheet = false },
                onOptionClick = { option ->
                    showCreateSheet = false
                    onCreateOptionSelected(option)
                }
            )
        }
    }
}

enum class CreateOption(val label: String, val subtitle: String, val icon: String) {
    PLAYLIST("Create Playlist", "Assemble custom music collection", "🎵"),
    ROOM("Start Room", "Host live listening room for friends", "🔴"),
    STORY("Share Story", "Post listening highlight to friends", "📖"),
    MIX("Build Mix", "AI-assisted multi-genre blend", "🎛️"),
    AI_PLAYLIST("AI Playlist", "Prompt NØ to generate smart queue", "✦"),
    MOOD_FLOW("Mood Flow", "Drag interactive vibe matrix", "🔮")
}

@Composable
private fun CreateModalSheet(
    onDismiss: () -> Unit,
    onOptionClick: (CreateOption) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            color = NotuneDarkUniverse
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "NØ CREATE",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = NotuneTextPrimary
                )
                Text(
                    text = "What would you like to build right now?",
                    fontSize = 12.sp,
                    color = NotuneTextSecondary
                )
                Spacer(modifier = Modifier.height(16.dp))

                CreateOption.values().forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(NotuneSurfaceDark)
                            .clickable { onOptionClick(option) }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = option.icon, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = option.label,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = NotuneTextPrimary
                            )
                            Text(
                                text = option.subtitle,
                                fontSize = 11.sp,
                                color = NotuneTextSecondary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
