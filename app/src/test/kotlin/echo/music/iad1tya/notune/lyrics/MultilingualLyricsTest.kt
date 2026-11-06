package echo.music.iad1tya.notune.lyrics

import echo.music.iad1tya.notune.ai.AiEngine
import com.music.echo.notune.lyrics.*
import android.content.Context
import io.mockk.mockk
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import echo.music.iad1tya.lyrics.LyricsUtils
import echo.music.iad1tya.lyrics.LyricsEntry
import echo.music.iad1tya.lyrics.LyricsTranslationHelper
import echo.music.iad1tya.db.entities.LyricsEntity
import org.junit.Assert.*
import org.junit.Test

class MultilingualLyricsTest {

    @Test
    fun `LyricsLanguage lookup resolves correctly`() {
        assertEquals(LyricsLanguage.TAMIL, LyricsLanguage.fromCode("ta"))
        assertEquals(LyricsLanguage.HINDI, LyricsLanguage.fromCode("hi"))
        assertEquals(LyricsLanguage.TELUGU, LyricsLanguage.fromCode("te"))
        assertEquals(LyricsLanguage.ENGLISH, LyricsLanguage.fromCode("invalid"))
    }

    @Test
    fun `SingAlongEngine transliterates phrases into target scripts`() {
        val engine = SingAlongEngine()

        val tamilPhonetic = engine.ruleBasedTransliterate("Until I found you", LyricsLanguage.TAMIL)
        assertEquals("அன்டில் ஐ ஃபவுண்ட் யூ", tamilPhonetic)

        val hindiPhonetic = engine.ruleBasedTransliterate("Until I found you", LyricsLanguage.HINDI)
        assertEquals("अंटिल आई फाउंड यू", hindiPhonetic)
    }

    @Test
    fun `LyricsTranslationEngine translates phrases into target languages`() {
        val engine = LyricsTranslationEngine()

        val tamilTranslation = engine.ruleBasedTranslate("Until I found you", LyricsLanguage.TAMIL)
        assertEquals("நான் உன்னை கண்டுபிடிக்கும் வரை", tamilTranslation)
    }

    @Test
    fun `romanization uses original lyric language even when target is English`() {
        val original = LyricLine("1", 100L, 200L, "காதல்", language = "ta")
        val text = LyricsTransliterator().transliterateToPronunciation(
            original.originalText, LyricsLanguage.fromCode(original.language)
        )
        assertEquals("kaadhal", text)
        assertEquals("காதல்", original.originalText)
    }

    @Test
    fun `repository romanizes by source and refreshes changed lyrics`() = runBlocking {
        val repository = MultilingualLyricsRepository(
            mockk<Context>(relaxed = true), SingAlongEngine(), LyricsTranslationEngine(), LyricsTransliterator()
        )
        val firstLine = LyricLine("1", 100L, 200L, "காதல்", language = "ta")
        val first = repository.getTransformedLyrics("song", listOf(firstLine), LyricsLanguage.ENGLISH, LyricsMode.ROMANIZED)
        assertEquals("kaadhal", first.transformedLines.single().transliteratedText)
        assertEquals(100L, first.transformedLines.single().startTime)
        assertEquals("காதல்", first.transformedLines.single().originalText)

        val cached = repository.getTransformedLyrics("song", listOf(firstLine), LyricsLanguage.ENGLISH, LyricsMode.ROMANIZED)
        assertTrue(cached.isCached)

        val updated = repository.getTransformedLyrics(
            "song", listOf(firstLine.copy(originalText = "அன்பே")), LyricsLanguage.ENGLISH, LyricsMode.ROMANIZED
        )
        assertFalse(updated.isCached)
        assertEquals("அன்பே", updated.transformedLines.single().originalText)
        assertEquals("anbe", updated.transformedLines.single().transliteratedText)
    }

    @Test
    fun `unavailable meaning translation leaves original text intact`() = runBlocking {
        val line = LyricLine("1", 100L, 200L, "Unknown lyric")
        val translated = LyricsTranslationEngine().generateTranslation(listOf(line), LyricsLanguage.TAMIL).single()
        assertNull(translated.translatedText)
        assertEquals("Unknown lyric", translated.activeText(LyricsMode.TRANSLATION))

        val known = LyricsTranslationEngine().generateTranslation(
            listOf(line.copy(originalText = "Until I found you")), LyricsLanguage.TAMIL
        ).single()
        assertEquals("நான் உன்னை கண்டுபிடிக்கும் வரை", known.translatedText)
    }

