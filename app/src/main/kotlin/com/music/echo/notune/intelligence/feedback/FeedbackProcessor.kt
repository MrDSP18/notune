package com.music.echo.notune.intelligence.feedback

import com.music.echo.notune.intelligence.personalization.PreferenceLearner
import com.music.echo.notune.intelligence.personalization.TasteProfileStore
import com.music.echo.notune.intelligence.personalization.TeachNotuneEngine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedbackProcessor @Inject constructor(
    private val rewardCalculator: RewardCalculator,
    private val preferenceLearner: PreferenceLearner,
    private val tasteProfileStore: TasteProfileStore,
    private val teachNotuneEngine: TeachNotuneEngine
) {

    fun processEvent(event: UserEvent): Float {
        val reward = rewardCalculator.calculateReward(event)

        when (event) {
            is UserEvent.Completed -> {
                preferenceLearner.onTrackCompleted(event.genre, event.artistName, event.language)
            }
            is UserEvent.Skip -> {
                preferenceLearner.onTrackSkippedEarly(event.genre, event.artistName, event.playedDurationSec)
            }
            is UserEvent.Replay -> {
                preferenceLearner.onTrackReplayed(event.genre, event.artistName)
            }
            is UserEvent.Dislike -> {
                tasteProfileStore.addNeverRecommendArtist(event.artistName)
            }
            is UserEvent.TeachRule -> {
                val rule = teachNotuneEngine.parseDirective(event.rawDirective)
                tasteProfileStore.addPreferenceRule(rule)
            }
            is UserEvent.ToggleTasteExclusion -> {
                tasteProfileStore.setSessionExcludedFromTaste(event.excludeCurrentSession)
            }
            else -> {
                // Other passive telemetry events recorded for analytics
            }
        }

        return reward
    }
}
