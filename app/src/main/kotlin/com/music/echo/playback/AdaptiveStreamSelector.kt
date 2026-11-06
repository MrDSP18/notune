package com.music.echo.playback

import echo.music.iad1tya.constants.AudioQuality
import echo.music.iad1tya.utils.NetworkQuality
import kotlinx.coroutines.delay
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap

object AdaptiveStreamSelector {

    private const val TAG = "AdaptiveStreamSelector"
    private val failedUrls = ConcurrentHashMap<String, Long>()
    private const val FAILED_URL_BLACK_LIST_TTL_MS = 5 * 60 * 1000L // 5 minutes

    fun getTargetBitrate(
        userPreference: AudioQuality,
        networkQuality: NetworkQuality,
        dataSaverEnabled: Boolean = false
    ): Int {
        if (userPreference == AudioQuality.ORIGINAL) {
            return AudioQuality.ORIGINAL.targetBitrateKbps
        }

        if (dataSaverEnabled || networkQuality == NetworkQuality.SLOW_2G_3G) {
            return AudioQuality.DATA_SAVER.targetBitrateKbps
        }

        return when (userPreference) {
            AudioQuality.AUTO -> {
                when (networkQuality) {
                    NetworkQuality.OFFLINE -> 0
                    NetworkQuality.SLOW_2G_3G -> 64
                    NetworkQuality.METERED -> 128
                    NetworkQuality.STABLE_4G -> 192
                    NetworkQuality.EXCELLENT_WIFI -> 320
                }
            }
            AudioQuality.DATA_SAVER -> 64
            AudioQuality.BALANCED -> 128
            AudioQuality.HIGH_QUALITY -> 320
            AudioQuality.ORIGINAL -> 1411
            AudioQuality.OPUS -> 160
        }
    }

    suspend fun <T> retryWithExponentialBackoff(
        maxAttempts: Int = 3,
        initialDelayMs: Long = 1000L,
        factor: Double = 2.0,
        block: suspend () -> T
    ): Result<T> {
        var currentDelay = initialDelayMs
        var lastException: Throwable? = null

        for (attempt in 1..maxAttempts) {
            try {
                val result = block()
                return Result.success(result)
            } catch (e: Throwable) {
                lastException = e
                Timber.tag(TAG).w(e, "Stream attempt $attempt/$maxAttempts failed: ${e.message}")
                if (attempt < maxAttempts) {
                    delay(currentDelay)
                    currentDelay = (currentDelay * factor).toLong()
                }
            }
        }

        return Result.failure(lastException ?: Exception("All $maxAttempts stream attempts failed"))
    }

    fun markUrlFailed(url: String) {
        if (url.isBlank()) return
        failedUrls[url] = System.currentTimeMillis()
        Timber.tag(TAG).d("Marked stream URL as failed: $url")
    }

    fun isUrlBlacklisted(url: String): Boolean {
        val timestamp = failedUrls[url] ?: return false
        if (System.currentTimeMillis() - timestamp > FAILED_URL_BLACK_LIST_TTL_MS) {
            failedUrls.remove(url)
            return false
        }
        return true
    }

    fun clearFailedUrls() {
        failedUrls.clear()
    }
}
