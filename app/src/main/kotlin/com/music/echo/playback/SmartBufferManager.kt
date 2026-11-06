package com.music.echo.playback

import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import echo.music.iad1tya.utils.NetworkQuality

@UnstableApi
object SmartBufferManager {

    fun createLoadControl(
        networkQuality: NetworkQuality = NetworkQuality.STABLE_4G,
        smartBufferingEnabled: Boolean = true
    ): DefaultLoadControl {
        val (minBufferMs, maxBufferMs, bufferForPlaybackMs, bufferForPlaybackAfterRebufferMs) = when {
            !smartBufferingEnabled -> Quadruple(2_500, 30_000, 1_000, 1_500)
            networkQuality == NetworkQuality.SLOW_2G_3G || networkQuality == NetworkQuality.METERED -> {
                Quadruple(15_000, 90_000, 3_500, 5_000)
            }
            networkQuality == NetworkQuality.STABLE_4G -> {
                Quadruple(5_000, 45_000, 1_500, 2_500)
            }
            else -> { // EXCELLENT_WIFI
                Quadruple(2_500, 30_000, 1_000, 1_500)
            }
        }

        return DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                minBufferMs,
                maxBufferMs,
                bufferForPlaybackMs,
                bufferForPlaybackAfterRebufferMs
            )
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()
    }

    private data class Quadruple<A, B, C, D>(
        val first: A,
        val second: B,
        val third: C,
        val fourth: D
    )
}
