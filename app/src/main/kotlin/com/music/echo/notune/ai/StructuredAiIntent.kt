package com.music.echo.notune.ai

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Serializable
enum class IntentActionType {
    PLAY,
    PAUSE,
    NEXT,
    PREVIOUS,
    ADJUST_ENERGY,
    SET_MOOD,
    FILTER_LANGUAGE,
    DISCOVER_NEW,
    UNKNOWN
}

@Immutable
@Serializable
data class StructuredAiIntent(
    val actionType: IntentActionType,
    val targetEnergy: Float? = null,
    val targetMood: String? = null,
    val targetLanguage: String? = null,
    val rawPrompt: String = "",
    val isValid: Boolean = true
) {
    companion object {
        fun parse(prompt: String): StructuredAiIntent {
            val lower = prompt.lowercase()
            return when {
                "pause" in lower -> StructuredAiIntent(IntentActionType.PAUSE, rawPrompt = prompt)
                "next" in lower || "skip" in lower -> StructuredAiIntent(IntentActionType.NEXT, rawPrompt = prompt)
                "previous" in lower || "back" in lower -> StructuredAiIntent(IntentActionType.PREVIOUS, rawPrompt = prompt)
                "calm" in lower || "relax" in lower -> StructuredAiIntent(IntentActionType.SET_MOOD, targetEnergy = 0.3f, targetMood = "Calm", rawPrompt = prompt)
                "energetic" in lower || "party" in lower -> StructuredAiIntent(IntentActionType.SET_MOOD, targetEnergy = 0.85f, targetMood = "Energetic", rawPrompt = prompt)
                "tamil" in lower -> StructuredAiIntent(IntentActionType.FILTER_LANGUAGE, targetLanguage = "Tamil", rawPrompt = prompt)
                "hindi" in lower -> StructuredAiIntent(IntentActionType.FILTER_LANGUAGE, targetLanguage = "Hindi", rawPrompt = prompt)
                "telugu" in lower -> StructuredAiIntent(IntentActionType.FILTER_LANGUAGE, targetLanguage = "Telugu", rawPrompt = prompt)
                "surprise" in lower || "discover" in lower -> StructuredAiIntent(IntentActionType.DISCOVER_NEW, rawPrompt = prompt)
                else -> StructuredAiIntent(IntentActionType.PLAY, rawPrompt = prompt)
            }
        }
    }
}
