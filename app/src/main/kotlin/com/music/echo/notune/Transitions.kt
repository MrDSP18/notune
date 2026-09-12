
package echo.music.iad1tya.notune

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

object NoTuneTransitions {
    val TabEnter = fadeIn(tween(400)) + slideInHorizontally(tween(400, easing = FastOutSlowInEasing)) { it / 2 }
    val TabExit = fadeOut(tween(300)) + slideOutHorizontally(tween(300, easing = FastOutSlowInEasing)) { -it / 2 }

    val ScreenEnter = scaleIn(initialScale = 0.95f, animationSpec = tween(500)) + fadeIn(tween(500))
    val ScreenExit = scaleOut(targetScale = 1.05f, animationSpec = tween(400)) + fadeOut(tween(400))
}

fun Modifier.noTuneClickAnimation(enabled: Boolean = true): Modifier = if (!enabled) this else this.graphicsLayer {
    // Custom logic for click press/release scale
}
