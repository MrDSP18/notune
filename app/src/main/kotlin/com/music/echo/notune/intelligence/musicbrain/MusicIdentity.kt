package com.music.echo.notune.intelligence.musicbrain

data class MusicIdentity(
    val trackId: String,
    val canonicalTitle: String,
    val normalizedTitle: String,
    val artistIds: List<String>,
    val canonicalArtists: List<String>,
    val albumId: String? = null,
    val albumTitle: String? = null,
    val releaseYear: Int? = null,
    val durationSeconds: Int = 0,
    val primaryLanguage: String = "English",
    val secondaryLanguages: List<String> = emptyList(),
    val primaryGenre: String = "Pop",
    val subGenres: List<String> = emptyList(),
    val providerIds: Map<String, String> = emptyMap(), // e.g. "youtube" -> "v123", "local" -> "/path/file.mp3"
    val isPlayable: Boolean = true,
    val sourceConfidence: Float = 1.0f,
    val metadataConfidence: Float = 1.0f
) {
    fun matches(other: MusicIdentity): Boolean {
        if (trackId == other.trackId) return true
        val titleMatch = normalizedTitle.equals(other.normalizedTitle, ignoreCase = true)
        val artistMatch = canonicalArtists.any { a -> other.canonicalArtists.any { b -> a.equals(b, ignoreCase = true) } }
        return titleMatch && artistMatch
    }

    companion object {
        fun normalizeString(input: String): String {
            return input.lowercase()
                .replace(Regex("[^a-z0-9\\s]"), "")
                .replace(Regex("\\s+"), " ")
                .trim()
        }
    }
}
