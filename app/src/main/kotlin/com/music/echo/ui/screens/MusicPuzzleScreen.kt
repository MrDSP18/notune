package com.music.echo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MusicPuzzleScreen(
    question: String = "Which artist sang this song?",
    options: List<String> = listOf("Artist A", "Artist B", "Artist C", "Artist D"),
    correctOptionIndex: Int = 1,
    onBack: () -> Unit
) {
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var streak by remember { mutableStateOf(3) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0C0C0C))
            .padding(24.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "←",
                    color = Color.White,
                    fontSize = 24.sp,
                    modifier = Modifier.clickable { onBack() }
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "🔥 Streak: $streak",
                    color = Color(0xFFFF9E00),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "🧩 MUSIC PUZZLE",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF181818), RoundedCornerShape(16.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = question,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            options.forEachIndexed { index, optionText ->
                val isSelected = selectedIndex == index
                val isCorrect = isSelected && index == correctOptionIndex

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .background(
                            when {
                                isSelected && isCorrect -> Color(0xFF1B4332)
                                isSelected -> Color(0xFF4A0E17)
                                else -> Color(0xFF181818)
                            },
                            RoundedCornerShape(12.dp)
                        )
                        .border(
                            1.dp,
                            when {
                                isSelected && isCorrect -> Color(0xFF2D6A4F)
                                isSelected -> Color(0xFFFF0055)
                                else -> Color(0xFF2A2A2A)
                            },
                            RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            selectedIndex = index
                            if (index == correctOptionIndex) streak++
                        }
                        .padding(16.dp)
                ) {
                    Text(
                        text = optionText,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
