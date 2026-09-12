
package echo.music.iad1tya.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun NoTuneSpectrum(
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val barCount = 32
    val animatables = remember { List(barCount) { Animatable(0.2f) } }
    
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (true) {
                animatables.forEach { animatable ->
                    launch {
                        animatable.animateTo(
                            targetValue = Random.nextFloat().coerceIn(0.1f, 1f),
                            animationSpec = tween(Random.nextInt(100, 300), easing = LinearEasing)
                        )
                    }
                }
                kotlinx.coroutines.delay(150)
            }
        } else {
            animatables.forEach { it.animateTo(0.1f) }
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
    ) {
        val barWidth = size.width / (barCount * 2)
        val space = barWidth
        
        animatables.forEachIndexed { index, animatable ->
            val x = (index * (barWidth + space)) + (size.width / 4)
            val barHeight = size.height * animatable.value
            
            drawLine(
                color = Color.White.copy(alpha = 0.5f),
                start = Offset(x, size.height / 2 - barHeight / 2),
                end = Offset(x, size.height / 2 + barHeight / 2),
                strokeWidth = barWidth
            )
        }
    }
}
