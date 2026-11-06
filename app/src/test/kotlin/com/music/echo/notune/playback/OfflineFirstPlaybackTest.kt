package com.music.echo.notune.playback

import com.music.echo.playback.AdaptiveStreamSelector
import com.music.echo.playback.OfflinePlaybackGuard
import com.music.echo.playback.SmartBufferManager
import echo.music.iad1tya.constants.AudioQuality
import echo.music.iad1tya.utils.NetworkQuality
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OfflineFirstPlaybackTest {

    @Test
    fun testAdaptiveStreamBitrateTargets() {
        val originalTarget = AdaptiveStreamSelector.getTargetBitrate(
            userPreference = AudioQuality.ORIGINAL,
            networkQuality = NetworkQuality.SLOW_2G_3G,
            dataSaverEnabled = true
        )
        assertEquals(1411, originalTarget)

        val slowNetTarget = AdaptiveStreamSelector.getTargetBitrate(
            userPreference = AudioQuality.HIGH_QUALITY,
            networkQuality = NetworkQuality.SLOW_2G_3G,
            dataSaverEnabled = false
        )
        assertEquals(64, slowNetTarget)

        val autoWifiTarget = AdaptiveStreamSelector.getTargetBitrate(
            userPreference = AudioQuality.AUTO,
            networkQuality = NetworkQuality.EXCELLENT_WIFI,
            dataSaverEnabled = false
        )
        assertEquals(320, autoWifiTarget)
    }

    @Test
    fun testSmartBufferManagerLoadControl() {
        val slowLoadControl = SmartBufferManager.createLoadControl(
            networkQuality = NetworkQuality.SLOW_2G_3G,
            smartBufferingEnabled = true
        )
        assertNotNull(slowLoadControl)

        val fastLoadControl = SmartBufferManager.createLoadControl(
            networkQuality = NetworkQuality.EXCELLENT_WIFI,
            smartBufferingEnabled = true
        )
        assertNotNull(fastLoadControl)
    }

    @Test
    fun testExponentialBackoffRetrySuccess() = runBlocking {
        var attempts = 0
        val result = AdaptiveStreamSelector.retryWithExponentialBackoff(maxAttempts = 3) {
            attempts++
            if (attempts < 2) throw IllegalStateException("Network glitch")
            "StreamURL_OK"
        }

        assertTrue(result.isSuccess)
        assertEquals("StreamURL_OK", result.getOrNull())
        assertEquals(2, attempts)
    }

    @Test
    fun testUrlBlacklisting() {
        val testUrl = "https://failing-stream.com/audio.mp3"
        assertFalse(AdaptiveStreamSelector.isUrlBlacklisted(testUrl))

        AdaptiveStreamSelector.markUrlFailed(testUrl)
        assertTrue(AdaptiveStreamSelector.isUrlBlacklisted(testUrl))

        AdaptiveStreamSelector.clearFailedUrls()
        assertFalse(AdaptiveStreamSelector.isUrlBlacklisted(testUrl))
    }

    @Test
    fun testLocalMediaCheck() {
        assertTrue(OfflinePlaybackGuard.isLocalOrDownloadedMedia("content://media/external/audio/media/100"))
        assertTrue(OfflinePlaybackGuard.isLocalOrDownloadedMedia("file:///sdcard/Music/song.mp3"))
        assertTrue(OfflinePlaybackGuard.isLocalOrDownloadedMedia("/storage/emulated/0/Music/song.flac"))
        assertFalse(OfflinePlaybackGuard.isLocalOrDownloadedMedia("https://youtube.com/watch?v=12345"))
    }
}
