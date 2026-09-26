package com.music.echo.notune.intelligence.context

enum class TimeOfDay {
    MORNING,    // 05:00 - 11:59
    AFTERNOON,  // 12:00 - 16:59
    EVENING,    // 17:00 - 21:59
    NIGHT       // 22:00 - 04:59
}

enum class InferredActivity {
    WORKOUT,
    STUDY_WORK,
    NIGHT_RELAX,
    TRAVEL_COMMUTE,
    MORNING_ENERGY,
    PARTY_SOCIAL,
    GENERAL
}

data class TimeContext(
    val timeOfDay: TimeOfDay = TimeOfDay.AFTERNOON,
    val hourOfDay: Int = 14,
    val isWeekend: Boolean = false
)

data class PlaybackContext(
    val audioDeviceType: AudioDeviceType = AudioDeviceType.HEADPHONES,
    val isNetworkCellular: Boolean = false,
    val playbackVolumeLevel: Float = 0.7f
)

enum class AudioDeviceType {
    HEADPHONES,
    BLUETOOTH_SPEAKER,
    PHONE_SPEAKER,
    CAR_AUDIO,
    UNKNOWN
}

data class InferredSituation(
    val activity: InferredActivity = InferredActivity.GENERAL,
    val timeOfDay: TimeOfDay = TimeOfDay.AFTERNOON,
    val targetEnergy: Float = 0.6f,
    val targetTempoBpm: Float = 110f,
    val confidence: Float = 0.75f,
    val description: String = "Balanced Afternoon Listening"
)
