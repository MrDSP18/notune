
package echo.music.iad1tya.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import echo.music.iad1tya.ui.theme.NothingFont

@Composable
fun NoTuneLogo(
    modifier: Modifier = Modifier,
    showText: Boolean = true
) {
    val appName = echo.music.iad1tya.notune.rememberAppName()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(24.dp)
                .background(Color(0xFFFF0031), CircleShape)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(Color.White, CircleShape)
            )
        }
        
        if (showText) {
            Spacer(Modifier.width(8.dp))
            Text(
                text = appName.uppercase(),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = NothingFont,
                    letterSpacing = 2.sp,
                    color = Color.White
                )
            )
        }
    }
}
