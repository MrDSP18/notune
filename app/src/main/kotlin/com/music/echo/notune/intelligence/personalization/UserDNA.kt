package com.music.echo.notune.intelligence.personalization

/**
 * NØTUNE Multidimensional User DNA Profile
 *
 * Models long-term taste separately from short-term/session intent and explicit preferences.
 */
data class NotuneUserDNA(
    val userId: String = "local_user",
    val coreTaste: CoreTaste = CoreTaste(),
    val behavioralTaste: BehavioralTaste = BehavioralTaste(),
    val contextTaste: ContextTaste = ContextTaste(),
    val sessionTaste: SessionTaste = SessionTaste(),
    val discoveryProfile: DiscoveryProfile = DiscoveryProfile(),
    val socialTaste: SocialTaste = SocialTaste(),
    val explicitPreferences: ExplicitPreferences = ExplicitPreferences(),
    val activeRules: List<PreferenceRule> = emptyList(),
    val lastUpdatedMs: Long = System.currentTimeMillis()
)

data class CoreTaste(
    val genres: Map<String, Float> = mapOf("Tamil Pop" to 0.8f, "Pop" to 0.7f, "Indie" to 0.5f),
    val artists: Map<String, Float> = emptyMap(),
    val languages: Map<String, Float> = mapOf("Tamil" to 0.9f, "English" to 0.7f),
    val eras: Map<String, Float> = mapOf("2000s" to 0.8f, "2010s" to 0.7f, "2020s" to 0.6f),
    val moods: Map<String, Float> = mapOf("Calm" to 0.6f, "Energetic" to 0.7f, "Melancholic" to 0.5f),
    val acousticPreferences: Map<String, Float> = mapOf("Acoustic" to 0.7f, "Electronic" to 0.5f),
    val energyTarget: Float = 0.65f,
    val tempoTargetBpm: Float = 115f,
    val instrumentation: Map<String, Float> = mapOf("Guitar" to 0.8f, "Piano" to 0.7f)
)

data class BehavioralTaste(
    val replayRate: Float = 0.15f,
    val skipRate: Float = 0.20f,
    val completionRate: Float = 0.80f,
    val saveRate: Float = 0.10f,
    val searchBehavior: String = "EXPLORATORY",
    val playlistBehavior: String = "ACTIVE_CURATOR",
    val discoveryBehavior: String = "BALANCED"
)

data class ContextTaste(
    val morning: ContextPreference = ContextPreference(preferredEnergy = 0.75f, topGenres = listOf("Acoustic", "Pop")),
    val afternoon: ContextPreference = ContextPreference(preferredEnergy = 0.65f, topGenres = listOf("Pop", "Indie")),
    val evening: ContextPreference = ContextPreference(preferredEnergy = 0.60f, topGenres = listOf("Melody", "Rock")),
    val night: ContextPreference = ContextPreference(preferredEnergy = 0.35f, topGenres = listOf("Ambient", "Melody", "Chill")),
    val work: ContextPreference = ContextPreference(preferredEnergy = 0.45f, topGenres = listOf("Instrumental", "Lo-Fi")),
    val study: ContextPreference = ContextPreference(preferredEnergy = 0.30f, topGenres = listOf("Classical", "Ambient")),
    val workout: ContextPreference = ContextPreference(preferredEnergy = 0.90f, topGenres = listOf("EDM", "Hip-Hop")),
    val travel: ContextPreference = ContextPreference(preferredEnergy = 0.70f, topGenres = listOf("Rock", "Pop")),
    val sleep: ContextPreference = ContextPreference(preferredEnergy = 0.20f, topGenres = listOf("Ambient", "Sleep"))
)

data class ContextPreference(
    val preferredEnergy: Float = 0.5f,
    val topGenres: List<String> = emptyList(),
    val preferredTempoBpm: Float = 100f
)

data class SessionTaste(
    val currentMood: String = "Balanced",
    val currentEnergy: Float = 0.60f,
    val currentGenre: String? = null,
    val currentLanguage: String? = null,
    val currentArtist: String? = null,
    val currentIntent: String? = null,
    val isExcludedSession: Boolean = false,
    val sessionStartedMs: Long = System.currentTimeMillis(),
    val totalSessionTracks: Int = 0,
    val consecutiveSkips: Int = 0
)

data class DiscoveryProfile(
    val noveltyTolerance: Float = 0.40f,
    val explorationRate: Float = 0.30f,
    val familiarMusicPreference: Float = 0.70f,
    val unknownArtistTolerance: Float = 0.35f
)

data class SocialTaste(
    val sharedPlaylists: List<String> = emptyList(),
    val activeRooms: List<String> = emptyList(),
    val friends: List<String> = emptyList(),
    val collaborativeHistoryCount: Int = 0
)

data class ExplicitPreferences(
    val likes: Set<String> = emptySet(),
    val dislikes: Set<String> = emptySet(),
    val neverRecommendArtists: Set<String> = emptySet(),
    val neverRecommendSongs: Set<String> = emptySet(),
    val alwaysRecommendArtists: Set<String> = emptySet(),
    val excludedListeningSessions: Set<String> = emptySet()
)

data class PreferenceRule(
    val id: String,
    val ruleType: RuleType,
    val scope: RuleScope,
    val condition: String,
    val actionTarget: String,
    val priority: Int = 5,
    val confidence: Float = 1.0f,
    val createdAtMs: Long = System.currentTimeMillis(),
    val expiresAtMs: Long? = null
)

enum class RuleType {
    EXCLUDE_GENRE,
    EXCLUDE_ARTIST,
    PREFER_GENRE,
    PREFER_LANGUAGE,
    BOOST_NOVELTY,
    SET_MAX_ENERGY,
    SET_MIN_ENERGY,
    CUSTOM_NATURAL_LANGUAGE
}

enum class RuleScope {
    PERMANENT,
    CURRENT_SESSION,
    TEMPORARY_WORKOUT,
    TEMPORARY_STUDY
}
