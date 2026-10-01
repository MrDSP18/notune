package com.music.echo.notune.intelligence.memory

import kotlinx.serialization.Serializable
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

enum class MemorySource {
    EXPLICIT, BEHAVIORAL, INFERRED
}

enum class MemoryCategory {
    EXPLICIT, BEHAVIORAL, SESSION, MUSIC
}

@Serializable
data class MemoryItem(
    val id: String,
    val category: MemoryCategory,
    val key: String,
    val value: String,
    val source: MemorySource,
    val confidence: Float = 1.0f,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val decayRate: Float = 0.05f
) {
    fun calculateEffectiveConfidence(now: Long = System.currentTimeMillis()): Float {
        if (source == MemorySource.EXPLICIT) return 1.0f
        val daysElapsed = TimeUnit.MILLISECONDS.toDays(now - updatedAt).coerceAtLeast(0)
        val decayFactor = Math.pow((1.0 - decayRate).toDouble(), daysElapsed.toDouble()).toFloat()
        return (confidence * decayFactor).coerceIn(0.0f, 1.0f)
    }
}

data class ExplicitMemory(
    val favoriteArtists: List<String> = emptyList(),
    val preferredLanguages: List<String> = emptyList(),
    val dislikedGenres: List<String> = emptyList(),
    val customRules: List<String> = emptyList()
)

data class BehavioralMemory(
    val frequentlyReplayedArtists: Map<String, Int> = emptyMap(),
    val completedGenres: Map<String, Int> = emptyMap(),
    val skippedArtists: Map<String, Int> = emptyMap(),
    val discoveryTolerance: Float = 0.5f,
    val averageSessionDurationMinutes: Int = 45
)

data class SessionMemory(
    val activeMood: String = "Calm + Focused",
    val recentSongs: List<String> = emptyList(),
    val recentSkips: List<String> = emptyList(),
    val currentEnergy: Float = 0.6f
)

data class MusicMemory(
    val songsHeardCount: Int = 0,
    val artistsExploredCount: Int = 0,
    val albumsCompletedCount: Int = 0,
    val totalLyricsSearches: Int = 0,
    val totalShares: Int = 0
)

@Singleton
class UserMemoryEngine @Inject constructor() {

    private val _memories = mutableMapOf<String, MemoryItem>()

    fun getMemories(): List<MemoryItem> = _memories.values.toList()

    fun recordMemory(
        category: MemoryCategory,
        key: String,
        value: String,
        source: MemorySource,
        initialConfidence: Float = 1.0f,
        decayRate: Float = 0.05f
    ) {
        val memoryId = "${category.name}_${key}"
        val existing = _memories[memoryId]
        val updatedConfidence = if (existing != null && source != MemorySource.EXPLICIT) {
            (existing.confidence + 0.15f).coerceAtMost(1.0f)
        } else {
            initialConfidence
        }

        _memories[memoryId] = MemoryItem(
            id = memoryId,
            category = category,
            key = key,
            value = value,
            source = source,
            confidence = updatedConfidence,
            createdAt = existing?.createdAt ?: System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            decayRate = decayRate
        )
    }

    fun removeMemory(memoryId: String) {
        _memories.remove(memoryId)
    }

    fun clearCategory(category: MemoryCategory) {
        _memories.entries.removeIf { it.value.category == category }
    }

    fun resetAllMemories() {
        _memories.clear()
    }

    fun getActiveMemoriesForPrompt(): List<MemoryItem> {
        val now = System.currentTimeMillis()
        return _memories.values
            .filter { it.calculateEffectiveConfidence(now) > 0.2f }
            .sortedByDescending { it.calculateEffectiveConfidence(now) }
    }
}
