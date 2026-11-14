package com.music.echo.notune.lyrics

import echo.music.iad1tya.notune.ai.AiEngine
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

@Singleton
class LyricsTranslationEngine @Inject constructor() {
    private var aiEngine: AiEngine? = null

    constructor(aiEngine: AiEngine?) : this() {
        this.aiEngine = aiEngine
    }
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

        val engine = aiEngine
        if (engine != null) {
            val fullText = lines.joinToString("\n") { it.originalText }
            val prompt = """
                Translate the following lyrics into ${targetLanguage.name}. 
                Maintain the original emotion, slang, and cultural nuances. 
                Respond ONLY with the translated lines, one per line, matching the original count exactly.
                
                LYRICS:
                $fullText
            """.trimIndent()

            val result = engine.runNeuralMatrixLoop(prompt)
            result.onSuccess { response ->
                val translatedLines = response.text.lines().filter { it.isNotBlank() }
                if (translatedLines.size == lines.size) {
                    return lines.mapIndexed { index, line ->
                        line.copy(translatedText = translatedLines[index], transformationVersion = 2)
                    }
                }
                Timber.w("LYRICS_AI: Line count mismatch in translation. Falling back to rule-based.")
            }.onFailure {
                Timber.e(it, "LYRICS_AI: Neural translation failed.")
            }
        }

