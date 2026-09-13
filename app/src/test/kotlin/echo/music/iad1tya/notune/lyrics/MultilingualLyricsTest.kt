package echo.music.iad1tya.notune.lyrics

import echo.music.iad1tya.notune.ai.AiEngine
import com.music.echo.notune.lyrics.*
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
