package com.music.echo.notune.intelligence.intent

enum class IntentType {
    PLAY_MUSIC,
    FIND_SIMILAR,
    MODIFY_QUEUE,
    FILTER_QUEUE_LANGUAGE,
    SESSION_EXCLUSION,
    DISCOVER_UNHEARD,
    CREATE_TIME_LIMITED_QUEUE,
    SEARCH_WITH_CONSTRAINTS,
    EXPLAIN_RECOMMENDATION,
    TRANSLATE_LYRICS,
    CREATE_ROOM,
    SHARE_PLAYLIST,
    OTHER
}

data class IntentConstraints(
    val minEnergy: Float? = null,
    val maxEnergy: Float? = null,
    val discoveryRequested: Boolean = false,
    val underratedRequested: Boolean = false,
    val excludeArtists: List<String> = emptyList(),
    val excludeGenres: List<String> = emptyList(),
    val targetEra: String? = null
)

data class StructuredUserIntent(
    val intentType: IntentType,
    val rawQuery: String,
    val targetArtist: String? = null,
    val targetGenre: String? = null,
    val targetLanguage: String? = null,
    val targetMood: String? = null,
    val targetTrack: String? = null,
    val durationMinutes: Int? = null,
    val constraints: IntentConstraints = IntentConstraints(),
    val requestedAction: String,
    val confidence: Float = 0.90f,
    val sessionScope: Boolean = true,
    val persistentPreferenceChange: Boolean = false
)
