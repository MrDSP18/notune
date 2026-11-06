package com.music.echo.notune

import com.music.echo.notune.lyrics.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class LyricsPipelineTest {

    private lateinit var lyricsResolver: LyricsResolver
    private lateinit var lyricsSyncEngine: LyricsSyncEngine

    @Before
    fun setUp() {
        lyricsResolver = LyricsResolver()
        lyricsSyncEngine = LyricsSyncEngine()
    }

    @Test
    fun testLyricsResolverZeroHallucinationFallback() {
        val resolved = lyricsResolver.resolveLyrics(
            songId = "unknown_song",
            providerLines = null,
            cachedLines = null,
            userLines = null
        )

        assertFalse("Unavailable lyrics should mark isAvailable as false", resolved.isAvailable)
        assertEquals(LyricsSourceType.UNAVAILABLE, resolved.sourceType)
        assertEquals("Lyrics unavailable", resolved.statusMessage)
        assertTrue("Lines list should be empty", resolved.lines.isEmpty())
    }

    @Test
    fun testLyricsResolverSyncedProviderPriority() {
        val syncedLine = LyricLine(id = "1", startTime = 1000L, endTime = 5000L, originalText = "Hello World")
        val resolved = lyricsResolver.resolveLyrics(
            songId = "song_123",
            providerLines = listOf(syncedLine),
            cachedLines = null,
            userLines = null
        )

        assertTrue(resolved.isAvailable)
        assertEquals(LyricsSourceType.SYNCED_PROVIDER, resolved.sourceType)
        assertEquals(1, resolved.lines.size)
        assertEquals("Hello World", resolved.lines.first().originalText)
    }

    @Test
    fun testLyricsSyncEnginePositionSync() {
        val lines = listOf(
            LyricLine(id = "1", startTime = 0L, endTime = 4000L, originalText = "Line 1"),
            LyricLine(id = "2", startTime = 4000L, endTime = 8000L, originalText = "Line 2"),
            LyricLine(id = "3", startTime = 8000L, endTime = 12000L, originalText = "Line 3")
        )

        val activeState = lyricsSyncEngine.syncWithPosition(5500L, lines)
        assertEquals(1, activeState.currentLineIndex)
        assertEquals("Line 2", activeState.currentLine?.originalText)
        assertEquals("Line 3", activeState.nextLine?.originalText)
        assertTrue("Progress percentage should be > 0", activeState.lineProgressPct > 0f)
    }

    @Test
    fun testLanguageRegistryLookup() {
        val ta = LyricsLanguage.fromCode("ta")
        assertEquals(LyricsLanguage.TAMIL, ta)
        assertEquals("தமிழ்", ta.nativeName)
    }

    @Test
    fun testFlexibleLrcTimestampParsing() {
        val lineRegex = echo.music.iad1tya.lyrics.LyricsUtils.LINE_REGEX
        val timeRegex = echo.music.iad1tya.lyrics.LyricsUtils.TIME_REGEX

        val sampleLrcLine1 = "[01:23.45] Flexible Timestamp Line"
        val sampleLrcLine2 = "[1:23:456] Single Digit Minute Line"

        assertTrue(lineRegex.matches(sampleLrcLine1))
        assertTrue(lineRegex.matches(sampleLrcLine2))
    }
}
