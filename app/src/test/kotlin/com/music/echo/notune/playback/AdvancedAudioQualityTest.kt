package com.music.echo.notune.playback

import com.music.echo.playback.AudioEffectManager
import echo.music.iad1tya.eq.EqualizerService
import echo.music.iad1tya.eq.data.FilterType
import echo.music.iad1tya.eq.data.ParametricEQ
import echo.music.iad1tya.eq.data.ParametricEQBand
import echo.music.iad1tya.eq.data.SavedEQProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AdvancedAudioQualityTest {

    @Test
    fun testEqualizerServiceProfileApplication() {
        val eqService = EqualizerService()
        val profile = SavedEQProfile(
            id = "test_rock",
            name = "Test Rock",
            deviceModel = "Headphones",
            preamp = -1.0,
            bands = listOf(
                ParametricEQBand(frequency = 60.0, gain = 4.0, filterType = FilterType.PK),
                ParametricEQBand(frequency = 1000.0, gain = -1.5, filterType = FilterType.PK),
                ParametricEQBand(frequency = 12000.0, gain = 5.0, filterType = FilterType.HSC)
            )
        )

        val result = eqService.applyProfile(profile)
        assertTrue(result.isSuccess)
        assertNotNull(eqService.getEqualizerInfo())
    }

    @Test
    fun testParametricEqBandConfiguration() {
        val parametricEq = ParametricEQ(
            preamp = -2.0,
            bands = listOf(
                ParametricEQBand(frequency = 31.0, gain = 6.0, filterType = FilterType.LSC),
                ParametricEQBand(frequency = 16000.0, gain = 3.0, filterType = FilterType.HSC)
            )
        )

        assertEquals(-2.0, parametricEq.preamp, 0.01)
        assertEquals(2, parametricEq.bands.size)
        assertEquals(FilterType.LSC, parametricEq.bands[0].filterType)
    }

    @Test
    fun testAudioEffectManagerLifecycle() {
        val effectManager = AudioEffectManager()
        effectManager.attachAudioSession(1001)
        effectManager.setLoudnessGain(gainMb = 300, enabled = true)
        effectManager.release()
    }
}
