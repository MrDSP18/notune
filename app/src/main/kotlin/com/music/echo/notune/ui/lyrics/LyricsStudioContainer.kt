package com.music.echo.notune.ui.lyrics

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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

enum class LyricsSyncBadge(val label: String, val badgeColor: Color) {
    SYNCED("SYNCED", Color(0xFF10B981)),
    UNSYNCED("UNSYNCED", Color(0xFFF59E0B)),
    TRANSLATED("TRANSLATED", Color(0xFF3B82F6)),
    AI_ASSISTED("AI-ASSISTED", Color(0xFF8B5CF6)),
    USER_PROVIDED("COMMUNITY", Color(0xFFEC4899))
}

data class LyricLineItem(
    val originalText: String,
    val romanizedText: String? = null,
    val translatedText: String? = null,
    val timestampMs: Long = 0L
)

@Composable
fun LyricsStudioContainer(
    songTitle: String = "Husn",
    artistName: String = "Anuv Jain",
    activeLineIndex: Int = 2,
    badgeState: LyricsSyncBadge = LyricsSyncBadge.SYNCED,
    lyricLines: List<LyricLineItem> = listOf(
        LyricLineItem("Dekho dekho kaisi baatein yahan ki", "Dekho dekho kaisi baatein yahan ki", "Look at the kind of conversations happening here"),
        LyricLineItem("Batein ye kabhi na tu bhulana", "Batein ye kabhi na tu bhulana", "Never forget these words"),
        LyricLineItem("Tu aake dekhle ho maine raatein kitni", "Tu aake dekhle ho maine raatein kitni", "Come and see how many nights I have spent"),
        LyricLineItem("Aasman se taare kitne tode hain", "Aasman se taare kitne tode hain", "How many stars I have plucked from the sky"),
        LyricLineItem("Tere hi liye ye geet likhe hain", "Tere hi liye ye geet likhe hain", "Written these songs just for you")
    ),
    onLineClick: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedLanguage by remember { mutableStateOf("Original") }
    val languages = listOf("Original", "English", "தமிழ்", "Hindi", "Malayalam")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF09090B))
            .padding(20.dp)
    ) {
        // Header & Sync Confidence Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "LYRICS STUDIO // $songTitle",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = NothingFont,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = artistName,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFA1A1AA)
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = badgeState.badgeColor.copy(alpha = 0.2f),
                border = BorderStroke(1.dp, badgeState.badgeColor)
            ) {
                Text(
                    text = badgeState.label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = NothingFont,
                        fontWeight = FontWeight.Bold
                    ),
                    color = badgeState.badgeColor,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Language Translation Switcher Tabs
        SecondaryScrollableTabRow(
            selectedTabIndex = languages.indexOf(selectedLanguage),
            edgePadding = 0.dp,
            containerColor = Color.Transparent,
            divider = {}
        ) {
            languages.forEach { lang ->
                Tab(
                    selected = selectedLanguage == lang,
                    onClick = { selectedLanguage = lang },
                    text = {
                        Text(
                            text = lang,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (selectedLanguage == lang) MaterialTheme.colorScheme.primary else Color(0xFF71717A)
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Synchronized Karaoke Lyrics List
        if (lyricLines.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "LYRICS UNAVAILABLE",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = NothingFont,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        ),
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No authorized lyrics found for this track. AI does not hallucinate copyrighted lyrics.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                itemsIndexed(lyricLines) { index, line ->
                    val isActive = index == activeLineIndex

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                else Color.Transparent
                            )
                            .clickable { onLineClick(index) }
                            .padding(12.dp)
                    ) {
                        Text(
                            text = if (selectedLanguage == "Original" || line.translatedText == null) line.originalText else line.translatedText,
                            style = if (isActive) MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 22.sp
                            ) else MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Normal,
                                fontSize = 17.sp
                            ),
                            color = if (isActive) MaterialTheme.colorScheme.primary else Color(0xFFD4D4D8)
                        )

                        line.romanizedText?.let { roman ->
                            if (selectedLanguage != "Original") {
                                Text(
                                    text = "Pronunciation: $roman",
                                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = NothingFont),
                                    color = Color(0xFF71717A),
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
