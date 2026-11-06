package com.music.echo.notune.ui.timemachine

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.music.echo.notune.design.theme.NotuneDarkUniverse
import com.music.echo.notune.design.theme.NotuneDeepViolet
import com.music.echo.notune.design.theme.NotuneRed
import com.music.echo.notune.design.theme.NotuneSurfaceDark
import com.music.echo.notune.design.theme.NotuneTextPrimary
import com.music.echo.notune.design.theme.NotuneTextSecondary

data class TimeEraItem(
    val monthYear: String,
    val totalSongs: Int,
    val totalArtists: Int,
    val topGenre: String,
    val keyTrack: String,
    val timeContext: String
)

@Composable
fun TimeMachineViewV3(
    onPlayEra: (TimeEraItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val eras = listOf(
        TimeEraItem("September 2024", 124, 18, "Tamil Melody", "Munbe Vaa", "Mostly listened after 10 PM 🌙"),
        TimeEraItem("August 2024", 198, 24, "Anirudh Kuthu", "Vathi Coming", "Peak gym & commute energy ⚡"),
        TimeEraItem("July 2024", 87, 12, "Indie Chill", "Katchi Sera", "Rainy evening mood 🌧️")
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NotuneDarkUniverse)
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "NØ TIME MACHINE",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = NotuneTextPrimary,
                letterSpacing = 1.5.sp
            )
            Text(
                text = "Travel back to what you were feeling in past months",
                fontSize = 12.sp,
                color = NotuneTextSecondary
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                items(eras) { era ->
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = NotuneSurfaceDark),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = era.monthYear,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NotuneRed
                                )
                                Text(
                                    text = era.topGenre,
                                    fontSize = 11.sp,
                                    color = NotuneTextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "♡ ${era.totalSongs} songs • ♪ ${era.totalArtists} artists",
                                fontSize = 13.sp,
                                color = NotuneTextPrimary
                            )
                            Text(
                                text = era.timeContext,
                                fontSize = 12.sp,
                                color = NotuneTextSecondary
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = { onPlayEra(era) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = NotuneDeepViolet),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = NotuneTextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "REPLAY THIS ERA",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NotuneTextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
