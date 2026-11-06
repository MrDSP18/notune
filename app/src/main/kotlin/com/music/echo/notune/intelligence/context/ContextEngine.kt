package com.music.echo.notune.intelligence.context

import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * NØTUNE Context Engine
 *
 * Infers music context based on non-invasive signals:
 * - Time of day & day of week
 * - Audio output device (Headphones, Speaker, Car)
 * - Session duration & playback pace
 * - Recent genre/energy patterns
 *
 * (Location is strictly optional and privacy-controlled).
 */
@Singleton
class ContextEngine @Inject constructor() {

    fun getTimeContext(now: LocalDateTime = LocalDateTime.now()): TimeContext {
        val hour = now.hour
        val timeOfDay = when (hour) {
            in 5..11 -> TimeOfDay.MORNING
            in 12..16 -> TimeOfDay.AFTERNOON
            in 17..21 -> TimeOfDay.EVENING
            else -> TimeOfDay.NIGHT
        }
        val isWeekend = now.dayOfWeek.value >= 6
        return TimeContext(timeOfDay = timeOfDay, hourOfDay = hour, isWeekend = isWeekend)
    }

    fun inferSituation(
        timeContext: TimeContext = getTimeContext(),
        audioDevice: AudioDeviceType = AudioDeviceType.HEADPHONES,
        recentAvgEnergy: Float = 0.6f,
        userExplicitActivity: String? = null
    ): InferredSituation {
        // 1. Explicit user activity override
        if (!userExplicitActivity.isNullOrEmpty()) {
            val act = when (userExplicitActivity.lowercase()) {
                "workout", "gym" -> InferredActivity.WORKOUT
                "study", "work", "focus" -> InferredActivity.STUDY_WORK
                "night", "sleep", "relax" -> InferredActivity.NIGHT_RELAX
                "travel", "drive", "car" -> InferredActivity.TRAVEL_COMMUTE
                else -> InferredActivity.GENERAL
            }
            return buildSituationForActivity(act, timeContext.timeOfDay)
        }

        // 2. Heuristic inference
        val activity = when {
            audioDevice == AudioDeviceType.CAR_AUDIO -> InferredActivity.TRAVEL_COMMUTE
            timeContext.timeOfDay == TimeOfDay.NIGHT && recentAvgEnergy < 0.45f -> InferredActivity.NIGHT_RELAX
            timeContext.timeOfDay == TimeOfDay.MORNING && recentAvgEnergy > 0.70f -> InferredActivity.MORNING_ENERGY
            recentAvgEnergy > 0.85f && audioDevice == AudioDeviceType.HEADPHONES -> InferredActivity.WORKOUT
            timeContext.timeOfDay == TimeOfDay.AFTERNOON && recentAvgEnergy < 0.40f -> InferredActivity.STUDY_WORK
            else -> InferredActivity.GENERAL
        }

        return buildSituationForActivity(activity, timeContext.timeOfDay)
    }

    private fun buildSituationForActivity(activity: InferredActivity, timeOfDay: TimeOfDay): InferredSituation {
        return when (activity) {
            InferredActivity.WORKOUT -> InferredSituation(
                activity = activity,
                timeOfDay = timeOfDay,
                targetEnergy = 0.88f,
                targetTempoBpm = 135f,
                confidence = 0.85f,
                description = "High Energy Workout Mode"
            )
            InferredActivity.STUDY_WORK -> InferredSituation(
                activity = activity,
                timeOfDay = timeOfDay,
                targetEnergy = 0.35f,
                targetTempoBpm = 95f,
                confidence = 0.80f,
                description = "Focus & Study Mode"
            )
            InferredActivity.NIGHT_RELAX -> InferredSituation(
                activity = activity,
                timeOfDay = timeOfDay,
                targetEnergy = 0.25f,
                targetTempoBpm = 80f,
                confidence = 0.90f,
                description = "Late Night Chill & Relax"
            )
            InferredActivity.TRAVEL_COMMUTE -> InferredSituation(
                activity = activity,
                timeOfDay = timeOfDay,
                targetEnergy = 0.65f,
                targetTempoBpm = 115f,
                confidence = 0.82f,
                description = "Travel & Drive Companion"
            )
            InferredActivity.MORNING_ENERGY -> InferredSituation(
                activity = activity,
                timeOfDay = timeOfDay,
                targetEnergy = 0.75f,
                targetTempoBpm = 120f,
                confidence = 0.78f,
                description = "Fresh Morning Energy"
            )
            InferredActivity.PARTY_SOCIAL -> InferredSituation(
                activity = activity,
                timeOfDay = timeOfDay,
                targetEnergy = 0.85f,
                targetTempoBpm = 128f,
                confidence = 0.80f,
                description = "Party & Social Vibe"
            )
            InferredActivity.GENERAL -> InferredSituation(
                activity = activity,
                timeOfDay = timeOfDay,
                targetEnergy = 0.60f,
                targetTempoBpm = 110f,
                confidence = 0.70f,
                description = "Balanced Personal Flow"
            )
        }
    }
}
