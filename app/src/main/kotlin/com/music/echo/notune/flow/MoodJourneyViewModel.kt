package com.music.echo.notune.flow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MoodJourneyViewModel @Inject constructor(
    private val moodJourneyEngine: MoodJourneyEngine
) : ViewModel() {

    val journeyState: StateFlow<MoodJourneyState> = moodJourneyEngine.state

    val availablePresets = listOf(
        "Gradual Uplift" to listOf(
            MoodStep("Sad", 0.2f, 0.1f),
            MoodStep("Melancholic", 0.3f, 0.3f),
            MoodStep("Calm", 0.4f, 0.5f),
            MoodStep("Warm", 0.5f, 0.7f),
            MoodStep("Hopeful", 0.7f, 0.85f),
            MoodStep("Positive", 0.85f, 0.95f)
        ),
        "Deep Focus Flow" to listOf(
            MoodStep("Energetic", 0.7f, 0.5f),
            MoodStep("Steady", 0.5f, 0.5f),
            MoodStep("Calm", 0.4f, 0.6f),
            MoodStep("Flow", 0.4f, 0.7f),
            MoodStep("Zen", 0.3f, 0.8f)
        ),
        "Energy Arc" to listOf(
            MoodStep("Resting", 0.2f, 0.5f),
            MoodStep("Awakening", 0.5f, 0.6f),
            MoodStep("Energetic", 0.8f, 0.8f),
            MoodStep("Peak Energy", 0.95f, 0.9f),
            MoodStep("Wind Down", 0.4f, 0.7f)
        )
    )

    fun startPreset(presetName: String) {
        val preset = availablePresets.find { it.first == presetName }
        if (preset != null) {
            moodJourneyEngine.startJourney(name = preset.first, customSteps = preset.second)
        } else {
            moodJourneyEngine.startJourney()
        }
    }

    fun advanceStep() {
        moodJourneyEngine.advanceStep()
    }

    fun stopJourney() {
        moodJourneyEngine.stopJourney()
    }
}
