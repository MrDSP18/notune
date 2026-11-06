package com.music.echo.notune.ai.ui

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.music.echo.notune.ai.core.NoAiEngine
import com.music.echo.notune.ai.core.NoAiModel
import com.music.echo.notune.ai.core.NoAiModelRegistry
import com.music.echo.notune.ai.tools.CommandRouter
import com.music.echo.notune.design.theme.NotuneDarkUniverse
import com.music.echo.notune.design.theme.NotuneDeepViolet
import com.music.echo.notune.design.theme.NotuneRed
import com.music.echo.notune.design.theme.NotuneSurfaceDark
import com.music.echo.notune.design.theme.NotuneSurfaceVariantDark
import com.music.echo.notune.design.theme.NotuneTextPrimary
import com.music.echo.notune.design.theme.NotuneTextSecondary

@Composable
fun NoAiLabScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedModel by remember { mutableStateOf(NoAiEngine.getActiveModel()) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NotuneDarkUniverse)
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "✦",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = NotuneRed
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "NØ AI LAB",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = NotuneTextPrimary,
                        letterSpacing = 1.5.sp
                    )
                }

                Text(
                    text = "ON-DEVICE SERVERLESS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = NotuneRed,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(NotuneRed.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Text(
                text = "Privacy-first local AI runtime • ₹0 API Cost • 100% Offline ready",
                fontSize = 12.sp,
                color = NotuneTextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Active Model Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NotuneSurfaceDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Memory,
                        contentDescription = null,
                        tint = NotuneRed,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "ACTIVE RUNTIME",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = NotuneTextSecondary
                        )
                        Text(
                            text = selectedModel.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = NotuneTextPrimary
                        )
                        Text(
                            text = selectedModel.description,
                            fontSize = 11.sp,
                            color = NotuneTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "On-Device Model Store",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = NotuneTextPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Model Store List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(NoAiModelRegistry.Models) { model ->
                    val isSelected = model.id == selectedModel.id
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) NotuneSurfaceVariantDark else NotuneSurfaceDark,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedModel = model
                                NoAiEngine.setActiveModel(model)
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = model.name,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NotuneTextPrimary
                                    )
                                    if (model.isRecommendedForDevice) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "RECOMMENDED",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NotuneRed,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(NotuneRed.copy(alpha = 0.15f))
                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = model.description,
                                    fontSize = 11.sp,
                                    color = NotuneTextSecondary
                                )
                                Text(
                                    text = "Size: ${if (model.downloadSizeMb == 0) "Built-in (0MB)" else "${model.downloadSizeMb} MB"} • RAM: ${model.activeMemoryMb} MB",
                                    fontSize = 10.sp,
                                    color = NotuneTextSecondary
                                )
                            }

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Active",
                                    tint = NotuneRed,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tool Matrix Summary
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = NotuneSurfaceDark,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "REGISTERED AI MUSIC TOOLS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NotuneDeepViolet
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${CommandRouter.getAllTools().size} executable tools active (Playback, Queue, Flow, Search, Lyrics, DNA, Audio, Room, Share)",
                        fontSize = 12.sp,
                        color = NotuneTextSecondary
                    )
                }
            }
        }
    }
}
