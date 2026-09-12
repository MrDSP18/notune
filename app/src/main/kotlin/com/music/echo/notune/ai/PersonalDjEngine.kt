package com.music.echo.notune.ai

import echo.music.iad1tya.notune.flow.FlowEngine
import javax.inject.Inject
import javax.inject.Singleton

data class DjSessionState(
    val isDjActive: Boolean = false,
    val currentVibe: String = "Relaxed Start",
    val introSpeech: String = "NØTUNE DJ taking over. We're starting with a relaxed vibe."
)

@Singleton
class PersonalDjEngine @Inject constructor(
    private val flowEngine: FlowEngine
) {
    private var sessionState = DjSessionState()

    fun takeOver(vibe: String = "Relaxed Start"): DjSessionState {
        sessionState = DjSessionState(
            isDjActive = true,
            currentVibe = vibe,
            introSpeech = "NØTUNE DJ taking over. Setting current vibe to $vibe."
        )
        flowEngine.updateConfig(enabled = true)
        return sessionState
    }

    fun releaseControl(): DjSessionState {
        sessionState = DjSessionState(isDjActive = false)
        return sessionState
    }

    fun getDjState(): DjSessionState = sessionState
}
