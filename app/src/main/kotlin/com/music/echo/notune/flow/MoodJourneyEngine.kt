package com.music.echo.notune.flow

import javax.inject.Inject
import javax.inject.Singleton

enum class MoodJourneyStage(val displayName: String, val targetEnergy: Float) {
    CALM("Calm", 0.2f),
    CHILL("Chill", 0.4f),
    HAPPY("Happy", 0.6f),
    ENERGETIC("Energetic", 0.8f),
    EUPHORIC("Euphoric", 1.0f)
}

data class MoodJourneyConfig(
    val stages: List<MoodJourneyStage> = listOf(
        MoodJourneyStage.CALM,
        MoodJourneyStage.CHILL,
        MoodJourneyStage.HAPPY,
        MoodJourneyStage.ENERGETIC,
        MoodJourneyStage.EUPHORIC
    ),
    val currentStageIndex: Int = 0
)

@Singleton
class MoodJourneyEngine @Inject constructor() {

    private var activeConfig: MoodJourneyConfig? = null

    fun startJourney(config: MoodJourneyConfig = MoodJourneyConfig()) {
        activeConfig = config
    }

    fun stopJourney() {
        activeConfig = null
    }

    fun isJourneyActive(): Boolean = activeConfig != null

    fun getCurrentStage(): MoodJourneyStage? {
        val config = activeConfig ?: return null
        return config.stages.getOrNull(config.currentStageIndex)
    }

    fun advanceStage(): MoodJourneyStage? {
        val config = activeConfig ?: return null
        val nextIndex = config.currentStageIndex + 1
        return if (nextIndex < config.stages.size) {
            activeConfig = config.copy(currentStageIndex = nextIndex)
            config.stages[nextIndex]
        } else {
            stopJourney()
            null
        }
    }
}
