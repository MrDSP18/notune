package com.music.echo.notune.social.connect

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
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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

data class RecommendedConnection(
    val username: String,
    val displayName: String,
    val compatibilityPercentage: Int,
    val commonGenres: String,
    val isFollowing: Boolean = false
)

@Composable
fun NoConnectScreen(
    currentUsername: String = "dharan",
    onConnectWithUser: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var showQrDialog by remember { mutableStateOf(false) }

    val recommendations = remember {
        listOf(
            RecommendedConnection("priya", "Priya S.", 91, "Tamil 94% • Melody 88%"),
            RecommendedConnection("arun_k", "Arun Kumar", 86, "Anirudh Kuthu 92% • Gym 85%"),
            RecommendedConnection("kavi_m", "Kavitha", 82, "Indie Chill 90% • 2000s 84%")
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NotuneDarkUniverse)
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "NØ CONNECT",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = NotuneTextPrimary,
                    letterSpacing = 1.5.sp
                )

                // QR Code Button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(NotuneSurfaceDark)
                        .clickable { showQrDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = "NØ QR Code",
                        tint = NotuneRed,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Text(
                text = "Discover connections with matching Music DNA",
                fontSize = 12.sp,
                color = NotuneTextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search @username or scan QR...", color = NotuneTextSecondary) },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NotuneRed,
                    unfocusedBorderColor = NotuneSurfaceVariantDark,
                    focusedContainerColor = NotuneSurfaceDark,
                    unfocusedContainerColor = NotuneSurfaceDark,
                    focusedTextColor = NotuneTextPrimary,
                    unfocusedTextColor = NotuneTextPrimary
                ),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = NotuneTextSecondary)
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Recommended Music DNA Matches",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = NotuneTextPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(recommendations) { user ->
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = NotuneSurfaceDark,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(NotuneRed),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = user.displayName.take(1),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = user.displayName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NotuneTextPrimary
                                )
                                Text(
                                    text = "@${user.username} • ${user.commonGenres}",
                                    fontSize = 11.sp,
                                    color = NotuneTextSecondary
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${user.compatibilityPercentage}% Match",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = NotuneRed
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Button(
                                    onClick = { onConnectWithUser(user.username) },
                                    colors = ButtonDefaults.buttonColors(containerColor = NotuneDeepViolet),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text(text = "Connect", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // NØ QR Dialog Sheet
        if (showQrDialog) {
            NoQrDialog(
                username = currentUsername,
                onDismiss = { showQrDialog = false }
            )
        }
    }
}

@Composable
private fun NoQrDialog(username: String, onDismiss: () -> Unit) {
    val payload = NoQrEngine.generateUserPayload(username)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.82f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = NotuneSurfaceDark),
            modifier = Modifier
                .width(300.dp)
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "NØ QR CODE",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = NotuneRed,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "@$username", fontSize = 14.sp, color = NotuneTextSecondary)
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "NØ", fontSize = 36.sp, fontWeight = FontWeight.Black, color = Color.Black)
                        Text(text = "SCAN TO CONNECT", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Payload: $payload",
                    fontSize = 10.sp,
                    color = NotuneTextSecondary
                )
            }
        }
    }
}
