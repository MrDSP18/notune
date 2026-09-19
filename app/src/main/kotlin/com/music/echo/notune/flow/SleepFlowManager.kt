package com.music.echo.notune.flow

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SleepFlowState(
    val isActive: Boolean = false,
    val totalMinutes: Int = 30,
    val remainingMinutes: Int = 30,
    val currentVolumeFactor: Float = 1.0f
)

/**
 * Tracks the progressive volume-fade state during a sleep timer countdown.
 *
 * Works alongside [echo.music.iad1tya.playback.SleepTimer]: the existing SleepTimer handles
 * the final 3-second fade and player pause. This manager provides a minute-by-minute
 * volume factor that MusicService applies to gradually reduce volume across the full countdown
 * period (e.g., over 30 minutes, volume declines from 100% → ~0% before the final fade).
 */
@Singleton
class SleepFlowManager @Inject constructor() {

    private val _state = MutableStateFlow(SleepFlowState())

    /** Observable state for the UI to display fade progress. */
    val state: StateFlow<SleepFlowState> = _state.asStateFlow()

    fun startSleepTimer(minutes: Int): SleepFlowState {
        val newState = SleepFlowState(
            isActive = true,
            totalMinutes = minutes,
            remainingMinutes = minutes,
            currentVolumeFactor = 1.0f
        )
        _state.value = newState
        return newState
    }

    fun updateProgress(elapsedMinutes: Int): SleepFlowState {
        val current = _state.value
        if (!current.isActive) return current
        val remaining = (current.totalMinutes - elapsedMinutes).coerceAtLeast(0)
        val volumeFactor = (remaining.toFloat() / current.totalMinutes).coerceIn(0f, 1f)

        val newState = current.copy(
            remainingMinutes = remaining,
            currentVolumeFactor = volumeFactor,
            isActive = remaining > 0
        )
        _state.value = newState
        return newState
    }

    fun cancelSleepTimer(): SleepFlowState {
        val newState = SleepFlowState(isActive = false, currentVolumeFactor = 1.0f)
        _state.value = newState
        return newState
    }

    fun getSleepState(): SleepFlowState = _state.value
}
