package com.music.echo.notune.lyrics.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.music.echo.notune.lyrics.LyricsLanguage
import com.music.echo.notune.lyrics.LyricsMode

@Composable
fun MultilingualLyricsControlBar(
    selectedLanguage: LyricsLanguage,
    selectedMode: LyricsMode,
    onLanguageSelected: (LyricsLanguage) -> Unit,
    onModeSelected: (LyricsMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Mode Selector Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LyricsMode.entries.forEach { mode ->
                FilterChip(
                    selected = mode == selectedMode,
                    onClick = { onModeSelected(mode) },
                    label = {
                        Text(
                            text = mode.label,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Language Selector Scrollable Row (show native names)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LyricsLanguage.entries.forEach { lang ->
                AssistChip(
                    onClick = { onLanguageSelected(lang) },
                    label = {
                        Text(
                            text = if (lang == LyricsLanguage.ENGLISH) "EN" else "${lang.nativeName} (${lang.displayName})",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp)
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (lang == selectedLanguage) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    }
}
