package com.music.echo.notune.ui.dna

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.music.echo.notune.design.theme.NotuneDarkUniverse
import com.music.echo.notune.design.theme.NotuneDeepViolet
import com.music.echo.notune.design.theme.NotuneRed
import com.music.echo.notune.design.theme.NotuneSurfaceDark
import com.music.echo.notune.design.theme.NotuneTextPrimary
import com.music.echo.notune.design.theme.NotuneTextSecondary

@Composable
fun MusicDNAViewV3(
    melodyRatio: Float = 0.37f,
    indieRatio: Float = 0.22f,
    hipHopRatio: Float = 0.16f,
    retroRatio: Float = 0.13f,
    experimentalRatio: Float = 0.12f,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NotuneDarkUniverse)
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "YOUR MUSIC DNA",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = NotuneTextPrimary,
                letterSpacing = 1.5.sp
            )
            Text(
                text = "Evolves continuously based on your listening habits",
                fontSize = 12.sp,
                color = NotuneTextSecondary
            )
            Spacer(modifier = Modifier.height(20.dp))

            // Radar Visualizer Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(NotuneSurfaceDark, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(200.dp)) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val maxR = size.width / 2f
                    
                    // Draw Radar Webs
                    for (step in 1..3) {
                        val r = maxR * (step / 3f)
                        drawCircle(
                            color = NotuneTextSecondary.copy(alpha = 0.15f),
                            radius = r,
                            style = Stroke(width = 1.dp.toPx())
                        )
                    }

                    // 5 Axes Values
                    val values = listOf(melodyRatio, indieRatio, hipHopRatio, retroRatio, experimentalRatio)
                    val angles = listOf(0.0, 72.0, 144.0, 216.0, 288.0)
                    
                    val path = Path()
                    angles.forEachIndexed { i, deg ->
                        val rad = Math.toRadians(deg - 90.0)
                        val dist = maxR * values[i] * 1.8f
                        val x = (center.x + dist * Math.cos(rad)).toFloat()
                        val y = (center.y + dist * Math.sin(rad)).toFloat()
                        
                        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                    }
                    path.close()

                    drawPath(
                        path = path,
                        color = NotuneRed.copy(alpha = 0.35f)
                    )
                    drawPath(
                        path = path,
                        color = NotuneRed,
                        style = Stroke(width = 2.5.dp.toPx())
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Your Sound Breakdown",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = NotuneTextPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            val breakdown = listOf(
                "Melody" to melodyRatio,
                "Indie" to indieRatio,
                "Hip-Hop" to hipHopRatio,
                "Retro" to retroRatio,
                "Experimental" to experimentalRatio
            )

            breakdown.forEach { (label, ratio) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = label, fontSize = 13.sp, color = NotuneTextPrimary)
                    Text(
                        text = "${(ratio * 100).toInt()}%",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = NotuneRed
                    )
                }
            }
        }
    }
}
