package com.music.echo.notune.ui.memory

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.music.echo.notune.intelligence.memory.MemoryCategory
import com.music.echo.notune.intelligence.memory.MemoryItem
import com.music.echo.notune.intelligence.memory.MemorySource
import echo.music.iad1tya.ui.theme.NothingFont

@Composable
fun NotuneMemoryScreen(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isLearningPaused by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(MemoryCategory.EXPLICIT) }

    var memories by remember {
        mutableStateOf(
            listOf(
                MemoryItem("m1", MemoryCategory.EXPLICIT, "Language Preference", "Tamil, English", MemorySource.EXPLICIT, 1.0f),
                MemoryItem("m2", MemoryCategory.BEHAVIORAL, "Frequently Replayed Artist", "Anuv Jain", MemorySource.BEHAVIORAL, 0.88f),
                MemoryItem("m3", MemoryCategory.BEHAVIORAL, "Completed Genre", "Melody & Acoustic", MemorySource.BEHAVIORAL, 0.92f),
                MemoryItem("m4", MemoryCategory.SESSION, "Inferred Mood", "Calm + Focused", MemorySource.INFERRED, 0.75f),
                MemoryItem("m5", MemoryCategory.MUSIC, "Discovery Tolerance", "High (67%)", MemorySource.BEHAVIORAL, 0.80f)
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF09090B))
            .padding(20.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "NØTUNE MEMORY // DATA TRANSPARENCY",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = NothingFont,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "What NØTUNE learned about your music behavior",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFA1A1AA)
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isLearningPaused) Color(0xFFEF4444).copy(alpha = 0.2f) else Color(0xFF10B981).copy(alpha = 0.2f),
                border = BorderStroke(1.dp, if (isLearningPaused) Color(0xFFEF4444) else Color(0xFF10B981))
            ) {
                Text(
                    text = if (isLearningPaused) "PAUSED" else "ACTIVE",
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont),
                    color = if (isLearningPaused) Color(0xFFEF4444) else Color(0xFF10B981),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Control Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { isLearningPaused = !isLearningPaused },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isLearningPaused) MaterialTheme.colorScheme.primary else Color(0xFF27272A)
                )
            ) {
                Text(
                    text = if (isLearningPaused) "RESUME" else "PAUSE LEARNING",
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont)
                )
            }

            OutlinedButton(
                onClick = { memories = emptyList() },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "RESET ALL",
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont),
                    color = Color(0xFFEF4444)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Category Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            MemoryCategory.entries.forEach { cat ->
                val isSel = selectedCategory == cat
                FilterChip(
                    selected = isSel,
                    onClick = { selectedCategory = cat },
                    label = { Text(text = cat.name, style = MaterialTheme.typography.labelSmall) },
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Memory Items List
        val filtered = memories.filter { it.category == selectedCategory }

        if (filtered.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "NO MEMORIES STORED FOR THIS CATEGORY",
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = NothingFont),
                    color = Color(0xFF71717A)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filtered) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.key.uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = NothingFont,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Text(
                                    text = "FORGET",
                                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont),
                                    color = Color(0xFFEF4444),
                                    modifier = Modifier.clickable {
                                        memories = memories.filter { it.id != item.id }
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = item.value,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Source: ${item.source.name}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF71717A)
                                )
                                Text(
                                    text = "Confidence: ${(item.confidence * 100).toInt()}%",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF10B981)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
