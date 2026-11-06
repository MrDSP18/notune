package com.music.echo.notune.lyrics

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

/**
 * 5 Display Modes for NØTUNE Lyrics Intelligence 2.0
 */
@Immutable
@Serializable
enum class LyricsDisplayMode(val label: String, val description: String) {
    ORIGINAL("Original", "Original lyrics in native script"),
    PRONUNCIATION("Pronunciation", "Phonetic transliteration in Latin script (e.g. July kaatrin nerathil)"),
    MEANING("Meaning", "Translated meaning in target language (e.g. During the July breeze)"),
    DUAL_LYRICS("Dual Lyrics", "Original script + Phonetic pronunciation stack"),
    TRIPLE_LYRICS("Triple Stack", "Original script + Phonetic pronunciation + Translated meaning stack")
}

@Immutable
@Serializable
enum class LyricsMode(val label: String, val description: String) {
    ORIGINAL("Original", "Original lyrics as published"),
    TRANSLATION("Translation", "Meaning-preserving translation"),
    SING_ALONG("Sing Along", "Phonetic transliteration in target script"),
    ROMANIZED("Romanized", "Romanized pronunciation");

    fun toDisplayMode(): LyricsDisplayMode = when (this) {
        ORIGINAL -> LyricsDisplayMode.ORIGINAL
        TRANSLATION -> LyricsDisplayMode.MEANING
        SING_ALONG -> LyricsDisplayMode.PRONUNCIATION
        ROMANIZED -> LyricsDisplayMode.PRONUNCIATION
    }
}

@Immutable
@Serializable
data class LyricsWord(
    val text: String,
    val startTime: Long,
    val endTime: Long,
    val pronunciation: String? = null,
    val translatedWord: String? = null
)

@Immutable
@Serializable
data class TransliteratedLine(
    val originalText: String,
    val pronunciationText: String,
    val scriptName: String = "Latin"
)

@Immutable
@Serializable
data class TranslatedLine(
    val originalText: String,
    val meaningText: String,
    val targetLanguageCode: String = "en"
)

@Immutable
@Serializable
data class LyricLine(
    val id: String,
    val startTime: Long,
    val endTime: Long,
    val originalText: String,
    val translatedText: String? = null,
    val transliteratedText: String? = null,
    val language: String = "en",
    val source: String = "AUTHORIZED_PROVIDER",
    val transformationVersion: Int = 1,
    val words: List<LyricsWord> = emptyList()
) {
    fun activeText(mode: LyricsMode): String {
        return when (mode) {
            LyricsMode.ORIGINAL -> originalText
            LyricsMode.TRANSLATION -> translatedText ?: originalText
            LyricsMode.SING_ALONG -> transliteratedText ?: originalText
            LyricsMode.ROMANIZED -> transliteratedText ?: originalText
        }
    }

    fun textForDisplayMode(displayMode: LyricsDisplayMode): String {
        return when (displayMode) {
            LyricsDisplayMode.ORIGINAL -> originalText
            LyricsDisplayMode.PRONUNCIATION -> transliteratedText ?: originalText
            LyricsDisplayMode.MEANING -> translatedText ?: originalText
            LyricsDisplayMode.DUAL_LYRICS -> "$originalText\n${transliteratedText ?: originalText}"
            LyricsDisplayMode.TRIPLE_LYRICS -> "$originalText\n${transliteratedText ?: originalText}\n${translatedText ?: originalText}"
        }
    }
}

@Immutable
@Serializable
data class LyricsDocument(
    val songId: String,
    val lines: List<LyricLine>,
    val displayMode: LyricsDisplayMode = LyricsDisplayMode.ORIGINAL,
    val sourceLanguage: LyricsLanguage = LyricsLanguage.ENGLISH,
    val targetLanguage: LyricsLanguage = LyricsLanguage.ENGLISH,
    val isWordSynced: Boolean = false,
    val confidenceScore: Float = 1.0f
)

@Serializable
data class TransformLyricsRequest(
    val songId: String,
    val lines: List<LyricLine>,
    val sourceLanguage: LyricsLanguage = LyricsLanguage.ENGLISH,
    val targetLanguage: LyricsLanguage = LyricsLanguage.TAMIL,
    val mode: LyricsMode = LyricsMode.SING_ALONG,
    val displayMode: LyricsDisplayMode = LyricsDisplayMode.DUAL_LYRICS
)

@Serializable
data class TransformLyricsResult(
    val songId: String,
    val transformedLines: List<LyricLine>,
    val mode: LyricsMode = LyricsMode.SING_ALONG,
    val displayMode: LyricsDisplayMode = LyricsDisplayMode.DUAL_LYRICS,
    val targetLanguage: LyricsLanguage,
    val isCached: Boolean = false,
    val isOfflineFallback: Boolean = false,
    val errorMessage: String? = null
)

