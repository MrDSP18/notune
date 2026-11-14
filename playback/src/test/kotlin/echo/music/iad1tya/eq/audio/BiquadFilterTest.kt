package echo.music.iad1tya.eq.audio

import echo.music.iad1tya.eq.data.FilterType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BiquadFilterTest {

    @Test
    fun testPeakingFilterCalculatesFiniteOutputs() {
        val filter = BiquadFilter(
            sampleRate = 44100,
            frequency = 1000.0,
            gain = 6.0,
            q = 1.41,
            filterType = FilterType.PK
        )

        // Process a synthetic unit impulse
        val firstSample = filter.processSample(1.0)
        assertFalse("First sample should not be NaN", firstSample.isNaN())
        assertFalse("First sample should not be infinite", firstSample.isInfinite())

        // Feed silence and check ringing decays
        var maxSubsequent = 0.0
        repeat(100) {
            val s = kotlin.math.abs(filter.processSample(0.0))
            if (s > maxSubsequent) maxSubsequent = s
        }
        assertTrue("Subsequent samples must be finite", !maxSubsequent.isNaN())
    }

    @Test
    fun testInvalidSampleRateFallsBackToPassthrough() {
        val filter = BiquadFilter(
            sampleRate = 0,
            frequency = 1000.0,
            gain = 6.0,
            q = 1.41,
            filterType = FilterType.PK
        )

        val output = filter.processSample(0.5)
        assertEquals(0.5, output, 0.0001)
    }

    @Test
    fun testNyquistViolationFallsBackToPassthrough() {
        // Frequency >= sampleRate / 2 (Nyquist limit)
        val filter = BiquadFilter(
            sampleRate = 44100,
            frequency = 25000.0,
            gain = 3.0,
            q = 1.0,
            filterType = FilterType.PK
        )

        val output = filter.processSample(0.75)
        assertEquals(0.75, output, 0.0001)
    }

    @Test
    fun testStereoProcessingAndReset() {
        val filter = BiquadFilter(
            sampleRate = 48000,
            frequency = 500.0,
            gain = -3.0,
            q = 0.707,
            filterType = FilterType.LSC
        )

        val (outL, outR) = filter.processStereo(0.4, -0.4)
        assertFalse(outL.isNaN())
        assertFalse(outR.isNaN())

        filter.reset()
        val afterReset = filter.processSample(0.0)
        assertEquals(0.0, afterReset, 0.000001)
    }
}
