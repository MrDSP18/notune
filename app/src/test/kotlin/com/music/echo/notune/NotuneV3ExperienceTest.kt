package com.music.echo.notune

import com.music.echo.notune.design.components.SoundMarkState
import com.music.echo.notune.design.theme.ArtworkColorExtractor
import com.music.echo.notune.design.theme.NotuneRed
import com.music.echo.notune.ui.navigation.CreateOption
import com.music.echo.notune.ui.navigation.V3Destination
import com.music.echo.notune.ui.queue.QueueItemV3
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NotuneV3ExperienceTest {

    @Test
    fun testSoundMarkStates() {
        val states = SoundMarkState.values()
        assertEquals(6, states.size)
        assertTrue(states.contains(SoundMarkState.PAUSED))
        assertTrue(states.contains(SoundMarkState.PLAYING))
        assertTrue(states.contains(SoundMarkState.DOWNLOADING))
        assertTrue(states.contains(SoundMarkState.AI_ACTIVE))
        assertTrue(states.contains(SoundMarkState.ROOM_MODE))
        assertTrue(states.contains(SoundMarkState.RECORDING))
    }

    @Test
    fun testArtworkColorExtractorFallback() {
        val fallbackPalette = ArtworkColorExtractor.extractPalette(null, NotuneRed)
        assertEquals(NotuneRed, fallbackPalette.dominant)
        assertTrue(fallbackPalette.isDark)
    }

    @Test
    fun testArtworkColorExtractorHexParsing() {
        val palette = ArtworkColorExtractor.extractPalette("#FF0031")
        assertNotNull(palette.dominant)
        assertNotNull(palette.secondary)
        assertNotNull(palette.backgroundGlow)
    }

    @Test
    fun testV3Destinations() {
        val destinations = V3Destination.values()
        assertEquals(5, destinations.size)
        assertEquals(V3Destination.CREATE, destinations[2])
    }

    @Test
    fun testCreateOptions() {
        val options = CreateOption.values()
        assertEquals(6, options.size)
        assertTrue(options.any { it.label == "Start Room" })
        assertTrue(options.any { it.label == "AI Playlist" })
    }

    @Test
    fun testQueueItemV3Model() {
        val item = QueueItemV3(
            id = "t1",
            title = "Munbe Vaa",
            artist = "A.R. Rahman",
            matchPercentage = 94,
            matchTag = "Perfect continuation",
            isNowPlaying = false,
            reasonFactors = listOf("Fits Tamil melody preference", "Energy match 95%")
        )

        assertEquals("t1", item.id)
        assertEquals(94, item.matchPercentage)
        assertEquals(2, item.reasonFactors.size)
    }
}
