package echo.music.iad1tya.notune.intelligence.knowledge

/**
 * Types of relationships within the NØTUNE Music Knowledge Graph.
 */
enum class RelationshipType {
    COMPOSER_OF,
    SINGER_OF,
    LYRICIST_OF,
    SOUNDTRACK_FOR_MOVIE,
    ARTIST_COLLABORATION,
    RELATED_GENRE
}

/**
 * Directed relationship edge in knowledge graph.
 */
data class MusicRelationship(
    val fromId: String,
    val toId: String,
    val type: RelationshipType,
    val strength: Float = 1.0f,
    val description: String? = null
)
