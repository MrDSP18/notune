
package echo.music.iad1tya.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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
    val nothingRed = Color(0xFFFF0031)
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(0xFF09090B), RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFF27272A), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(
                    painter = painterResource(R.drawable.commit), // terminal icon substitute
                    contentDescription = null,
                    tint = nothingRed,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "CMD_PROMPT // PROMPT_READY",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = NothingFont,
                        color = nothingRed,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
            }
            Text(
                text = "NEURAL_CORE: 98.4%",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = NothingFont,
                    color = Color(0xFF69D6E2),
                    fontSize = 10.sp
                )
            )
        }

        Spacer(Modifier.height(12.dp))

        // Simulated Input Field
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF18181B))
                .clickable { onAskClick() }
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.mic),
                contentDescription = null,
                tint = nothingRed,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = "Instruct agent (e.g. play ambient post-rock)...",
                style = MaterialTheme.typography.bodyMedium.copy(fontFamily = NothingFont),
                color = Color.White.copy(alpha = 0.4f),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(16.dp))

        // Quick Command Pills (Scrollable)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickActionPill(
                label = "REPLAY",
                icon = R.drawable.replay,
                onClick = onReplayClick,
                modifier = Modifier.weight(1f)
            )
            QuickActionPill(
                label = "LAB",
                icon = R.drawable.biotech,
                onClick = onLabClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun QuickActionPill(
    label: String,
    icon: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(36.dp),
        shape = RoundedCornerShape(4.dp),
        color = Color.White.copy(alpha = 0.05f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = Color(0xFFFF0031),
                modifier = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = NothingFont,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = Color.White
            )
        }
    }
}
