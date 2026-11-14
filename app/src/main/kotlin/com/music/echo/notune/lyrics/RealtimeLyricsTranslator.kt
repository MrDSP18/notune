package com.music.echo.notune.lyrics

import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

/**
 * Real-Time Multilingual Lyrics Translator and Transliteration Engine for NØTUNE.
 * Translates line-by-line in real time, generates sing-along pronunciation,
 * and caches results for sub-millisecond playback syncing.
 */
@Singleton
class RealtimeLyricsTranslator @Inject constructor(
    private val translationEngine: LyricsTranslationEngine,
    private val transliterator: LyricsTransliterator
) {
    private val translationCache = ConcurrentHashMap<String, String>()
    private val pronunciationCache = ConcurrentHashMap<String, String>()

    /**
     * Translates a single line of lyrics into the target language in real time.
     */
    fun translateLine(line: String, targetLanguage: LyricsLanguage): String {
        if (line.isBlank() || targetLanguage == LyricsLanguage.ENGLISH) {
            return line
        }

        val cacheKey = "${targetLanguage.code}_${line.trim().lowercase()}"
        translationCache[cacheKey]?.let { return it }

        val translated = translationEngine.ruleBasedTranslate(line, targetLanguage)
        translationCache[cacheKey] = translated
        return translated
    }

    /**
     * Transliterates lyrics line to Romanized sing-along pronunciation.
     */
    fun transliterateLine(line: String, sourceLanguage: LyricsLanguage = LyricsLanguage.TAMIL): String {
        if (line.isBlank()) return line

        val cacheKey = "${sourceLanguage.code}_pron_${line.trim()}"
        pronunciationCache[cacheKey]?.let { return it }

        val pronunciation = transliterator.transliterateToPronunciation(line, sourceLanguage)
        pronunciationCache[cacheKey] = pronunciation
        return pronunciation
    }

    /**
     * Resolves dual-line representation for active player lyrics rendering.
     * Returns Pair(primaryLine, secondarySubtitleLine).
     */
    fun getDualLine(
        originalLine: String,
        targetLanguage: LyricsLanguage,
        mode: LyricsDisplayMode
    ): Pair<String, String?> {
        if (originalLine.isBlank()) return Pair(originalLine, null)

        return when (mode) {
            LyricsDisplayMode.ORIGINAL -> {
                Pair(originalLine, null)
            }
            LyricsDisplayMode.MEANING -> {
                val translated = translateLine(originalLine, targetLanguage)
                Pair(translated, null)
            }
            LyricsDisplayMode.PRONUNCIATION -> {
                val pron = transliterateLine(originalLine, LyricsLanguage.TAMIL)
                Pair(pron, null)
            }
            LyricsDisplayMode.DUAL_LYRICS -> {
                // Primary is original; secondary is translated meaning if target != English, else pronunciation
                val secondary = if (targetLanguage != LyricsLanguage.ENGLISH) {
                    translateLine(originalLine, targetLanguage)
                } else {
                    transliterateLine(originalLine, LyricsLanguage.TAMIL)
                }
                Pair(originalLine, secondary.takeIf { it != originalLine })
            }
            LyricsDisplayMode.TRIPLE_LYRICS -> {
                val translated = translateLine(originalLine, targetLanguage)
                val secondary = translated.takeIf { it != originalLine }
                    ?: transliterateLine(originalLine, LyricsLanguage.TAMIL)
                Pair(originalLine, secondary)
            }
        }
    }

    /**
     * Returns list of popular languages displayed on the real-time translation chip bar.
     */
    fun getSupportedLanguages(): List<LyricsLanguage> {
        return listOf(
            LyricsLanguage.ENGLISH,
            LyricsLanguage.TAMIL,
            LyricsLanguage.HINDI,
            LyricsLanguage.TELUGU,
            LyricsLanguage.SPANISH,
            LyricsLanguage.JAPANESE,
            LyricsLanguage.KOREAN,
            LyricsLanguage.FRENCH,
            LyricsLanguage.GERMAN,
            LyricsLanguage.MALAYALAM,
            LyricsLanguage.KANNADA
        )
    }

    fun clearCache() {
        translationCache.clear()
        pronunciationCache.clear()
    }
}
