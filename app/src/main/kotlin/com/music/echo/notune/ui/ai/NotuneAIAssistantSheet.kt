package com.music.echo.notune.ui.ai

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.music.echo.notune.design.theme.NotuneDarkUniverse
import com.music.echo.notune.design.theme.NotuneDeepViolet
import com.music.echo.notune.design.theme.NotuneRed
import com.music.echo.notune.design.theme.NotuneSurfaceDark
import com.music.echo.notune.design.theme.NotuneSurfaceVariantDark
import com.music.echo.notune.design.theme.NotuneTextPrimary
import com.music.echo.notune.design.theme.NotuneTextSecondary

@Composable
fun NotuneAIAssistantSheet(
    onDismiss: () -> Unit,
    onExecuteAIQuery: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var queryText by remember { mutableStateOf("") }

    val tryPrompts = remember {
        listOf(
            "Make this more energetic",
            "Play something nostalgic",
            "Find similar Tamil melodies",
            "Build a 30 min workout",
            "Explain the lyrics verse",
            "Remove repetitive tracks"
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.82f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable(enabled = false) {},
            shape = RoundedCornerShape(28.dp),
            color = NotuneDarkUniverse
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
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
                            text = "ASK NØ",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = NotuneTextPrimary,
                            letterSpacing = 1.sp
                        )
                    }

                    Text(
                        text = "Close",
                        fontSize = 12.sp,
                        color = NotuneTextSecondary,
                        modifier = Modifier.clickable { onDismiss() }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "What are you feeling or looking for?",
                    fontSize = 13.sp,
                    color = NotuneTextSecondary
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Input Box
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = queryText,
                        onValueChange = { queryText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Type or speak prompt...", color = NotuneTextSecondary) },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NotuneRed,
                            unfocusedBorderColor = NotuneSurfaceVariantDark,
                            focusedContainerColor = NotuneSurfaceDark,
                            unfocusedContainerColor = NotuneSurfaceDark,
                            focusedTextColor = NotuneTextPrimary,
                            unfocusedTextColor = NotuneTextPrimary
                        ),
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Voice Input",
                                tint = NotuneRed,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (queryText.isNotBlank()) NotuneRed else NotuneSurfaceVariantDark
                            )
                            .clickable(enabled = queryText.isNotBlank()) {
                                onExecuteAIQuery(queryText)
                                onDismiss()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send AI Request",
                            tint = if (queryText.isNotBlank()) Color.White else NotuneTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Try asking",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NotuneTextSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Quick Suggestion Chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(tryPrompts) { prompt ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = NotuneSurfaceDark,
                            modifier = Modifier.clickable {
                                queryText = prompt
                            }
                        ) {
                            Text(
                                text = prompt,
                                fontSize = 12.sp,
                                color = NotuneTextPrimary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
