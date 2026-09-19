package echo.music.iad1tya.notune.flow

import echo.music.iad1tya.db.entities.AlbumEntity
import echo.music.iad1tya.db.entities.ArtistEntity
import echo.music.iad1tya.db.entities.SongEntity
import echo.music.iad1tya.models.MediaMetadata
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguageSessionTracker @Inject constructor() {

    private val knownLanguageKeywords = listOf(
        "Tamil", "Hindi", "Telugu", "Punjabi", "Malayalam", "Kannada", "Bengali", "Marathi",
        "English", "Spanish", "Korean", "K-Pop", "Japanese", "J-Pop", "French", "German"
    )

    fun detectLanguage(
        mediaMetadata: MediaMetadata,
        songEntity: SongEntity? = null,
        albumEntity: AlbumEntity? = null,
        artistEntity: ArtistEntity? = null
    ): String {
        // 1. Check album name or title metadata for language tags
        val titleText = mediaMetadata.title
        val albumText = mediaMetadata.album?.title.orEmpty()
        val artistText = mediaMetadata.artists.joinToString(" ") { it.name }

        val combinedMeta = "$titleText $albumText $artistText"

        for (lang in knownLanguageKeywords) {
            if (combinedMeta.contains(lang, ignoreCase = true)) {
                return normalizeLanguageName(lang)
            }
        }

        // 2. Check song entity tokens or album entity metadata if available
        songEntity?.let { entity ->
            val entityStr = "${entity.albumName} ${entity.title}"
            for (lang in knownLanguageKeywords) {
                if (entityStr.contains(lang, ignoreCase = true)) {
                    return normalizeLanguageName(lang)
                }
            }
        }

        albumEntity?.let { album ->
            for (lang in knownLanguageKeywords) {
                if (album.title.contains(lang, ignoreCase = true)) {
                    return normalizeLanguageName(lang)
                }
            }
        }

        // 3. Fallback to UNKNOWN if no factual metadata indicates language
        return "UNKNOWN"
    }

    private fun normalizeLanguageName(raw: String): String {
        return when (raw.uppercase()) {
            "K-POP", "KOREAN" -> "Korean"
            "J-POP", "JAPANESE" -> "Japanese"
            else -> raw.lowercase().replaceFirstChar { it.uppercase() }
        }
    }

    fun computeSessionProfile(recentTrackLanguages: List<String>): LanguageSessionProfile {
        val knownLanguages = recentTrackLanguages.filter { it != "UNKNOWN" }
        if (knownLanguages.isEmpty()) {
            return LanguageSessionProfile(
                primaryLanguage = "UNKNOWN",
                secondaryLanguage = null,
                recentLanguageCounts = emptyMap(),
                confidence = 0f
            )
        }

        val counts = knownLanguages.groupingBy { it }.eachCount()
        val sorted = counts.entries.sortedByDescending { it.value }

        val primary = sorted.firstOrNull()?.key ?: "UNKNOWN"
        val secondary = sorted.getOrNull(1)?.key
        val total = knownLanguages.size.toFloat()
        val confidence = (sorted.firstOrNull()?.value?.toFloat() ?: 0f) / total

        return LanguageSessionProfile(
            primaryLanguage = primary,
            secondaryLanguage = secondary,
            recentLanguageCounts = counts,
            confidence = confidence
        )
    }
}
