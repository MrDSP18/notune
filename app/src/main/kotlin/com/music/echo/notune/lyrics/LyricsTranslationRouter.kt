package com.music.echo.notune.lyrics

import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Advanced Lyrics Translation Router for NØTUNE Lyrics Intelligence 2.0.
 * Handles protected tokens, lookahead pre-caching, pronunciation transliteration, and meaning translation.
 */
@Singleton
class LyricsTranslationRouter @Inject constructor(
    private val transliterator: LyricsTransliterator,
    private val translationEngine: LyricsTranslationEngine,
    private val singAlongEngine: SingAlongEngine
) {
    // In-memory cache key: "songId_displayMode_targetLanguageCode"
    private val cache = ConcurrentHashMap<String, LyricsDocument>()

    /**
     * Protect proper nouns, artist names, and sound expressions from unintended translation.
     */
    fun protectTokens(text: String, protectedNames: List<String> = emptyList()): Pair<String, Map<String, String>> {
        var processedText = text
        val tokenMap = mutableMapOf<String, String>()
        var counter = 0

        // Protect known artist names and proper nouns
        val allProtected = DEFAULT_PROTECTED_TOKENS + protectedNames
        allProtected.forEach { name ->
            val regex = Regex("(?i)\\b${Regex.escape(name)}\\b")
            if (regex.containsMatchIn(processedText)) {
                val placeholder = "__PROTECTED_TOKEN_${counter++}__"
                tokenMap[placeholder] = name
                processedText = processedText.replace(regex, placeholder)
            }
        }

        // Protect vocalizations / sound expressions (oh, ah, la, na, yeah)
        DEFAULT_VOCALIZATIONS.forEach { voc ->
            val regex = Regex("(?i)\\b${Regex.escape(voc)}\\b")
            if (regex.containsMatchIn(processedText)) {
                val placeholder = "__PROTECTED_TOKEN_${counter++}__"
                tokenMap[placeholder] = voc
                processedText = processedText.replace(regex, placeholder)
            }
        }

        return Pair(processedText, tokenMap)
    }

    /**
     * Restore protected tokens after translation.
     */
    fun restoreTokens(text: String, tokenMap: Map<String, String>): String {
        var restored = text
        tokenMap.forEach { (placeholder, originalValue) ->
            restored = restored.replace(placeholder, originalValue)
        }
        return restored
    }

    /**
     * Process and transform full lyrics document according to target language and display mode.
     */
    suspend fun processDocument(
        songId: String,
        lines: List<LyricLine>,
        sourceLanguage: LyricsLanguage,
        targetLanguage: LyricsLanguage,
        displayMode: LyricsDisplayMode,
        artistName: String? = null
    ): LyricsDocument = withContext(Dispatchers.Default) {
        val cacheKey = "${songId}_${displayMode.name}_${targetLanguage.code}"
        cache[cacheKey]?.let { return@withContext it }

        val protectedList = if (!artistName.isNullOrBlank()) listOf(artistName) else emptyList()

        // 1. Process transliteration (Pronunciation) if needed
        val transliteratedLines = lines.map { line ->
            if (displayMode in listOf(LyricsDisplayMode.PRONUNCIATION, LyricsDisplayMode.DUAL_LYRICS, LyricsDisplayMode.TRIPLE_LYRICS)) {
                val pron = transliterator.transliterateToPronunciation(line.originalText, sourceLanguage)
                line.copy(transliteratedText = pron)
            } else {
                line
            }
        }

        // 2. Process meaning translation if needed
        val translatedLines = if (displayMode in listOf(LyricsDisplayMode.MEANING, LyricsDisplayMode.TRIPLE_LYRICS)) {
            transliteratedLines.map { line ->
                val (protectedText, tokenMap) = protectTokens(line.originalText, protectedList)
                val rawTranslation = translationEngine.ruleBasedTranslate(protectedText, targetLanguage)
                val restored = restoreTokens(rawTranslation, tokenMap)
                line.copy(translatedText = restored)
            }
        } else {
            transliteratedLines
        }

        val document = LyricsDocument(
            songId = songId,
            lines = translatedLines,
            displayMode = displayMode,
            sourceLanguage = sourceLanguage,
            targetLanguage = targetLanguage,
            confidenceScore = 0.95f
        )

        cache[cacheKey] = document
        document
    }

    /**
     * Pre-translate lookahead lines (current line + next 3 lines) asynchronously.
     */
    suspend fun triggerLookaheadCache(
        songId: String,
        lines: List<LyricLine>,
        currentIndex: Int,
        sourceLanguage: LyricsLanguage,
        targetLanguage: LyricsLanguage,
        displayMode: LyricsDisplayMode,
        lookaheadCount: Int = 3
    ) = withContext(Dispatchers.Default) {
        if (currentIndex < 0 || lines.isEmpty()) return@withContext

        val endIndex = (currentIndex + lookaheadCount).coerceAtMost(lines.size - 1)
        val lookaheadLines = lines.subList(currentIndex.coerceAtMost(lines.size - 1), endIndex + 1)

        lookaheadLines.forEach { line ->
            if (displayMode in listOf(LyricsDisplayMode.PRONUNCIATION, LyricsDisplayMode.DUAL_LYRICS, LyricsDisplayMode.TRIPLE_LYRICS)) {
                if (line.transliteratedText == null) {
                    transliterator.transliterateToPronunciation(line.originalText, sourceLanguage)
                }
            }
            if (displayMode in listOf(LyricsDisplayMode.MEANING, LyricsDisplayMode.TRIPLE_LYRICS)) {
                if (line.translatedText == null) {
                    val (protectedText, tokenMap) = protectTokens(line.originalText)
                    val rawTranslation = translationEngine.ruleBasedTranslate(protectedText, targetLanguage)
                    restoreTokens(rawTranslation, tokenMap)
                }
            }
        }
    }

    fun clearCache() {
        cache.clear()
    }

    companion object {
        val DEFAULT_PROTECTED_TOKENS = listOf(
            "Taylor Swift", "Anirudh", "A.R. Rahman", "AR Rahman", "Arijit Singh",
            "Sid Sriram", "Shreya Ghoshal", "Justin Bieber", "Drake", "BTS",
            "NØTUNE", "Echo", "Spotify", "YouTube"
        )

        val DEFAULT_VOCALIZATIONS = listOf(
            "oh", "ooh", "ah", "aah", "la", "la-la", "na", "na-na", "yeah", "yea", "hey", "woo"
        )
    }
}
