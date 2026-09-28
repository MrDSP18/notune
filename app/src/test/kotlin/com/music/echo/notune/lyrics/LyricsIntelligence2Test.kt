package com.music.echo.notune.lyrics

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LyricsIntelligence2Test {

    private lateinit var transliterator: LyricsTransliterator
    private lateinit var translationEngine: LyricsTranslationEngine
    private lateinit var singAlongEngine: SingAlongEngine
    private lateinit var router: LyricsTranslationRouter

    @Before
    fun setup() {
        transliterator = LyricsTransliterator()
        translationEngine = LyricsTranslationEngine(null)
        singAlongEngine = SingAlongEngine(null)
        router = LyricsTranslationRouter(transliterator, translationEngine, singAlongEngine)
    }

    @Test
    fun testLyricsDisplayModes() {
        val line = LyricLine(
            id = "1",
            startTime = 0L,
            endTime = 5000L,
            originalText = "ஜூலை காற்றின் நேரத்தில்",
            transliteratedText = "July kaatrin nerathil",
            translatedText = "During the July breeze"
        )

        assertEquals("ஜூலை காற்றின் நேரத்தில்", line.textForDisplayMode(LyricsDisplayMode.ORIGINAL))
        assertEquals("July kaatrin nerathil", line.textForDisplayMode(LyricsDisplayMode.PRONUNCIATION))
        assertEquals("During the July breeze", line.textForDisplayMode(LyricsDisplayMode.MEANING))
        assertTrue(line.textForDisplayMode(LyricsDisplayMode.DUAL_LYRICS).contains("ஜூலை காற்றின் நேரத்தில்"))
        assertTrue(line.textForDisplayMode(LyricsDisplayMode.DUAL_LYRICS).contains("July kaatrin nerathil"))
        assertTrue(line.textForDisplayMode(LyricsDisplayMode.TRIPLE_LYRICS).contains("During the July breeze"))
    }

    @Test
    fun testPhoneticTransliteration() {
        val tamilPron = transliterator.transliterateToPronunciation("ஜூலை காற்றின் நேரத்தில்", LyricsLanguage.TAMIL)
        assertEquals("July kaatrin nerathil", tamilPron)

        val devanagariPron = transliterator.transliterateToPronunciation("केसरिया तेरा इश्क", LyricsLanguage.HINDI)
        assertEquals("Kesariya tera ishq", devanagariPron)
    }

    @Test
    fun testProtectedTokens() {
        val rawText = "Taylor Swift sang until i found you ooh"
        val (protected, tokenMap) = router.protectTokens(rawText, listOf("Taylor Swift"))

        assertTrue(protected.contains("__PROTECTED_TOKEN_"))
        assertEquals("Taylor Swift", tokenMap["__PROTECTED_TOKEN_0__"])

        val restored = router.restoreTokens(protected, tokenMap)
        assertEquals(rawText, restored)
    }

    @Test
    fun testDocumentProcessingAndCaching() = runBlocking {
        val lines = listOf(
            LyricLine(
                id = "line_1",
                startTime = 0L,
                endTime = 3000L,
                originalText = "ஜூலை காற்றின் நேரத்தில்"
            )
        )

        val doc = router.processDocument(
            songId = "song_123",
            lines = lines,
            sourceLanguage = LyricsLanguage.TAMIL,
            targetLanguage = LyricsLanguage.ENGLISH,
            displayMode = LyricsDisplayMode.DUAL_LYRICS,
            artistName = "Anirudh"
        )

        assertNotNull(doc)
        assertEquals("song_123", doc.songId)
        assertEquals(LyricsDisplayMode.DUAL_LYRICS, doc.displayMode)
        assertEquals(1, doc.lines.size)
        assertEquals("July kaatrin nerathil", doc.lines[0].transliteratedText)

        // Repeat call should return cached document instantly
        val cachedDoc = router.processDocument(
            songId = "song_123",
            lines = lines,
            sourceLanguage = LyricsLanguage.TAMIL,
            targetLanguage = LyricsLanguage.ENGLISH,
            displayMode = LyricsDisplayMode.DUAL_LYRICS
        )
        assertEquals(doc, cachedDoc)
    }
}
