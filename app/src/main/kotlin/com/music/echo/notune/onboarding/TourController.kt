package com.music.echo.notune.onboarding

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import echo.music.iad1tya.utils.dataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

private val PREF_TOUR_COMPLETED = booleanPreferencesKey("pref_tour_completed")
private val PREF_TOUR_SKIPPED = booleanPreferencesKey("pref_tour_skipped")
private val PREF_TOUR_LAST_STEP = intPreferencesKey("pref_tour_last_step")

@Singleton
class TourController @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _isTourActive = MutableStateFlow(false)
    val isTourActive: StateFlow<Boolean> = _isTourActive.asStateFlow()

    private val _currentStepIndex = MutableStateFlow(0)
    val currentStepIndex: StateFlow<Int> = _currentStepIndex.asStateFlow()

    val currentStep: TourStep
        get() = TourStepsCatalog.ALL_STEPS.getOrElse(_currentStepIndex.value) { TourStepsCatalog.ALL_STEPS.first() }

    init {
        scope.launch {
            val completed = context.dataStore.data.first()[PREF_TOUR_COMPLETED] ?: false
            val skipped = context.dataStore.data.first()[PREF_TOUR_SKIPPED] ?: false
            if (!completed && !skipped) {
                // First launch: tour is available
                _currentStepIndex.value = context.dataStore.data.first()[PREF_TOUR_LAST_STEP] ?: 0
            }
        }
    }

    fun startTour() {
        _currentStepIndex.value = 0
        _isTourActive.value = true
    }

    fun nextStep() {
        if (_currentStepIndex.value < TourStepsCatalog.ALL_STEPS.size - 1) {
            _currentStepIndex.value += 1
            persistStep(_currentStepIndex.value)
        } else {
            completeTour()
        }
    }

    fun previousStep() {
        if (_currentStepIndex.value > 0) {
            _currentStepIndex.value -= 1
            persistStep(_currentStepIndex.value)
        }
    }

    fun skipTour() {
        _isTourActive.value = false
        scope.launch {
            context.dataStore.edit { prefs ->
                prefs[PREF_TOUR_SKIPPED] = true
                prefs[PREF_TOUR_COMPLETED] = false
            }
        }
    }

    fun completeTour() {
        _isTourActive.value = false
        scope.launch {
            context.dataStore.edit { prefs ->
                prefs[PREF_TOUR_COMPLETED] = true
                prefs[PREF_TOUR_SKIPPED] = false
            }
        }
    }

    private fun persistStep(stepIndex: Int) {
        scope.launch {
            context.dataStore.edit { prefs ->
                prefs[PREF_TOUR_LAST_STEP] = stepIndex
            }
        }
    }
}
