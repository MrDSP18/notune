package com.music.echo.notune.flow

import javax.inject.Inject
import javax.inject.Singleton

data class SleepFlowState(
    val isActive: Boolean = false,
    val totalMinutes: Int = 30,
    val remainingMinutes: Int = 30,
    val currentVolumeFactor: Float = 1.0f
)

@Singleton
class SleepFlowManager @Inject constructor() {

    private var state = SleepFlowState()

    fun startSleepTimer(minutes: Int): SleepFlowState {
        state = SleepFlowState(
            isActive = true,
            totalMinutes = minutes,
            remainingMinutes = minutes,
            currentVolumeFactor = 1.0f
        )
        return state
    }

    fun updateProgress(elapsedMinutes: Int): SleepFlowState {
        if (!state.isActive) return state
        val remaining = (state.totalMinutes - elapsedMinutes).coerceAtLeast(0)
        val volumeFactor = (remaining.toFloat() / state.totalMinutes).coerceIn(0f, 1f)

        state = state.copy(
            remainingMinutes = remaining,
            currentVolumeFactor = volumeFactor,
            isActive = remaining > 0
        )
        return state
    }

    fun cancelSleepTimer(): SleepFlowState {
        state = SleepFlowState(isActive = false)
        return state
    }

    fun getSleepState(): SleepFlowState = state
}
