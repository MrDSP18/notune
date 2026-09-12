package com.music.echo.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import echo.music.iad1tya.notune.MusicDnaProfile

@Composable
fun MusicDnaCard(
    dnaProfile: MusicDnaProfile,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF141414), Color(0xFF000000))
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .border(1.dp, Color(0xFF333333), RoundedCornerShape(24.dp))
            .padding(24.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🧬", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "NØTUNE DNA 2.0",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            DnaDimensionBar(label = "ENERGY", value = dnaProfile.energy)
            DnaDimensionBar(label = "DISCOVERY", value = dnaProfile.discovery)
            DnaDimensionBar(label = "NOSTALGIA", value = dnaProfile.nostalgia)
            DnaDimensionBar(label = "DIVERSITY", value = dnaProfile.diversity)
            DnaDimensionBar(label = "REPLAY", value = dnaProfile.replay)
            DnaDimensionBar(label = "MOOD RANGE", value = dnaProfile.moodRange)
        }
    }
}

@Composable
private fun DnaDimensionBar(label: String, value: Float) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = Color.Gray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${(value * 100).toInt()}",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(Color(0xFF222222), RoundedCornerShape(3.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(value.coerceIn(0f, 1f))
                    .height(6.dp)
                    .background(Color(0xFFFF0055), RoundedCornerShape(3.dp))
            )
        }
    }
}
