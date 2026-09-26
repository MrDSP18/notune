package com.music.echo.notune.intelligence.feedback

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RewardCalculator @Inject constructor() {

    fun calculateReward(event: UserEvent): Float {
        return when (event) {
            is UserEvent.Replay -> +2.5f
            is UserEvent.Save -> +2.0f
            is UserEvent.Like -> +1.8f
            is UserEvent.Completed -> +1.0f
            is UserEvent.Search -> +0.8f
            is UserEvent.Play -> +0.3f
            is UserEvent.Resume -> +0.2f
            is UserEvent.Pause -> 0.0f
            is UserEvent.Skip -> {
                if (event.playedDurationSec < 10f) -1.8f
                else if (event.playedDurationSec < 30f) -1.0f
                else -0.3f
            }
            is UserEvent.Dislike -> -2.5f
            is UserEvent.TeachRule -> 0.0f
            is UserEvent.ToggleTasteExclusion -> 0.0f
        }
    }
}
