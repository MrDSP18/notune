package echo.music.iad1tya.notune.lyrics

import com.music.echo.notune.lyrics.LyricsDisplayMode
import com.music.echo.notune.lyrics.LyricsLanguage
import com.music.echo.notune.lyrics.LyricsTranslationEngine
import com.music.echo.notune.lyrics.LyricsTransliterator
import com.music.echo.notune.lyrics.RealtimeLyricsTranslator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RealtimeLyricsTranslatorTest {

    private lateinit var translator: RealtimeLyricsTranslator
    private val translationEngine = LyricsTranslationEngine(null)
    private val transliterator = LyricsTransliterator()

    @Before
    fun setUp() {
        translator = RealtimeLyricsTranslator(translationEngine, transliterator)
    }

    @Test
    fun `translateLine translates phrase to Tamil correctly`() {
        val translated = translator.translateLine("Until I found you", LyricsLanguage.TAMIL)
        assertEquals("நான் உன்னை கண்டுபிடிக்கும் வரை", translated)
    }

    @Test
    fun `translateLine translates phrase to Hindi and Spanish correctly`() {
        val hindi = translator.translateLine("Until I found you", LyricsLanguage.HINDI)
        assertEquals("जब तक मुझे तुम नहीं मिले", hindi)

        val spanish = translator.translateLine("Until I found you", LyricsLanguage.SPANISH)
        assertEquals("Hasta que te encontré", spanish)
    }

    @Test
    fun `transliterateLine produces readable English pronunciation for Tamil`() {
        val pron = translator.transliterateLine("காதல்", LyricsLanguage.TAMIL)
        assertNotNull(pron)
        assertTrue(pron.isNotBlank())
    }

    @Test
    fun `getDualLine returns primary and secondary correctly for DUAL_LYRICS mode`() {
        val (primary, secondary) = translator.getDualLine(
            "Until I found you",
            LyricsLanguage.TAMIL,
            LyricsDisplayMode.DUAL_LYRICS
        )

        assertEquals("Until I found you", primary)
        assertEquals("நான் உன்னை கண்டுபிடிக்கும் வரை", secondary)
    }

    @Test
    fun `getDualLine returns single line for ORIGINAL and MEANING modes`() {
        val (origPrimary, origSecondary) = translator.getDualLine(
            "Until I found you",
            LyricsLanguage.TAMIL,
            LyricsDisplayMode.ORIGINAL
        )
        assertEquals("Until I found you", origPrimary)
        assertNull(origSecondary)

        val (meanPrimary, meanSecondary) = translator.getDualLine(
            "Until I found you",
            LyricsLanguage.TAMIL,
            LyricsDisplayMode.MEANING
        )
        assertEquals("நான் உன்னை கண்டுபிடிக்கும் வரை", meanPrimary)
        assertNull(meanSecondary)
    }

    @Test
    fun `getSupportedLanguages includes key Indian and International languages`() {
        val supported = translator.getSupportedLanguages()
        assertTrue(supported.contains(LyricsLanguage.ENGLISH))
        assertTrue(supported.contains(LyricsLanguage.TAMIL))
        assertTrue(supported.contains(LyricsLanguage.HINDI))
        assertTrue(supported.contains(LyricsLanguage.SPANISH))
        assertTrue(supported.contains(LyricsLanguage.JAPANESE))
    }
}
