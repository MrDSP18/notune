package com.music.echo.notune.lyrics

import javax.inject.Inject
import javax.inject.Singleton

enum class LyricsSourceType {
    SYNCED_PROVIDER,
    PLAIN_PROVIDER,
    CACHED_AUTHORIZED,
    USER_PROVIDED,
    UNAVAILABLE
}

data class ResolvedLyrics(
    val songId: String,
    val isAvailable: Boolean = true,
    val sourceType: LyricsSourceType = LyricsSourceType.SYNCED_PROVIDER,
    val lines: List<LyricLine> = emptyList(),
    val language: LyricsLanguage = LyricsLanguage.ENGLISH,
    val statusMessage: String = ""
)

@Singleton
class LyricsResolver @Inject constructor() {

    fun resolveLyrics(
        songId: String,
        providerLines: List<LyricLine>?,
        cachedLines: List<LyricLine>?,
        userLines: List<LyricLine>?
    ): ResolvedLyrics {
        // Priority 1: Synced provider lyrics
        if (!providerLines.isNullOrEmpty() && providerLines.any { it.startTime > 0 || it.endTime > 0 }) {
            return ResolvedLyrics(
                songId = songId,
                isAvailable = true,
                sourceType = LyricsSourceType.SYNCED_PROVIDER,
                lines = providerLines,
                statusMessage = "Synced Provider Lyrics"
            )
        }

        // Priority 2: Plain provider lyrics
        if (!providerLines.isNullOrEmpty()) {
            return ResolvedLyrics(
                songId = songId,
                isAvailable = true,
                sourceType = LyricsSourceType.PLAIN_PROVIDER,
                lines = providerLines,
                statusMessage = "Plain Provider Lyrics"
            )
        }

        // Priority 3: Cached authorized lyrics
        if (!cachedLines.isNullOrEmpty()) {
            return ResolvedLyrics(
                songId = songId,
                isAvailable = true,
                sourceType = LyricsSourceType.CACHED_AUTHORIZED,
                lines = cachedLines,
                statusMessage = "Cached Authorized Lyrics"
            )
        }

        // Priority 4: User-provided lyrics
        if (!userLines.isNullOrEmpty()) {
            return ResolvedLyrics(
                songId = songId,
                isAvailable = true,
                sourceType = LyricsSourceType.USER_PROVIDED,
                lines = userLines,
                statusMessage = "Community Lyrics"
            )
        }

        // Priority 5: Fallback - ZERO HALLUCINATION GUARANTEE
        return ResolvedLyrics(
            songId = songId,
            isAvailable = false,
            sourceType = LyricsSourceType.UNAVAILABLE,
            lines = emptyList(),
            statusMessage = "Lyrics unavailable"
        )
    }
}
