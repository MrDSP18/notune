package echo.music.iad1tya.notune.intelligence.enrichment

import echo.music.iad1tya.notune.intelligence.knowledge.MetadataSource

/**
 * Match confidence levels for song and entity identification.
 */
enum class MatchConfidence {
    EXACT,       // >= 0.95: Perfect match across title, artist, duration/ISRC
    HIGH,        // >= 0.80: Strong match across title and artist
    MEDIUM,      // >= 0.60: Moderate match, partial fields matched
    LOW,         // >= 0.35: Weak match, candidate fields questionable
    UNKNOWN      // < 0.35:  Uncertain or unverified identity
}

/**
 * Normalized song identity representation.
 */
data class SongIdentity(
    val cleanTitle: String,
    val cleanArtist: String,
    val cleanAlbum: String? = null,
    val cleanMovie: String? = null,
    val featuredArtists: List<String> = emptyList(),
    val language: String? = null,
    val year: Int? = null,
    val durationMs: Long = 0L,
    val isrc: String? = null,
    val rawInput: String = ""
)

/**
 * Song identity match result containing scoring and provider provenance.
 */
data class SongIdentityMatch(
    val identity: SongIdentity,
    val matchConfidence: MatchConfidence,
    val confidenceScore: Float,
    val matchingFields: List<String> = emptyList(),
    val providerId: String = "local",
    val source: MetadataSource = MetadataSource.UNKNOWN
)
