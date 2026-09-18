package echo.music.iad1tya.notune.audio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import echo.music.iad1tya.models.PlaybackState
import echo.music.iad1tya.constants.AnimationLevel
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * Procedural Visualizer Engine for NØTUNE.
 * This engine generates visual states based on truthful playback telemetry.
 * It strictly distinguishes between REAL data and PROCEDURAL animation.
 */
class ProceduralVisualizerEngine {

    /**
     * Represents the visual state of a single bar in the waveform.
     */
    data class WaveformBar(
        val heightFactor: Float,
        val opacity: Float
    )

    /**
     * Generates a frame of the procedural waveform based on the current playback state.
     */
    @Composable
    fun rememberWaveformFrame(
        playbackState: PlaybackState,
        animationLevel: AnimationLevel = AnimationLevel.FULL,
        barCount: Int = 32
    ): List<WaveformBar> {
        var frame by remember { mutableStateOf(List(barCount) { WaveformBar(0.1f, 0.5f) }) }

        LaunchedEffect(playbackState.isPlaying, animationLevel) {
            if (!playbackState.isPlaying || animationLevel == AnimationLevel.OFF) {
                // Transition to idle state smoothly
                frame = frame.map { it.copy(heightFactor = 0.05f, opacity = 0.3f) }
                return@LaunchedEffect
            }

            val refreshDelay = when (animationLevel) {
                AnimationLevel.FULL -> 80L
                AnimationLevel.REDUCED -> 160L
                AnimationLevel.MINIMAL -> 320L
                else -> 80L
            }

            while (playbackState.isPlaying) {
                // Procedural generation driven by "energy"
                // In a real implementation with FFT, this would use actual frequency bands.
                // Here we use a procedural approach but label it as such in the UI.
                frame = List(barCount) { index ->
                    val targetHeight = 0.2f + Random.nextFloat() * 0.6f
                    WaveformBar(
                        heightFactor = targetHeight,
                        opacity = 0.6f + Random.nextFloat() * 0.4f
                    )
                }
                delay(refreshDelay)
            }
        }

        return frame
    }
}
