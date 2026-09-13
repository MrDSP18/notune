package com.music.echo.notune.lyrics

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import echo.music.iad1tya.utils.dataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import java.util.concurrent.ConcurrentHashMap

private val PREF_LYRICS_LANGUAGE = stringPreferencesKey("pref_lyrics_language")
private val PREF_LYRICS_MODE = stringPreferencesKey("pref_lyrics_mode")

@Singleton
class MultilingualLyricsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val singAlongEngine: SingAlongEngine,
    private val translationEngine: LyricsTranslationEngine
) {
    // In-memory cache for transformed lyric lines key: "songId_targetLanguage_mode"
    private val transformationCache = ConcurrentHashMap<String, List<LyricLine>>()

    val lyricsLanguageFlow: Flow<LyricsLanguage> = context.dataStore.data.map { prefs ->
        val code = prefs[PREF_LYRICS_LANGUAGE] ?: LyricsLanguage.TAMIL.code
        LyricsLanguage.fromCode(code)
    }

    val lyricsModeFlow: Flow<LyricsMode> = context.dataStore.data.map { prefs ->
        val modeName = prefs[PREF_LYRICS_MODE] ?: LyricsMode.SING_ALONG.name
        runCatching { LyricsMode.valueOf(modeName) }.getOrDefault(LyricsMode.SING_ALONG)
    }

    suspend fun setLyricsLanguage(language: LyricsLanguage) {
        context.dataStore.edit { prefs ->
            prefs[PREF_LYRICS_LANGUAGE] = language.code
        }
    }

    suspend fun setLyricsMode(mode: LyricsMode) {
        context.dataStore.edit { prefs ->
            prefs[PREF_LYRICS_MODE] = mode.name
        }
    }

    /**
     * Retrieves or builds transformed lyrics for a song in the specified mode & language.
     * Preserves timestamp alignment for all lines.
     */
    suspend fun getTransformedLyrics(
        songId: String,
        lines: List<LyricLine>,
        targetLanguage: LyricsLanguage,
        mode: LyricsMode
    ): TransformLyricsResult {
        if (mode == LyricsMode.ORIGINAL || targetLanguage == LyricsLanguage.ENGLISH) {
            return TransformLyricsResult(
                songId = songId,
                transformedLines = lines,
                mode = LyricsMode.ORIGINAL,
                targetLanguage = targetLanguage,
                isCached = true
            )
        }

        val cacheKey = "${songId}_${targetLanguage.code}_${mode.name}"
        transformationCache[cacheKey]?.let { cachedLines ->
            return TransformLyricsResult(
                songId = songId,
                transformedLines = cachedLines,
                mode = mode,
                targetLanguage = targetLanguage,
                isCached = true
            )
        }

        val transformed = when (mode) {
            LyricsMode.SING_ALONG, LyricsMode.ROMANIZED -> {
                singAlongEngine.generateSingAlong(lines, targetLanguage)
            }
            LyricsMode.TRANSLATION -> {
                translationEngine.generateTranslation(lines, targetLanguage)
            }
            LyricsMode.ORIGINAL -> lines
        }

        transformationCache[cacheKey] = transformed

        return TransformLyricsResult(
            songId = songId,
            transformedLines = transformed,
            mode = mode,
            targetLanguage = targetLanguage,
            isCached = false
        )
    }
}
