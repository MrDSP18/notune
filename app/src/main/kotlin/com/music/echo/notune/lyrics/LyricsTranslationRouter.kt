package com.music.echo.notune.lyrics

import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Singleton
class LyricsTranslationRouter @Inject constructor(
    private val transliterator: LyricsTransliterator,
    private val translationEngine: LyricsTranslationEngine,
    private val singAlongEngine: SingAlongEngine
) {
    private data class CacheKey(
        val songId: String,
        val lines: List<LyricLine>,
        val sourceLanguage: LyricsLanguage,
        val targetLanguage: LyricsLanguage,
        val displayMode: LyricsDisplayMode,
        val artistName: String?
    )

    private val cache = ConcurrentHashMap<CacheKey, LyricsDocument>()

    fun protectTokens(text: String, protectedNames: List<String> = emptyList()): Pair<String, Map<String, String>> {
        var processedText = text
        val tokenMap = mutableMapOf<String, String>()
        var counter = 0
        (DEFAULT_PROTECTED_TOKENS + protectedNames).forEach { name ->
            val regex = Regex("(?i)\\b${Regex.escape(name)}\\b")
            if (regex.containsMatchIn(processedText)) {
                val placeholder = "__PROTECTED_TOKEN_${counter++}__"
                tokenMap[placeholder] = name
                processedText = processedText.replace(regex, placeholder)
            }
        }
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

    fun restoreTokens(text: String, tokenMap: Map<String, String>): String {
        var restored = text
        tokenMap.forEach { (placeholder, originalValue) -> restored = restored.replace(placeholder, originalValue) }
        return restored
    }

    suspend fun processDocument(
        songId: String,
        lines: List<LyricLine>,
        sourceLanguage: LyricsLanguage,
        targetLanguage: LyricsLanguage,
        displayMode: LyricsDisplayMode,
        artistName: String? = null
    ): LyricsDocument = withContext(Dispatchers.Default) {
        val cacheKey = CacheKey(songId, lines.toList(), sourceLanguage, targetLanguage, displayMode, artistName)
        cache[cacheKey]?.let { return@withContext it }
        val protectedNames = if (!artistName.isNullOrBlank()) listOf(artistName) else emptyList()

        val transliteratedLines = lines.map { line ->
            if (displayMode in listOf(LyricsDisplayMode.PRONUNCIATION, LyricsDisplayMode.DUAL_LYRICS, LyricsDisplayMode.TRIPLE_LYRICS)) {
                line.copy(transliteratedText = transliterator.transliterateToPronunciation(line.originalText, sourceLanguage))
            } else line
        }

        val translatedLines = if (displayMode in listOf(LyricsDisplayMode.MEANING, LyricsDisplayMode.TRIPLE_LYRICS)) {
            val protected = transliteratedLines.map { line ->
                val (text, _) = protectTokens(line.originalText, protectedNames)
                line.copy(originalText = text)
            }
            val results = translationEngine.generateTranslation(protected, targetLanguage)
            results.mapIndexed { index, result ->
                val original = transliteratedLines[index]
                val (_, tokens) = protectTokens(original.originalText, protectedNames)
                result.copy(
                    originalText = original.originalText,
                    translatedText = result.translatedText?.let { restoreTokens(it, tokens) }
                )
            }
        } else transliteratedLines

        val document = LyricsDocument(
            songId = songId,
            lines = translatedLines,
            displayMode = displayMode,
            sourceLanguage = sourceLanguage,
            targetLanguage = targetLanguage,
            confidenceScore = if (translatedLines.any { it.translatedText != null }) 0.85f else 0f
        )
        cache[cacheKey] = document
        document
    }

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
        if (displayMode in listOf(LyricsDisplayMode.MEANING, LyricsDisplayMode.TRIPLE_LYRICS)) {
            translationEngine.generateTranslation(lookaheadLines, targetLanguage)
        }
        if (displayMode in listOf(LyricsDisplayMode.PRONUNCIATION, LyricsDisplayMode.DUAL_LYRICS, LyricsDisplayMode.TRIPLE_LYRICS)) {
            lookaheadLines.forEach { line ->
                if (line.transliteratedText == null) transliterator.transliterateToPronunciation(line.originalText, sourceLanguage)
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
