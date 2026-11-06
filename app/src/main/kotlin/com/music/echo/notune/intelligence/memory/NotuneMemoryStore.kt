package com.music.echo.notune.intelligence.memory

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotuneMemoryStore @Inject constructor() {

    private val engine = UserMemoryEngine()
    private val _memoriesFlow = MutableStateFlow<List<MemoryItem>>(emptyList())
    val memoriesFlow: StateFlow<List<MemoryItem>> = _memoriesFlow.asStateFlow()

    init {
        // Seed default foundational memory items
        engine.recordMemory(MemoryCategory.EXPLICIT, "language_preference", "Tamil, English", MemorySource.EXPLICIT)
        engine.recordMemory(MemoryCategory.BEHAVIORAL, "frequently_replayed_artist", "Anuv Jain", MemorySource.BEHAVIORAL, 0.85f)
        engine.recordMemory(MemoryCategory.BEHAVIORAL, "completed_genre", "Melody & Acoustic", MemorySource.BEHAVIORAL, 0.90f)
        engine.recordMemory(MemoryCategory.SESSION, "current_mood", "Calm + Focused", MemorySource.INFERRED, 0.75f)
        syncFlow()
    }

    fun record(
        category: MemoryCategory,
        key: String,
        value: String,
        source: MemorySource,
        confidence: Float = 1.0f,
        decayRate: Float = 0.05f
    ) {
        engine.recordMemory(category, key, value, source, confidence, decayRate)
        syncFlow()
    }

    fun remove(memoryId: String) {
        engine.removeMemory(memoryId)
        syncFlow()
    }

    fun clearCategory(category: MemoryCategory) {
        engine.clearCategory(category)
        syncFlow()
    }

    fun resetAll() {
        engine.resetAllMemories()
        syncFlow()
    }

    fun getEngineSnapshot(): UserMemoryEngine = engine

    private fun syncFlow() {
        _memoriesFlow.value = engine.getActiveMemoriesForPrompt()
    }
}
