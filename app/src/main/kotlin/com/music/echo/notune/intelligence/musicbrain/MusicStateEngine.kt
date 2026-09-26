package com.music.echo.notune.intelligence.musicbrain

import javax.inject.Inject
import javax.inject.Singleton

enum class MusicMoodState {
    CALM,
    ENERGETIC,
    UPBEAT,
    DREAMY,
    ROMANTIC,
    MELANCHOLIC,
    FOCUSED,
    NOSTALGIC,
    DARK,
    PARTY,
    DISCOVERY
}

data class InferredMusicState(
    val moodState: MusicMoodState = MusicMoodState.CALM,
    val targetEnergy: Float = 0.45f,
    val targetValence: Float = 0.50f,
    val targetAcousticness: Float = 0.65f,
    val targetTempoBpm: Float = 95f,
    val confidence: Float = 0.82f,
    val quietUserMessage: String = "Keeping the flow calm"
)

@Singleton
class MusicStateEngine @Inject constructor() {

    fun inferMusicStateFromRecent(
        recentAvgEnergy: Float,
        recentAvgValence: Float,
        timeOfDayHour: Int = 14
    ): InferredMusicState {
        val moodState = when {
            timeOfDayHour in 22..23 || timeOfDayHour in 0..4 -> {
                if (recentAvgEnergy < 0.40f) MusicMoodState.CALM else MusicMoodState.DREAMY
            }
            recentAvgEnergy > 0.80f -> MusicMoodState.ENERGETIC
            recentAvgEnergy > 0.65f && recentAvgValence > 0.70f -> MusicMoodState.UPBEAT
            recentAvgEnergy < 0.35f && recentAvgValence < 0.40f -> MusicMoodState.MELANCHOLIC
            recentAvgEnergy < 0.45f -> MusicMoodState.CALM
            else -> MusicMoodState.FOCUSED
        }

        val quietMessage = when (moodState) {
            MusicMoodState.CALM -> "Keeping the flow calm"
            MusicMoodState.ENERGETIC -> "High energy flow active"
            MusicMoodState.UPBEAT -> "Upbeat session flow"
            MusicMoodState.DREAMY -> "Late night atmospheric flow"
            MusicMoodState.ROMANTIC -> "Melodic romantic flow"
            MusicMoodState.MELANCHOLIC -> "Reflective acoustic flow"
            MusicMoodState.FOCUSED -> "Balanced focus flow"
            MusicMoodState.NOSTALGIC -> "Nostalgic hits flow"
            MusicMoodState.DARK -> "Deep dark flow"
            MusicMoodState.PARTY -> "Party rhythm flow"
            MusicMoodState.DISCOVERY -> "Exploring fresh sounds"
        }

        return InferredMusicState(
            moodState = moodState,
            targetEnergy = recentAvgEnergy,
            targetValence = recentAvgValence,
            quietUserMessage = quietMessage
        )
    }
}