    @Test
    fun `synced line lookup handles partial timing and seeking`() {
        assertTrue(LyricsUtils.parseLyrics("[Verse 1]\nNo timestamps here").isEmpty())
        val lines = LyricsUtils.parseLyrics("[00:01.00] First\nUnmarked line\n[00:03.00] Next")
        assertEquals(2, lines.size)
        assertEquals(-1, LyricsUtils.findCurrentLineIndex(lines, 0L))
        assertEquals(0, LyricsUtils.findCurrentLineIndex(lines, 1500L))
        assertEquals(1, LyricsUtils.findCurrentLineIndex(lines, 3500L))
        assertEquals(0, LyricsUtils.findCurrentLineIndex(lines, 1500L))
        assertEquals(-1, LyricsUtils.findCurrentLineIndex(emptyList(), 1500L))
    }

    @Test
    fun `unconfigured translation provider never reports invented translation`() = runBlocking {
        val line = LyricsEntry(1000L, "Original lyric")
        LyricsTranslationHelper.resetStatus()
        LyricsTranslationHelper.setCompositionActive(true)
        coroutineScope {
            LyricsTranslationHelper.translateLyrics(
                lyrics = listOf(line),
                targetLanguage = "ta",
                apiKey = "",
                baseUrl = "",
                model = "",
                mode = "Literal",
                scope = this,
                context = mockk<Context>(relaxed = true),
                provider = "OpenRouter"
            )
        }

        assertTrue(LyricsTranslationHelper.status.value is LyricsTranslationHelper.TranslationStatus.Error)
        assertNull(line.translatedTextFlow.value)
        assertFalse(LyricsTranslationHelper.hasActiveTranslations.value)
        LyricsTranslationHelper.cancelTranslation()
    }

    @Test
    fun `translation cache distinguishes source lyrics with colliding hashes`() {
        assertEquals("Aa".hashCode(), "BB".hashCode())
        LyricsTranslationHelper.clearCache()
        LyricsTranslationHelper.loadTranslationsFromDatabase(
            lyrics = listOf(LyricsEntry(0L, "Aa")),
            lyricsEntity = LyricsEntity("song", "Aa", translatedLyrics = "Translation", translationLanguage = "ta", translationMode = "Literal"),
            targetLanguage = "ta",
            mode = "Literal"
        )
        assertNull(LyricsTranslationHelper.getCachedTranslations(listOf(LyricsEntry(0L, "BB")), "Literal", "ta"))
        LyricsTranslationHelper.clearCache()
    }

    @Test
    fun `incomplete persisted translation is not displayed`() {
        val lines = listOf(LyricsEntry(0L, "First"), LyricsEntry(1000L, "Second"))
        LyricsTranslationHelper.loadTranslationsFromDatabase(
            lyrics = lines,
            lyricsEntity = LyricsEntity("song", "First\nSecond", translatedLyrics = "Only first", translationLanguage = "ta", translationMode = "Literal"),
            targetLanguage = "ta",
            mode = "Literal"
        )
        assertFalse(LyricsTranslationHelper.hasActiveTranslations.value)
        assertTrue(lines.all { it.translatedTextFlow.value == null })
    }

    @Test
    fun `incomplete provider response cannot become a successful translation`() = runBlocking {
        val lines = listOf(LyricsEntry(0L, "First"), LyricsEntry(1000L, "Second"))
        LyricsTranslationHelper.clearCache()
        LyricsTranslationHelper.setCompositionActive(true)
        LyricsTranslationHelper.externalNeuralTranslator = { _, _ -> Result.success(listOf("Only one")) }
        try {
            coroutineScope {
                LyricsTranslationHelper.translateLyrics(
                    lyrics = lines,
                    targetLanguage = "ta",
                    apiKey = "",
                    baseUrl = "",
                    model = "",
                    mode = "Literal",
                    scope = this,
                    context = mockk<Context>(relaxed = true),
                    provider = "NØTUNE Neural"
                )
            }
            assertTrue(LyricsTranslationHelper.status.value is LyricsTranslationHelper.TranslationStatus.Error)
            assertFalse(LyricsTranslationHelper.hasActiveTranslations.value)
            assertTrue(lines.all { it.translatedTextFlow.value == null })
            assertNull(LyricsTranslationHelper.getCachedTranslations(lines, "Literal", "ta"))
        } finally {
            LyricsTranslationHelper.externalNeuralTranslator = null
            LyricsTranslationHelper.cancelTranslation()
        }
    }

    @Test
    fun `LyricLine activeText returns correct representation for each mode`() {
        val line = LyricLine(
            id = "1",
            startTime = 12200L,
            endTime = 15400L,
            originalText = "Until I found you",
            translatedText = "நான் உன்னை கண்டுபிடிக்கும் வரை",
            transliteratedText = "அன்டில் ஐ ஃபவுண்ட் யூ"
        )

        assertEquals("Until I found you", line.activeText(LyricsMode.ORIGINAL))
        assertEquals("நான் உன்னை கண்டுபிடிக்கும் வரை", line.activeText(LyricsMode.TRANSLATION))
        assertEquals("அன்டில் ஐ ஃபவுண்ட் யூ", line.activeText(LyricsMode.SING_ALONG))
    }
}
