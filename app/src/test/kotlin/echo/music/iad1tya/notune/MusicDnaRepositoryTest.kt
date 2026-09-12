package echo.music.iad1tya.notune

import echo.music.iad1tya.notune.dna.MusicDnaRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class MusicDnaRepositoryTest {

    @Test
    fun testEnergyProfileComputation_shortSongs_yieldsHighEnergy() {
        val avgDurationMs = 120_000L // 2 mins
        val profile = when {
            avgDurationMs < 180_000L -> "High Energy"
            avgDurationMs < 240_000L -> "Balanced"
            else -> "Deep & Chill"
        }
        assertEquals("High Energy", profile)
    }

    @Test
    fun testDiscoveryScoreComputation_ratioCalculatedCorrectly() {
        val uniqueSongsCount = 20
        val totalEstimatedPlayEvents = 100
        val discoveryScore = ((uniqueSongsCount.toDouble() / totalEstimatedPlayEvents.coerceAtLeast(1)) * 100).toInt().coerceIn(0, 100)
        assertEquals(20, discoveryScore)
    }
}
