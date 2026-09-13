package com.music.echo.notune.flow

import androidx.compose.runtime.Immutable
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Immutable
data class MoodStep(
    val name: String,
    val targetEnergy: Float, // 0.0f to 1.0f
    val targetValence: Float // 0.0f (sad/melancholic) to 1.0f (happy/positive)
)

@Immutable
data class MoodJourneyState(
    val isActive: Boolean = false,
    val journeyName: String = "Gradual Uplift",
    val steps: List<MoodStep> = listOf(
        MoodStep("Sad", 0.2f, 0.1f),
        MoodStep("Melancholic", 0.3f, 0.3f),
        MoodStep("Calm", 0.4f, 0.5f),
        MoodStep("Warm", 0.5f, 0.7f),
        MoodStep("Hopeful", 0.7f, 0.85f),
        MoodStep("Positive", 0.85f, 0.95f)
    ),
    val currentStepIndex: Int = 0
)

@Singleton
class MoodJourneyEngine @Inject constructor() {

    private val _state = MutableStateFlow(MoodJourneyState())
    val state: StateFlow<MoodJourneyState> = _state.asStateFlow()

    fun startJourney(
        name: String = "Gradual Uplift",
        customSteps: List<MoodStep>? = null
    ) {
        val steps = customSteps ?: listOf(
            MoodStep("Sad", 0.2f, 0.1f),
            MoodStep("Melancholic", 0.3f, 0.3f),
            MoodStep("Calm", 0.4f, 0.5f),
            MoodStep("Warm", 0.5f, 0.7f),
            MoodStep("Hopeful", 0.7f, 0.85f),
            MoodStep("Positive", 0.85f, 0.95f)
        )
        _state.update {
            MoodJourneyState(
                isActive = true,
                journeyName = name,
                steps = steps,
                currentStepIndex = 0
            )
        }
    }

    fun advanceStep(): MoodStep? {
        val current = _state.value
        if (!current.isActive) return null

        val nextIndex = current.currentStepIndex + 1
        return if (nextIndex < current.steps.size) {
            _state.update { it.copy(currentStepIndex = nextIndex) }
            current.steps[nextIndex]
        } else {
            stopJourney()
            null
        }
    }

    fun stopJourney() {
        _state.update { it.copy(isActive = false) }
    }

    fun getCurrentStep(): MoodStep? {
        val current = _state.value
        if (!current.isActive) return null
        return current.steps.getOrNull(current.currentStepIndex)
    }
}
