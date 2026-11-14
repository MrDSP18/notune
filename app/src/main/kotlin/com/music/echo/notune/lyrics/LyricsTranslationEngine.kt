package com.music.echo.notune.lyrics

import echo.music.iad1tya.notune.ai.AiEngine
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonPrimitive
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
        val engine = aiEngine ?: return lines
        if (lines.isEmpty() || targetLanguage == LyricsLanguage.ENGLISH) {
            return lines
        }

        val translated = ArrayList<LyricLine>(lines.size)
        for (batch in lines.chunked(MAX_LINES_PER_REQUEST)) {
            val prompt = buildString {
                appendLine("Translate each supplied lyric line into ${targetLanguage.displayName} (${targetLanguage.nativeName}).")
                appendLine("Preserve meaning, tone, idioms and cultural context.")
                appendLine("Return ONLY a valid JSON array of strings.")
                appendLine("The array must contain exactly ${batch.size} strings in the same order.")
                appendLine("Do not add, remove, explain, summarize, or invent any lyric content.")
                appendLine("Keep names and vocalizations when translating them would change their meaning.")
                appendLine()
                appendLine("SOURCE LINES:")
                batch.forEachIndexed { index, line ->
                    appendLine("${index + 1}. ${line.originalText}")
                }
            }

            val result = runCatching {
                engine.runNeuralMatrixLoop(
                    prompt = prompt,
                    systemInstruction = "You are a precise line-preserving music lyric translator. Translate only the user-provided source lines. Output a JSON string array and nothing else."
                ).getOrThrow()
            }

            val values = result.getOrNull()?.text?.let {
                parseTranslationResponse(it, batch.size)
            }

            if (values == null) {
                result.exceptionOrNull()?.let { Timber.w(it, "LYRICS_AI: Translation request failed.") }
                Timber.w("LYRICS_AI: Invalid translation payload; preserving original lines.")
                translated.addAll(batch)
            } else {
                batch.forEachIndexed { index, line ->
                    translated += line.copy(
                        translatedText = values[index],
                        transformationVersion = 2
                    )
                }
            }
        }
        return translated
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
        private const val MAX_LINES_PER_REQUEST = 20
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
            ),
            "die with a smile" to mapOf(
                LyricsLanguage.TAMIL to "புன்னகையோடு உன்னுடன்",
                LyricsLanguage.HINDI to "एक मुस्कान के साथ",
                LyricsLanguage.TELUGU to "చిరునవ్వుతో నీతో",
                LyricsLanguage.SPANISH to "Morir con una sonrisa",
                LyricsLanguage.FRENCH to "Mourir avec un sourire",
                LyricsLanguage.JAPANESE to "笑顔で",
                LyricsLanguage.KOREAN to "미소 지으며"
            ),
            "all of me" to mapOf(
                LyricsLanguage.TAMIL to "என் முழுமையும் உனக்கு",
                LyricsLanguage.HINDI to "मेरा सब कुछ तुम्हारा है",
                LyricsLanguage.TELUGU to "నా సర్వస్వం నీదే",
                LyricsLanguage.SPANISH to "Todo de mí te ama",
                LyricsLanguage.FRENCH to "Tout de moi t'aime",
                LyricsLanguage.JAPANESE to "私のすべて",
                LyricsLanguage.KOREAN to "나의 모든 것"
            ),
            "shape of you" to mapOf(
                LyricsLanguage.TAMIL to "உன் அழகிய வடிவம்",
                LyricsLanguage.HINDI to "तुम्हारी प्यारी सूरत",
                LyricsLanguage.TELUGU to "నీ అందమైన రూపం",
                LyricsLanguage.SPANISH to "Tu hermosa figura",
                LyricsLanguage.FRENCH to "Ta belle silhouette",
                LyricsLanguage.JAPANESE to "あなたの姿に恋して",
                LyricsLanguage.KOREAN to "너의 아름다운 모습"
            ),
            "tum hi ho" to mapOf(
                LyricsLanguage.TAMIL to "நீ மட்டுமே என் உலகம்",
                LyricsLanguage.HINDI to "तुम ही हो अब तुम ही हो",
                LyricsLanguage.TELUGU to "నువ్వే నా సర్వస్వం",
                LyricsLanguage.SPANISH to "Solo tú eres mi vida",
                LyricsLanguage.FRENCH to "Tu es mon seul amour",
                LyricsLanguage.JAPANESE to "あなただけがすべて",
                LyricsLanguage.KOREAN to "오직 너만이 나의 전부"
            ),
            "munbe vaa" to mapOf(
                LyricsLanguage.TAMIL to "முன்பே வா என் அன்பே வா",
                LyricsLanguage.HINDI to "मेरे पास आओ मेरी जान",
                LyricsLanguage.TELUGU to "ముందు రా నా ప్రియతమా",
                LyricsLanguage.SPANISH to "Ven a mí mi amor",
                LyricsLanguage.FRENCH to "Viens à moi mon amour",
                LyricsLanguage.JAPANESE to "私の元へ来て愛しい人",
                LyricsLanguage.KOREAN to "내 곁으로 와요 내 사랑"
            ),
            "katchi sera" to mapOf(
                LyricsLanguage.TAMIL to "கட்சி சேர வந்தவளே",
                LyricsLanguage.HINDI to "मेरे पास आने वाली हसीं",
                LyricsLanguage.TELUGU to "దగ్గరకు వచ్చిన ప్రేయసి",
                LyricsLanguage.SPANISH to "La que vino a estar conmigo",
                LyricsLanguage.FRENCH to "Celle qui est venue à moi",
                LyricsLanguage.JAPANESE to "私の元へ来た人",
                LyricsLanguage.KOREAN to "내게 다가온 그대"
            ),
            "kesariya" to mapOf(
                LyricsLanguage.TAMIL to "குங்குமப்பூ நிற காதல்",
                LyricsLanguage.HINDI to "केसरिया तेरा इश्क़ है पिया",
                LyricsLanguage.TELUGU to "కుంకుమపువ్వు వంటి ప్రేమ",
                LyricsLanguage.SPANISH to "Tu amor color azafrán",
                LyricsLanguage.FRENCH to "Ton amour safran",
                LyricsLanguage.JAPANESE to "サフラン色のあなたの愛",
                LyricsLanguage.KOREAN to "너의 사프란 빛 사랑"
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
            ),
            "baby" to mapOf(
                LyricsLanguage.TAMIL to "அன்பே",
                LyricsLanguage.HINDI to "जानेमन",
                LyricsLanguage.TELUGU to "ప్రియా",
                LyricsLanguage.SPANISH to "bebé",
                LyricsLanguage.FRENCH to "bébé",
                LyricsLanguage.JAPANESE to "ベイビー",
                LyricsLanguage.KOREAN to "그대"
            ),
            "smile" to mapOf(
                LyricsLanguage.TAMIL to "புன்னகை",
                LyricsLanguage.HINDI to "मुस्कान",
                LyricsLanguage.TELUGU to "చిరునవ్వు",
                LyricsLanguage.SPANISH to "sonrisa",
                LyricsLanguage.FRENCH to "sourire",
                LyricsLanguage.JAPANESE to "笑顔",
                LyricsLanguage.KOREAN to "미소"
            ),
            "eyes" to mapOf(
                LyricsLanguage.TAMIL to "விழிகள்",
                LyricsLanguage.HINDI to "आँखें",
                LyricsLanguage.TELUGU to "కళ్లు",
                LyricsLanguage.SPANISH to "ojos",
                LyricsLanguage.FRENCH to "yeux",
                LyricsLanguage.JAPANESE to "瞳",
                LyricsLanguage.KOREAN to "눈빛"
            ),
            "music" to mapOf(
                LyricsLanguage.TAMIL to "இசை",
                LyricsLanguage.HINDI to "संगीत",
                LyricsLanguage.TELUGU to "సంగీతం",
                LyricsLanguage.SPANISH to "música",
                LyricsLanguage.FRENCH to "musique",
                LyricsLanguage.JAPANESE to "音楽",
                LyricsLanguage.KOREAN to "음악"
            ),
            "soul" to mapOf(
                LyricsLanguage.TAMIL to "ஆன்மா",
                LyricsLanguage.HINDI to "रूह",
                LyricsLanguage.TELUGU to "ఆత్మ",
                LyricsLanguage.SPANISH to "alma",
                LyricsLanguage.FRENCH to "âme",
                LyricsLanguage.JAPANESE to "魂",
                LyricsLanguage.KOREAN to "영혼"
            ),
            "world" to mapOf(
                LyricsLanguage.TAMIL to "உலகம்",
                LyricsLanguage.HINDI to "दुनिया",
                LyricsLanguage.TELUGU to "ప్రపంచం",
                LyricsLanguage.SPANISH to "mundo",
                LyricsLanguage.FRENCH to "monde",
                LyricsLanguage.JAPANESE to "世界",
                LyricsLanguage.KOREAN to "세상"
            ),
            "light" to mapOf(
                LyricsLanguage.TAMIL to "வெளிச்சம்",
                LyricsLanguage.HINDI to "रोशनी",
                LyricsLanguage.TELUGU to "వెలుగు",
                LyricsLanguage.SPANISH to "luz",
                LyricsLanguage.FRENCH to "lumière",
                LyricsLanguage.JAPANESE to "光",
                LyricsLanguage.KOREAN to "빛"
            )
        )
    }
}

internal fun parseTranslationResponse(response: String, expectedLineCount: Int): List<String>? {
    if (expectedLineCount <= 0 || response.isBlank()) return null
    val cleaned = response.trim()
        .removePrefix("```json")
        .removePrefix("```")
        .removeSuffix("```")
        .trim()
    val start = cleaned.indexOf('[')
    val end = cleaned.lastIndexOf(']')
    if (start < 0 || end <= start) return null
    val parsed = runCatching {
        Json.parseToJsonElement(cleaned.substring(start, end + 1)) as? JsonArray
    }.getOrNull() ?: return null
    if (parsed.size != expectedLineCount) return null
    val values = ArrayList<String>(parsed.size)
    for (element in parsed) {
        val value = runCatching { element.jsonPrimitive }.getOrNull() ?: return null
        if (!value.isString || value.content.isBlank()) return null
        values += value.content
    }
    return values
}
