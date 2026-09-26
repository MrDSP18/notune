package com.music.echo.notune.intelligence.personalization

/**
 * 3-Layer Taste Model for NØTUNE:
 *
 * Layer 1: LongTermTaste (What the user usually loves - core preferences)
 * Layer 2: RecentTaste   (What changed recently - last 5-10 songs)
 * Layer 3: SessionTaste  (What fits right now - active session context)
 *
 * Final Recommendation Score = 0.40 * LongTerm + 0.35 * Recent + 0.25 * Session
 */
data class UserTasteProfile(
    val userId: String = "local_user",
    val longTermTaste: LongTermTaste = LongTermTaste(),
    val recentTaste: RecentTaste = RecentTaste(),
    val sessionTaste: SessionTaste = SessionTaste(),
    val noveltyPreference: Float = 0.35f,
    val familiarityPreference: Float = 0.70f,
    val lastUpdatedMs: Long = System.currentTimeMillis()
)

data class LongTermTaste(
    val genres: Map<String, Float> = mapOf("Tamil Pop" to 0.80f, "Pop" to 0.70f, "Indie" to 0.50f),
    val artists: Map<String, Float> = emptyMap(),
    val languages: Map<String, Float> = mapOf("Tamil" to 0.90f, "English" to 0.70f),
    val eras: Map<String, Float> = mapOf("2000s" to 0.80f, "2010s" to 0.70f),
    val averageEnergy: Float = 0.65f,
    val averageValence: Float = 0.60f
)

data class RecentTaste(
    val lastSongsCount: Int = 0,
    val recentGenres: Map<String, Float> = emptyMap(),
    val recentArtists: Map<String, Float> = emptyMap(),
    val recentLanguages: Map<String, Float> = emptyMap(),
    val recentAvgEnergy: Float = 0.60f,
    val recentAvgValence: Float = 0.55f,
    val last5TrackIds: List<String> = emptyList()
)
