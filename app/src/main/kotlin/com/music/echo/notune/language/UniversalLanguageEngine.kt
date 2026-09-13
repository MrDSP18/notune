package com.music.echo.notune.language

import androidx.compose.runtime.Immutable
import com.music.echo.notune.lyrics.LyricsLanguage
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Immutable
data class LanguageDomainsConfig(
    val appUiLanguage: LyricsLanguage = LyricsLanguage.ENGLISH,
    val musicLanguage: LyricsLanguage = LyricsLanguage.ENGLISH,
    val aiLanguage: LyricsLanguage = LyricsLanguage.ENGLISH,
    val lyricsLanguage: LyricsLanguage = LyricsLanguage.ENGLISH,
    val translationLanguage: LyricsLanguage = LyricsLanguage.ENGLISH,
    val transliterationLanguage: LyricsLanguage = LyricsLanguage.ENGLISH,
    val voiceCommandLanguage: LyricsLanguage = LyricsLanguage.ENGLISH,
    val notificationLanguage: LyricsLanguage = LyricsLanguage.ENGLISH
)

@Immutable
data class MultilingualIntent(
    val originalQuery: String,
    val detectedLanguages: List<LyricsLanguage>,
    val targetAction: String,
    val extractedMood: String? = null,
    val extractedGenre: String? = null,
    val extractedArtist: String? = null,
    val extractedEra: String? = null,
    val normalizedIntent: String
)

@Singleton
class UniversalLanguageEngine @Inject constructor() {

    private val _config = MutableStateFlow(LanguageDomainsConfig())
    val config: StateFlow<LanguageDomainsConfig> = _config.asStateFlow()

    fun updateDomainLanguage(domain: String, language: LyricsLanguage) {
        _config.update { current ->
            when (domain.lowercase()) {
                "ui", "appui", "app_ui" -> current.copy(appUiLanguage = language)
                "music" -> current.copy(musicLanguage = language)
                "ai" -> current.copy(aiLanguage = language)
                "lyrics" -> current.copy(lyricsLanguage = language)
                "translation" -> current.copy(translationLanguage = language)
                "transliteration" -> current.copy(transliterationLanguage = language)
                "voice", "voice_command" -> current.copy(voiceCommandLanguage = language)
                "notification", "notifications" -> current.copy(notificationLanguage = language)
                else -> current
            }
        }
    }

    fun normalizeMultilingualQuery(query: String): MultilingualIntent {
        val lower = query.trim().lowercase()
        val detectedLanguages = mutableListOf<LyricsLanguage>()

        if (query.any { it in '\u0B80'..'\u0BFF' } || lower.contains("pannu") || lower.contains("venum") || lower.contains("paattu")) {
            detectedLanguages.add(LyricsLanguage.TAMIL)
        }
        if (query.any { it in '\u0C80'..'\u0CFF' } || lower.contains("maadu") || lower.contains("heli") || lower.contains("haaku")) {
            detectedLanguages.add(LyricsLanguage.KANNADA)
        }
        if (query.any { it in '\u0C00'..'\u0C7F' } || lower.contains("cheyyi") || lower.contains("kavali")) {
            detectedLanguages.add(LyricsLanguage.TELUGU)
        }
        if (query.any { it in '\u0D00'..'\u0D7F' } || lower.contains("venue") || lower.contains("paattu")) {
            detectedLanguages.add(LyricsLanguage.MALAYALAM)
        }
        if (query.any { it in '\u0900'..'\u097F' } || lower.contains("chalao") || lower.contains("gaana") || lower.contains("suno")) {
            detectedLanguages.add(LyricsLanguage.HINDI)
        }
        if (detectedLanguages.isEmpty()) {
            detectedLanguages.add(LyricsLanguage.ENGLISH)
        }

        val action = when {
            lower.contains("play") || lower.contains("போடு") || lower.contains("ಹಾಕು") || lower.contains("चलाओ") -> "PLAY"
            lower.contains("meaning") || lower.contains("அர்த்தம்") || lower.contains("ಅರ್ಥ") || lower.contains("मतलब") -> "EXPLAIN_LYRICS"
            else -> "RECOMMEND"
        }

        val extractedMood = when {
            lower.contains("calm") || lower.contains("peaceful") || lower.contains("அமைதியான") || lower.contains("శాంతమైన") || lower.contains("शांत") -> "Calm"
            lower.contains("energetic") || lower.contains("party") || lower.contains("उत्साही") -> "Energetic"
            lower.contains("sad") || lower.contains("melancholy") || lower.contains("சோக") -> "Sad"
            else -> null
        }

        val extractedEra = when {
            lower.contains("90s") || lower.contains("90") -> "1990s"
            lower.contains("80s") || lower.contains("80") -> "1980s"
            lower.contains("2000s") -> "2000s"
            else -> null
        }

        return MultilingualIntent(
            originalQuery = query,
            detectedLanguages = detectedLanguages,
            targetAction = action,
            extractedMood = extractedMood,
            extractedEra = extractedEra,
            normalizedIntent = "Action: $action | Mood: ${extractedMood ?: "Any"} | Era: ${extractedEra ?: "Any"} | Languages: ${detectedLanguages.joinToString { it.displayName }}"
        )
    }
}
