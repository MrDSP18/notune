package com.music.echo.notune.lyrics

import echo.music.iad1tya.notune.ai.AiEngine
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

@Singleton
class SingAlongEngine @Inject constructor(
    private val aiEngine: AiEngine? = null
) {
    /**
     * Converts lyric lines into Sing-Along (phonetic transliteration) representation.
     * Preserves timestamps (startTime, endTime) exactly.
     */
    suspend fun generateSingAlong(
        lines: List<LyricLine>,
        targetLanguage: LyricsLanguage
    ): List<LyricLine> {
        if (targetLanguage == LyricsLanguage.ENGLISH) {
            return lines
        }

        return lines.map { line ->
            val phoneticText = ruleBasedTransliterate(line.originalText, targetLanguage)
            line.copy(
                transliteratedText = phoneticText,
                transformationVersion = 1
            )
        }
    }

    /**
     * Language-aware rule-based phonetic transliteration map for quick offline conversion.
     */
    fun ruleBasedTransliterate(text: String, targetLanguage: LyricsLanguage): String {
        if (text.isBlank()) return text

        // Check common phrase dictionary first for exact matches
        val phraseMatch = COMMON_PHRASE_DICTIONARY[text.trim().lowercase()]?.get(targetLanguage)
        if (phraseMatch != null) return phraseMatch

        // Fallback to word-by-word / character mapping
        val words = text.split(Regex("\\s+"))
        return words.joinToString(" ") { word ->
            val cleanWord = word.lowercase().replace(Regex("[^a-z]"), "")
            COMMON_WORD_DICTIONARY[cleanWord]?.get(targetLanguage)
                ?: phoneticFallback(word, targetLanguage)
        }
    }

    private fun phoneticFallback(word: String, targetLanguage: LyricsLanguage): String {
        // Fallback mapping for phonetic character rendering
        return when (targetLanguage) {
            LyricsLanguage.TAMIL -> word.lowercase()
                .replace("ph", "ஃபி").replace("th", "த்").replace("sh", "ஷ்")
                .replace("ch", "ச்").replace("a", "அ").replace("b", "பி")
                .replace("c", "சி").replace("d", "டி").replace("e", "இ")
                .replace("f", "எஃப்").replace("g", "ஜி").replace("h", "எச்")
                .replace("i", "ஐ").replace("j", "ஜே").replace("k", "கே")
                .replace("l", "எல்").replace("m", "எம்").replace("n", "என்")
                .replace("o", "ஓ").replace("p", "பி").replace("q", "க்யூ")
                .replace("r", "ஆர்").replace("s", "எஸ்").replace("t", "டி")
                .replace("u", "யூ").replace("v", "வி").replace("w", "டபிள்யூ")
                .replace("x", "எக்ஸ்").replace("y", "ஒய்").replace("z", "செட்")
            LyricsLanguage.HINDI -> word.lowercase()
                .replace("th", "थ").replace("sh", "श").replace("ch", "च")
                .replace("a", "अ").replace("b", "ब").replace("c", "स")
                .replace("d", "ड").replace("e", "इ").replace("f", "फ")
                .replace("g", "ग").replace("h", "ह").replace("i", "आई")
                .replace("j", "ज").replace("k", "क").replace("l", "ल")
                .replace("m", "म").replace("n", "न").replace("o", "ओ")
                .replace("p", "प").replace("r", "र").replace("s", "स")
                .replace("t", "ट").replace("u", "यू").replace("v", "व")
                .replace("w", "व").replace("y", "वाय").replace("z", "ज़")
            LyricsLanguage.TELUGU -> word.lowercase()
                .replace("th", "త్").replace("sh", "ష్").replace("ch", "చ్")
                .replace("a", "అ").replace("b", "బి").replace("c", "సి")
                .replace("d", "డి").replace("e", "ఇ").replace("f", "ఎఫ్")
                .replace("g", "జి").replace("h", "హెచ్").replace("i", "ఐ")
                .replace("j", "జె").replace("k", "కె").replace("l", "ఎల్")
                .replace("m", "ఎమ్").replace("n", "ఎన్").replace("o", "ఒ")
                .replace("p", "పి").replace("r", "ఆర్").replace("s", "ఎస్")
                .replace("t", "టి").replace("u", "యు").replace("v", "వి")
            else -> word
        }
    }

    companion object {
        val COMMON_PHRASE_DICTIONARY: Map<String, Map<LyricsLanguage, String>> = mapOf(
            "until i found you" to mapOf(
                LyricsLanguage.TAMIL to "அன்டில் ஐ ஃபவுண்ட் யூ",
                LyricsLanguage.HINDI to "अंटिल आई फाउंड यू",
                LyricsLanguage.TELUGU to "అంటిల్ ఐ ఫౌండ్ యూ",
                LyricsLanguage.KANNADA to "ಅಂಟಿಲ್ ಐ ಫೌಂಡ್ ಯು",
                LyricsLanguage.MALAYALAM to "അന്റിൽ ഐ ഫൗണ്ട് യൂ"
            )
        )

        val COMMON_WORD_DICTIONARY: Map<String, Map<LyricsLanguage, String>> = mapOf(
            "until" to mapOf(LyricsLanguage.TAMIL to "அன்டில்", LyricsLanguage.HINDI to "अंटिल", LyricsLanguage.TELUGU to "అంటిల్"),
            "i" to mapOf(LyricsLanguage.TAMIL to "ஐ", LyricsLanguage.HINDI to "आई", LyricsLanguage.TELUGU to "ఐ"),
            "found" to mapOf(LyricsLanguage.TAMIL to "ஃபவுண்ட்", LyricsLanguage.HINDI to "फाउंड", LyricsLanguage.TELUGU to "ఫౌండ్"),
            "you" to mapOf(LyricsLanguage.TAMIL to "யூ", LyricsLanguage.HINDI to "यू", LyricsLanguage.TELUGU to "యూ"),
            "love" to mapOf(LyricsLanguage.TAMIL to "லவ்", LyricsLanguage.HINDI to "लव", LyricsLanguage.TELUGU to "లవ్"),
            "baby" to mapOf(LyricsLanguage.TAMIL to "பேபி", LyricsLanguage.HINDI to "बेबी", LyricsLanguage.TELUGU to "బేబీ")
        )
    }
}
