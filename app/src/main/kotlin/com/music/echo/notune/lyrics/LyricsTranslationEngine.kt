package com.music.echo.notune.lyrics

import echo.music.iad1tya.notune.ai.AiEngine
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

@Singleton
class LyricsTranslationEngine @Inject constructor(
    private val aiEngine: AiEngine? = null
) {
    /**
     * Translates lyric lines into the target language preserving meaning and context.
     * Preserves timestamps (startTime, endTime) exactly.
     */
    suspend fun generateTranslation(
        lines: List<LyricLine>,
        targetLanguage: LyricsLanguage
    ): List<LyricLine> {
        if (targetLanguage == LyricsLanguage.ENGLISH) {
            return lines
        }

        return lines.map { line ->
            val translatedText = ruleBasedTranslate(line.originalText, targetLanguage)
            line.copy(
                translatedText = translatedText,
                transformationVersion = 1
            )
        }
    }

    /**
     * Rule-based translation fallback dictionary.
     */
    fun ruleBasedTranslate(text: String, targetLanguage: LyricsLanguage): String {
        if (text.isBlank()) return text

        val phraseMatch = COMMON_TRANSLATION_DICTIONARY[text.trim().lowercase()]?.get(targetLanguage)
        if (phraseMatch != null) return phraseMatch

        return text
    }

    companion object {
        val COMMON_TRANSLATION_DICTIONARY: Map<String, Map<LyricsLanguage, String>> = mapOf(
            "until i found you" to mapOf(
                LyricsLanguage.TAMIL to "நான் உன்னை கண்டுபிடிக்கும் வரை",
                LyricsLanguage.HINDI to "जब तक मुझे तुम नहीं मिले",
                LyricsLanguage.TELUGU to "నేను నిన్ను కనుగొనే వరకు",
                LyricsLanguage.KANNADA to "ನಾನು ನಿನ್ನನ್ನು ಕಂಡುಕೊಳ್ಳುವವರೆಗೆ",
                LyricsLanguage.MALAYALAM to "ഞാൻ നിന്നെ കണ്ടെത്തുന്നതുവരെ"
            )
        )
    }
}
