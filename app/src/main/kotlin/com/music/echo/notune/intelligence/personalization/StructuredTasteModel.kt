package com.music.echo.notune.intelligence.personalization

import com.music.echo.notune.intelligence.memory.MemoryCategory
import com.music.echo.notune.intelligence.memory.MemorySource
import com.music.echo.notune.intelligence.memory.UserMemoryEngine
import javax.inject.Inject
import javax.inject.Singleton

enum class PreferenceSource {
    ONBOARDING,
    PLAYBACK,
    COMPLETION,
    SKIP,
    REPLAY,
    FAVORITE,
    SEARCH,
    QUEUE_SELECTION,
    PLAYLIST,
    SHARING,
    DOWNLOAD,
    EXPLICIT_COMMAND
}

data class TasteEntry(
    val key: String,
    val value: String,
    val weight: Float,
    val confidence: Float = 0.8f,
    val source: PreferenceSource = PreferenceSource.PLAYBACK,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val decayRate: Float = 0.05f
)

@Singleton
class StructuredTasteModel @Inject constructor(
    private val userMemoryEngine: UserMemoryEngine
) {
    fun recordPreference(
        key: String,
        value: String,
        deltaWeight: Float,
        source: PreferenceSource
    ) {
        val multiplier = if (source == PreferenceSource.EXPLICIT_COMMAND) 3.0f else 1.0f
        val effectiveConfidence = (deltaWeight * multiplier).coerceIn(0.1f, 1.0f)

        val memorySource = if (source == PreferenceSource.EXPLICIT_COMMAND) MemorySource.EXPLICIT else MemorySource.BEHAVIORAL

        userMemoryEngine.recordMemory(
            category = MemoryCategory.BEHAVIORAL,
            key = key,
            value = value,
            source = memorySource,
            initialConfidence = effectiveConfidence
        )
    }

    fun getPreferenceWeight(key: String, default: Float = 0.5f): Float {
        val item = userMemoryEngine.getMemories().find { it.key == key }
        return item?.calculateEffectiveConfidence() ?: default
    }
}
