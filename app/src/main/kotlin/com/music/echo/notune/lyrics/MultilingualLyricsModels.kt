package com.music.echo.notune.lyrics

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
enum class LyricsMode(val label: String, val description: String) {
    ORIGINAL("Original", "Original lyrics as published"),
    TRANSLATION("Translation", "Meaning-preserving translation"),
    SING_ALONG("Sing Along", "Phonetic transliteration in target script"),
    ROMANIZED("Romanized", "Romanized pronunciation")
}

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
    val transformationVersion: Int = 1
) {
    fun activeText(mode: LyricsMode): String {
        return when (mode) {
            LyricsMode.ORIGINAL -> originalText
            LyricsMode.TRANSLATION -> translatedText ?: originalText
            LyricsMode.SING_ALONG -> transliteratedText ?: originalText
            LyricsMode.ROMANIZED -> transliteratedText ?: originalText
        }
    }
}

@Serializable
data class TransformLyricsRequest(
    val songId: String,
    val lines: List<LyricLine>,
    val sourceLanguage: LyricsLanguage = LyricsLanguage.ENGLISH,
    val targetLanguage: LyricsLanguage = LyricsLanguage.TAMIL,
    val mode: LyricsMode = LyricsMode.SING_ALONG
)

@Serializable
data class TransformLyricsResult(
    val songId: String,
    val transformedLines: List<LyricLine>,
    val mode: LyricsMode,
    val targetLanguage: LyricsLanguage,
    val isCached: Boolean = false,
    val isOfflineFallback: Boolean = false,
    val errorMessage: String? = null
)
