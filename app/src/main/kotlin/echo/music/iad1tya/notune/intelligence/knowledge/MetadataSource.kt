package echo.music.iad1tya.notune.intelligence.knowledge

/**
 * Metadata provenance and confidence level identifier.
 * Prevents presenting inferred or default fallback metadata as confirmed fact.
 */
enum class MetadataSource {
    /** Resolved from local MP3/FLAC ID3/Vorbis tags or local Room database. */
    LOCAL,

    /** Resolved from NØTUNE Cloudflare Edge Worker API. */
    BACKEND,

    /** Resolved from external metadata provider (YouTube, Last.fm, InnerTube, etc.). */
    PROVIDER,

    /** Manually edited or provided by the user. */
    USER,

    /** Inferred algorithmically by NØ AI. */
    INFERRED,

    /** Fallback default when metadata is unresolvable or unavailable. */
    UNKNOWN
}
