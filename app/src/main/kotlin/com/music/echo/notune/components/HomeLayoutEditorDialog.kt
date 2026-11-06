package com.music.echo.notune.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.datastore.preferences.core.edit
import com.music.echo.ui.theme.tokens.LocalThemeTokens
import echo.music.iad1tya.constants.HomeLayoutOrderKey
import echo.music.iad1tya.constants.HomeOrderKey
import echo.music.iad1tya.utils.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class HomeLayoutSectionConfig(
    val id: String,
    val displayName: String,
    val isEnabled: Boolean
)

val DEFAULT_HOME_SECTIONS = listOf(
    HomeLayoutSectionConfig("greeting", "Greeting & Quick Actions", true),
    HomeLayoutSectionConfig("speed_dial", "Speed Dial & Quick Picks", true),
    HomeLayoutSectionConfig("keep_listening", "Continue Listening & Recently Played", true),
    HomeLayoutSectionConfig("quick_picks", "Made For You & Quick Picks", true),
    HomeLayoutSectionConfig("daily_discover", "Daily Discover & Replay", true),
    HomeLayoutSectionConfig("mood_and_genres", "Mood Mixes & Genres", true),
    HomeLayoutSectionConfig("trending_charts", "Trending & Charts", true),
    HomeLayoutSectionConfig("new_releases", "New Releases", true),
    HomeLayoutSectionConfig("ai_recommendations", "AI Music Recommendations", true),
    HomeLayoutSectionConfig("music_dna", "Music DNA & Vibe Radar", true),
    HomeLayoutSectionConfig("rooms", "Listen Together & Sync Rooms", true),
    HomeLayoutSectionConfig("account_playlists", "Account Playlists & Downloads", true),
    HomeLayoutSectionConfig("forgotten_favorites", "Forgotten Favorites", true),
    HomeLayoutSectionConfig("from_the_community", "From the Community", true)
)

@Composable
fun HomeLayoutEditorDialog(
    onDismiss: () -> Unit
) {
    val tokens = LocalThemeTokens.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var sections by remember { mutableStateOf(DEFAULT_HOME_SECTIONS) }

    LaunchedEffect(Unit) {
        val savedOrder = context.dataStore.data.first()[HomeOrderKey] ?: ""
        if (savedOrder.isNotBlank()) {
            val savedIds = savedOrder.split(",").map { it.trim() }
            val existingIds = DEFAULT_HOME_SECTIONS.map { it.id }.toSet()
            val orderedList = mutableListOf<HomeLayoutSectionConfig>()
            
            savedIds.forEach { id ->
                DEFAULT_HOME_SECTIONS.find { it.id == id }?.let {
                    orderedList.add(it.copy(isEnabled = true))
                }
            }
            DEFAULT_HOME_SECTIONS.forEach { def ->
                if (!savedIds.contains(def.id)) {
                    orderedList.add(def.copy(isEnabled = false))
                }
            }
            sections = orderedList
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = tokens.shapes.cardShape,
            color = tokens.colors.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, tokens.colors.outline),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxHeight(0.85f)
            ) {
                Text(
                    text = "EDIT HOME LAYOUT",
                    style = tokens.typography.titleMedium,
                    color = tokens.colors.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Toggle visibility and order sections to personalize your music home.",
                    style = tokens.typography.bodySmall,
                    color = tokens.colors.textSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(sections) { index, item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(tokens.colors.surfaceVariant, shape = RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "::",
                                style = tokens.typography.titleMedium,
                                color = tokens.colors.textSecondary,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                            Text(
                                text = item.displayName,
                                style = tokens.typography.bodyMedium,
                                color = if (item.isEnabled) tokens.colors.textPrimary else tokens.colors.textSecondary,
                                modifier = Modifier.weight(1f)
                            )
                            Switch(
                                checked = item.isEnabled,
                                onCheckedChange = { isChecked ->
                                    val updated = sections.toMutableList()
                                    updated[index] = item.copy(isEnabled = isChecked)
                                    sections = updated
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = tokens.colors.textSecondary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    NoTuneButton(
                        text = "Save Layout",
                        onClick = {
                            scope.launch {
                                val savedOrder = sections.filter { it.isEnabled }.joinToString(",") { it.id }
                                context.dataStore.edit { prefs ->
                                    prefs[HomeOrderKey] = savedOrder
                                    prefs[HomeLayoutOrderKey] = savedOrder
                                }
                                onDismiss()
                            }
                        }
                    )
                }
            }
        }
    }
}