        return lines.map { line ->
            val translatedText = ruleBasedTranslate(line.originalText, targetLanguage)
            line.copy(
                translatedText = translatedText.takeIf { it != line.originalText },
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

        // Check common word substitutions if full phrase is not found
        val words = text.split(" ")
        if (words.size in 1..8) {
            var translatedAny = false
            val translatedWords = words.map { word ->
                val clean = word.lowercase().trim { !it.isLetterOrDigit() }
                val match = COMMON_WORDS_MAP[clean]?.get(targetLanguage)
                if (match != null) {
                    translatedAny = true
                    word.replace(clean, match, ignoreCase = true)
                } else {
                    word
                }
            }
            if (translatedAny) {
                return translatedWords.joinToString(" ")
            }
        }

        return text
    }

    companion object {
        val COMMON_TRANSLATION_DICTIONARY: Map<String, Map<LyricsLanguage, String>> = mapOf(
            "until i found you" to mapOf(
                LyricsLanguage.TAMIL to "நான் உன்னை கண்டுபிடிக்கும் வரை",
                LyricsLanguage.HINDI to "जब तक मुझे तुम नहीं मिले",
                LyricsLanguage.TELUGU to "నేను నిన్ను కనుగొనే వరకు",
                LyricsLanguage.KANNADA to "ನಾನು ನಿನ್ನನ್ನು ಕಂಡುಕೊಳ್ಳುವವರೆಗೆ",
                LyricsLanguage.MALAYALAM to "ഞാൻ നിന്നെ കണ്ടെത്തുന്നതുവരെ",
                LyricsLanguage.SPANISH to "Hasta que te encontré",
                LyricsLanguage.FRENCH to "Jusqu'à ce que je te trouve",
                LyricsLanguage.JAPANESE to "あなたを見つけるまで",
                LyricsLanguage.KOREAN to "너를 찾을 때까지"
            ),
            "i love you" to mapOf(
                LyricsLanguage.TAMIL to "நான் உன்னை காதலிக்கிறேன்",
                LyricsLanguage.HINDI to "मैं तुमसे प्यार करता हूँ",
                LyricsLanguage.TELUGU to "నేను నిన్ను ప్రేమిస్తున్నాను",
                LyricsLanguage.SPANISH to "Te amo",
                LyricsLanguage.FRENCH to "Je t'aime",
                LyricsLanguage.JAPANESE to "愛してる",
                LyricsLanguage.KOREAN to "사랑해"
            ),
            "stay with me" to mapOf(
                LyricsLanguage.TAMIL to "என்னுடன் இரு",
                LyricsLanguage.HINDI to "मेरे साथ रहो",
                LyricsLanguage.TELUGU to "నాతో ఉండు",
                LyricsLanguage.SPANISH to "Quédate conmigo",
                LyricsLanguage.FRENCH to "Reste avec moi",
                LyricsLanguage.JAPANESE to "そばにいて",
                LyricsLanguage.KOREAN to "내 곁에 있어줘"
            ),
            "hold my hand" to mapOf(
                LyricsLanguage.TAMIL to "என் கையைப் பிடி",
                LyricsLanguage.HINDI to "मेरा हाथ थाम लो",
                LyricsLanguage.TELUGU to "నా చేయి పట్టుకో",
                LyricsLanguage.SPANISH to "Toma mi mano",
                LyricsLanguage.FRENCH to "Prends ma main",
                LyricsLanguage.JAPANESE to "手をつないで",
                LyricsLanguage.KOREAN to "내 손을 잡아"
            ),
            "never let you go" to mapOf(
                LyricsLanguage.TAMIL to "உன்னை ஒருபோதும் போக விடமாட்டேன்",
                LyricsLanguage.HINDI to "तुम्हें कभी जाने नहीं दूंगा",
                LyricsLanguage.TELUGU to "నిన్ను ఎప్పటికీ వదలలేను",
                LyricsLanguage.SPANISH to "Nunca te dejaré ir",
                LyricsLanguage.FRENCH to "Ne jamais te laisser partir",
                LyricsLanguage.JAPANESE to "決して離さない",
                LyricsLanguage.KOREAN to "너를 절대 놓지 않을게"
            ),
            "you and me" to mapOf(
                LyricsLanguage.TAMIL to "நீயும் நானும்",
                LyricsLanguage.HINDI to "तुम और मैं",
                LyricsLanguage.TELUGU to "నువ్వు మరియు నేను",
                LyricsLanguage.SPANISH to "Tú y yo",
                LyricsLanguage.FRENCH to "Toi et moi",
                LyricsLanguage.JAPANESE to "君と僕",
                LyricsLanguage.KOREAN to "너와 나"
            ),
            "in the night" to mapOf(
                LyricsLanguage.TAMIL to "இரவினில்",
                LyricsLanguage.HINDI to "रात में",
                LyricsLanguage.TELUGU to "రాత్రి వేళలో",
                LyricsLanguage.SPANISH to "En la noche",
                LyricsLanguage.FRENCH to "Dans la nuit",
                LyricsLanguage.JAPANESE to "夜の中に",
                LyricsLanguage.KOREAN to "밤에"
            )
        )

        val COMMON_WORDS_MAP: Map<String, Map<LyricsLanguage, String>> = mapOf(
            "love" to mapOf(
                LyricsLanguage.TAMIL to "காதல்",
                LyricsLanguage.HINDI to "प्यार",
                LyricsLanguage.TELUGU to "ప్రేమ",
                LyricsLanguage.SPANISH to "amor",
                LyricsLanguage.FRENCH to "amour",
                LyricsLanguage.JAPANESE to "愛",
                LyricsLanguage.KOREAN to "사랑"
            ),
            "heart" to mapOf(
                LyricsLanguage.TAMIL to "இதயம்",
                LyricsLanguage.HINDI to "दिल",
                LyricsLanguage.TELUGU to "గుండె",
                LyricsLanguage.SPANISH to "corazón",
                LyricsLanguage.FRENCH to "cœur",
                LyricsLanguage.JAPANESE to "心",
                LyricsLanguage.KOREAN to "마음"
            ),
            "night" to mapOf(
                LyricsLanguage.TAMIL to "இரவு",
                LyricsLanguage.HINDI to "रात",
                LyricsLanguage.TELUGU to "రాత్రి",
                LyricsLanguage.SPANISH to "noche",
                LyricsLanguage.FRENCH to "nuit",
                LyricsLanguage.JAPANESE to "夜",
                LyricsLanguage.KOREAN to "밤"
            ),
            "dream" to mapOf(
                LyricsLanguage.TAMIL to "கனவு",
                LyricsLanguage.HINDI to "सपना",
                LyricsLanguage.TELUGU to "కల",
                LyricsLanguage.SPANISH to "sueño",
                LyricsLanguage.FRENCH to "rêve",
                LyricsLanguage.JAPANESE to "夢",
                LyricsLanguage.KOREAN to "꿈"
            ),
            "forever" to mapOf(
                LyricsLanguage.TAMIL to "என்றென்றும்",
                LyricsLanguage.HINDI to "हमेशा",
                LyricsLanguage.TELUGU to "ఎప్పటికీ",
                LyricsLanguage.SPANISH to "para siempre",
                LyricsLanguage.FRENCH to "pour toujours",
                LyricsLanguage.JAPANESE to "永遠に",
                LyricsLanguage.KOREAN to "영원히"
            )
        )
    }
}
