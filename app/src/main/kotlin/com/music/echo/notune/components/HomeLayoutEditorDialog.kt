package com.music.echo.notune.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.datastore.preferences.core.edit
import com.music.echo.ui.theme.tokens.LocalThemeTokens
import echo.music.iad1tya.constants.HomeOrderKey
import echo.music.iad1tya.utils.dataStore
import kotlinx.coroutines.launch

@Composable
fun HomeLayoutEditorDialog(
    onDismiss: () -> Unit
) {
    val tokens = LocalThemeTokens.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var sections by remember {
        mutableStateOf(
            listOf(
                "Greeting & Quick Access" to true,
                "Recently Played" to true,
                "Continue Listening" to true,
                "Quick Picks" to true,
                "Made For You" to true,
                "Mood Mixes" to true,
                "Trending & Charts" to true,
                "New Releases" to true,
                "AI Recommendations" to true,
                "Music DNA" to true,
                "Listen Together & Rooms" to true
            )
        )
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
                    .fillMaxHeight(0.8f)
            ) {
                Text(
                    text = "EDIT HOME LAYOUT",
                    style = tokens.typography.titleMedium,
                    color = tokens.colors.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Reorder and toggle sections to customize your home experience.",
                    style = tokens.typography.bodySmall,
                    color = tokens.colors.textSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(sections) { index, item ->
                        val (name, enabled) = item
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(tokens.colors.surfaceVariant, shape = RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "≡",
                                style = tokens.typography.titleMedium,
                                color = tokens.colors.textSecondary,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                            Text(
                                text = name,
                                style = tokens.typography.bodyMedium,
                                color = if (enabled) tokens.colors.textPrimary else tokens.colors.textSecondary,
                                modifier = Modifier.weight(1f)
                            )
                            Switch(
                                checked = enabled,
                                onCheckedChange = { isChecked ->
                                    val updated = sections.toMutableList()
                                    updated[index] = name to isChecked
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
                                val savedOrder = sections.filter { it.second }.joinToString(",") { it.first }
                                context.dataStore.edit { prefs ->
                                    prefs[HomeOrderKey] = savedOrder
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
