package echo.music.iad1tya.notune.intelligence.knowledge

/**
 * Flexible cast role labels supporting Indian and international cinema models.
 */
enum class CastRole {
    HERO,
    HEROINE,
    LEAD_ACTOR,
    LEAD_ACTRESS,
    SUPPORTING_ACTOR,
    SUPPORTING_ACTRESS,
    CAMEO,
    FEATURED_PERFORMER
}

/**
 * Crew roles for film and music production.
 */
enum class CrewRole {
    DIRECTOR,
    PRODUCER,
    MUSIC_DIRECTOR,
    COMPOSER,
    LYRICIST,
    CINEMATOGRAPHER,
    EDITOR,
    WRITER
}

/**
 * Information model for individuals (actors, actresses, directors, composers, lyricists).
 */
data class PersonDetails(
    val personId: String,
    val name: String,
    val roleName: String? = null,
    val profileArtwork: MediaArtwork? = null,
    val biography: String? = null,
    val knownWorks: List<String> = emptyList(),
    val source: MetadataSource = MetadataSource.UNKNOWN
)

/**
 * Cast member relationship in film/soundtrack context.
 */
data class CastMember(
    val person: PersonDetails,
    val role: CastRole,
    val characterName: String? = null
)

/**
 * Crew member relationship in film/soundtrack context.
 */
data class CrewMember(
    val person: PersonDetails,
    val role: CrewRole
)
