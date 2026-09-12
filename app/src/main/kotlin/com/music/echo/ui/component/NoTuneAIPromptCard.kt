
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White.copy(alpha = 0.08f), Color.Transparent)
                ),
                shape = RoundedCornerShape(4.dp)
            )
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    text = "HELLO, I AM NØTUNE",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = NothingFont,
                        letterSpacing = 2.sp,
                        color = Color.White
                    )
                )
                Text(
                    text = "Your futuristic music ecosystem.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }
            
            Icon(
                painter = painterResource(R.drawable.sparks),
                contentDescription = null,
                tint = Color(0xFFFF0031),
                modifier = Modifier.size(32.dp)
            )
        }
        
        Spacer(Modifier.height(24.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onAskClick,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(2.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
            ) {
                Text("ASK", style = MaterialTheme.typography.labelLarge.copy(fontFamily = NothingFont))
            }

            Button(
                onClick = onReplayClick,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(2.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f), contentColor = Color.White)
            ) {
                Text("REPLAY", style = MaterialTheme.typography.labelLarge.copy(fontFamily = NothingFont))
            }
            
            OutlinedButton(
                onClick = onLabClick,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(2.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
            ) {
                Text("LAB", style = MaterialTheme.typography.labelLarge.copy(fontFamily = NothingFont))
            }
        }
    }
}
