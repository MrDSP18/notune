package com.music.echo.notune.engine

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import java.util.Calendar

enum class TimeOfDay(val label: String, val icon: String) {
    MORNING("Morning", "☀️"),
    DAY("Afternoon", "🌤️"),
    EVENING("Evening", "🌆"),
    NIGHT("Late Night", "🌙")
}

enum class EnvironmentProfile(val title: String, val icon: String) {
    NORMAL("Standard", "🎧"),
    DRIVING("Driving Mode", "🚗"),
    WORKOUT("Workout Energy", "🏃"),
    FOCUS("Deep Focus", "📖"),
    SLEEP("Sleep Ambient", "💤"),
    STUDIO("Audiophile Studio", "🎚️")
}

enum class BatteryState {
    NORMAL, LOW_POWER, CHARGING
}

data class NotuneContextState(
    val timeOfDay: TimeOfDay = TimeOfDay.EVENING,
    val environment: EnvironmentProfile = EnvironmentProfile.NORMAL,
    val batteryState: BatteryState = BatteryState.NORMAL,
    val activeMood: String = "Calm + Focused",
    val isOledOptimized: Boolean = false,
    val dominantColor: Color = Color(0xFF18181B),
    val accentColor: Color = Color(0xFFE4E4E7)
)

val LocalNotuneContextState = compositionLocalOf { NotuneContextState() }

object NotuneContextResolver {
    fun resolveTimeOfDay(hour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)): TimeOfDay {
        return when (hour) {
            in 5..11 -> TimeOfDay.MORNING
            in 12..16 -> TimeOfDay.DAY
            in 17..21 -> TimeOfDay.EVENING
            else -> TimeOfDay.NIGHT
        }
    }

    fun getSuggestedMood(timeOfDay: TimeOfDay): String {
        return when (timeOfDay) {
            TimeOfDay.MORNING -> "Acoustic Morning Coffee"
            TimeOfDay.DAY -> "Upbeat Afternoon Momentum"
            TimeOfDay.EVENING -> "Calm + Focused Evening"
            TimeOfDay.NIGHT -> "Late Night Ambient Explorations"
        }
    }
}
