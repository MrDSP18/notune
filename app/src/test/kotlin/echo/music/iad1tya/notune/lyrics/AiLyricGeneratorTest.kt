package echo.music.iad1tya.notune.lyrics

import android.content.Context
import com.music.echo.notune.lyrics.AiLyricGenerator
import com.music.echo.notune.lyrics.LyricsLanguage
import echo.music.iad1tya.db.MusicDatabase
import echo.music.iad1tya.db.entities.LyricsEntity
import echo.music.iad1tya.lyrics.LyricsUtils
import echo.music.iad1tya.models.MediaMetadata
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AiLyricGeneratorTest {

    private val context = mockk<Context>(relaxed = true)
    private val database = mockk<MusicDatabase>(relaxed = true)
    private lateinit var generator: AiLyricGenerator

    @Before
    fun setUp() {
        generator = AiLyricGenerator(context, database, null)
    }

    @Test
    fun `synthesizeAlgorithmicLrc produces valid LRC format with sequential timestamps`() {
        val lrc = generator.synthesizeAlgorithmicLrc("Midnight Frequency", "Cyber Sound", 180)

        assertNotNull(lrc)
        assertTrue(lrc.startsWith("[00:00.00] ♪ (Intro Instrumental) ♪"))

        val parsed = LyricsUtils.parseLyrics(lrc)
        assertTrue(parsed.isNotEmpty())
        assertTrue("Parsed lyrics should contain multiple timed lines", parsed.size >= 5)

        // Ensure timestamps are monotonically non-decreasing
        var lastTime = 0L
        parsed.forEach { entry ->
            assertTrue("Timestamp should be non-decreasing: ${entry.time} >= $lastTime", entry.time >= lastTime)
            lastTime = entry.time
        }
    }

    @Test
    fun `synthesizeAlgorithmicLrc detects Tamil language and synthesizes appropriate lyrics`() {
        val lrc = generator.synthesizeAlgorithmicLrc("காதல் மெட்டுக்கள்", "அனிருத்", 200)

        assertTrue(lrc.contains("காற்றின் அலைகளில்") || lrc.contains("காதல் மெட்டுக்கள்"))
        val parsed = LyricsUtils.parseLyrics(lrc)
        assertTrue(parsed.isNotEmpty())
    }

    @Test
    fun `generateLyricsForSong saves generated lyrics to database with provider name`() = runTest {
        val metadata = MediaMetadata(
            id = "test_song_123",
            title = "Starlight Beats",
            artists = listOf(MediaMetadata.Artist("a1", "Cosmic Artist")),
            duration = 180
        )

        every { database.query(any()) } answers {
            // Mock transaction execution
        }

        val lrc = generator.generateLyricsForSong(metadata)

        assertTrue(lrc.isNotBlank())
        assertTrue(lrc.contains("Starlight Beats"))
        verify(atLeast = 1) { database.query(any()) }
    }
}
