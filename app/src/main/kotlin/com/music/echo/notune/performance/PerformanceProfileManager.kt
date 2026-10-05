package com.music.echo.notune.performance

import android.content.Context
import android.os.PowerManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

enum class PerformanceProfile {
    AUTO,
    BATTERY_SAVER,
    BALANCED,
    PERFORMANCE
}

@Singleton
class PerformanceProfileManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val _currentProfile = MutableStateFlow(PerformanceProfile.AUTO)
    val currentProfile: StateFlow<PerformanceProfile> = _currentProfile.asStateFlow()

    fun setProfile(profile: PerformanceProfile) {
        _currentProfile.value = profile
    }

    val isBlurEnabled: Boolean
        get() {
            if (_currentProfile.value == PerformanceProfile.BATTERY_SAVER) return false
            if (_currentProfile.value == PerformanceProfile.PERFORMANCE) return true
            return !isPowerSaveMode() && getAvailableRamMb() > 2048
        }

    val isVisualizerEnabled: Boolean
        get() {
            if (_currentProfile.value == PerformanceProfile.BATTERY_SAVER) return false
            return !isPowerSaveMode()
        }

    val isBackgroundAiEnabled: Boolean
        get() {
            if (_currentProfile.value == PerformanceProfile.BATTERY_SAVER) return false
            return !isPowerSaveMode()
        }

    val isHeavyAnimationEnabled: Boolean
        get() {
            if (_currentProfile.value == PerformanceProfile.BATTERY_SAVER) return false
            return getAvailableRamMb() > 1024
        }

    private fun isPowerSaveMode(): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        return powerManager?.isPowerSaveMode == true
    }

    private fun getAvailableRamMb(): Long {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? android.app.ActivityManager
        val memoryInfo = android.app.ActivityManager.MemoryInfo()
        activityManager?.getMemoryInfo(memoryInfo)
        return memoryInfo.availMem / (1024 * 1024)
    }
}
