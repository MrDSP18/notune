package echo.music.iad1tya.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import echo.music.iad1tya.R
import echo.music.iad1tya.ui.theme.NothingFont

@Composable
fun NoTuneAIPromptCard(
    onAskClick: () -> Unit,
    onLabClick: () -> Unit,
    onReplayClick: () -> Unit
) {
    val red = Color(0xFFFF0031)
    val cyan = Color(0xFF57D8C6)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFF0B0B0E),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF34343B))
    ) {
        Box {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(190.dp)
                    .offset(x = 70.dp, y = (-90).dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(red.copy(alpha = 0.22f), Color.Transparent)
                        ),
                        CircleShape
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF171216), Color(0xFF0B0B0E), Color(0xFF101719))
                        )
                    )
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(cyan)
                        )
                        Text(
                            text = "NØ / NEURAL STUDIO",
                            style = androidx.compose.ui.text.TextStyle(
                                fontFamily = NothingFont,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp,
                                color = cyan
                            )
                        )
                    }
                    Text(
                        text = "MUSIC INTELLIGENCE",
                        style = androidx.compose.ui.text.TextStyle(
                            fontFamily = NothingFont,
                            fontSize = 8.sp,
                            letterSpacing = 0.8.sp,
                            color = Color.White.copy(alpha = 0.48f)
                        )
                    )
                }

                Spacer(Modifier.height(18.dp))

                Text(
                    text = "Your music.\nYour signal.",
                    fontSize = 27.sp,
                    lineHeight = 30.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Ask in plain language. Build a queue, explore a sound, or understand what you love.",
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = Color.White.copy(alpha = 0.62f)
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF202026))
                        .border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(12.dp))
                        .clickable(onClick = onAskClick)
                        .padding(horizontal = 13.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.mic),
                        contentDescription = null,
                        tint = red,
                        modifier = Modifier.size(19.dp)
                    )
                    Spacer(Modifier.width(11.dp))
                    Text(
                        text = "Ask NØ to find your next sound...",
                        fontFamily = NothingFont,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.72f),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "↗",
                        fontSize = 18.sp,
                        color = red,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickActionPill(
                        label = "AI PLAYGROUND",
                        icon = R.drawable.biotech,
                        accent = cyan,
                        onClick = onLabClick,
                        modifier = Modifier.weight(1.25f)
                    )
                    QuickActionPill(
                        label = "REPLAY",
                        icon = R.drawable.replay,
                        accent = red,
                        onClick = onReplayClick,
                        modifier = Modifier.weight(0.75f)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionPill(
    label: String,
    icon: Int,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(42.dp),
        shape = RoundedCornerShape(10.dp),
        color = Color.White.copy(alpha = 0.045f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.10f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(7.dp))
            Text(
                text = label,
                style = androidx.compose.ui.text.TextStyle(
                    fontFamily = NothingFont,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.6.sp,
                    fontSize = 9.sp
                ),
                color = Color.White
            )
        }
    }
}
