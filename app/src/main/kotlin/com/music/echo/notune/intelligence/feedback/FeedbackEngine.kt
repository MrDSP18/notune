package com.music.echo.notune.intelligence.feedback

import com.music.echo.notune.intelligence.memory.MemoryCategory
import com.music.echo.notune.intelligence.memory.MemorySource
import com.music.echo.notune.intelligence.memory.UserMemoryEngine
import com.music.echo.notune.intelligence.personalization.PreferenceSource
import com.music.echo.notune.intelligence.personalization.StructuredTasteModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedbackEngine @Inject constructor(
    private val rewardCalculator: RewardCalculator,
    private val userMemoryEngine: UserMemoryEngine,
    private val structuredTasteModel: StructuredTasteModel
) {

    fun processUserEvent(event: UserEvent): Float {
        val reward = rewardCalculator.calculateReward(event)
        userMemoryEngine.recordMemory(
            category = MemoryCategory.SESSION,
            key = event.javaClass.simpleName,
            value = event.toString(),
            source = MemorySource.BEHAVIORAL
        )

        when (event) {
            is UserEvent.Favorited -> {
                structuredTasteModel.recordPreference("artist_${event.artistName}", event.artistName, 0.40f, PreferenceSource.FAVORITE)
            }
            is UserEvent.Completed -> {
                structuredTasteModel.recordPreference("artist_${event.artistName}", event.artistName, 0.15f, PreferenceSource.COMPLETION)
            }
            is UserEvent.EarlySkip -> {
                structuredTasteModel.recordPreference("artist_${event.artistName}", event.artistName, -0.25f, PreferenceSource.SKIP)
            }
            is UserEvent.TeachRule -> {
                structuredTasteModel.recordPreference("rule_${event.rawDirective}", event.rawDirective, 1.0f, PreferenceSource.EXPLICIT_COMMAND)
            }
            else -> {}
        }

        return reward
    }
}
