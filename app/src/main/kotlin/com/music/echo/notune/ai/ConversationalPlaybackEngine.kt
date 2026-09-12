package com.music.echo.notune.ai

import echo.music.iad1tya.notune.flow.FlowContextMode
import echo.music.iad1tya.notune.flow.FlowIntent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConversationalPlaybackEngine @Inject constructor() {

    fun parseConversationalRequest(userQuery: String): FlowIntent {
        val queryLower = userQuery.lowercase()

        val mood = when {
            queryLower.contains("calm") || queryLower.contains("relax") || queryLower.contains("sleepy") -> "calm"
            queryLower.contains("happy") || queryLower.contains("upbeat") -> "happy"
            queryLower.contains("energetic") || queryLower.contains("workout") -> "energetic"
            queryLower.contains("sad") -> "melancholic"
            else -> "balanced"
        }

        val energy = when {
            queryLower.contains("calm") -> 0.3f
            queryLower.contains("energetic") -> 0.9f
            else -> 0.5f
        }

        val discovery = when {
            queryLower.contains("never heard") || queryLower.contains("discover") -> 0.8f
            queryLower.contains("familiar") || queryLower.contains("favorite") -> 0.1f
            else -> 0.3f
        }

        val contextMode = when {
            queryLower.contains("workout") -> FlowContextMode.WORKOUT
            queryLower.contains("focus") || queryLower.contains("study") -> FlowContextMode.FOCUS
            queryLower.contains("driving") || queryLower.contains("car") -> FlowContextMode.DRIVING
            queryLower.contains("party") -> FlowContextMode.PARTY
            queryLower.contains("sleep") -> FlowContextMode.SLEEP
            else -> FlowContextMode.NORMAL
        }

        return FlowIntent(
            mood = mood,
            targetEnergy = energy,
            discoveryRatio = discovery,
            contextMode = contextMode,
            rawPrompt = userQuery
        )
    }
}
