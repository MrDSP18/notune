package com.music.echo.notune.theme

import androidx.compose.runtime.Immutable
import echo.music.iad1tya.constants.UiContext

@Immutable
data class WidgetAction(
    val id: String,
    val title: String,
    val iconResName: String,
    val isPrimary: Boolean = false
)

object SmartContextWidgetEngine {

    /**
     * Prioritizes available widget actions depending on the active UiContext.
     */
    fun getPrioritizedActions(context: UiContext): List<WidgetAction> {
        return when (context) {
            UiContext.NORMAL -> listOf(
                WidgetAction("play_pause", "Play/Pause", "play", isPrimary = true),
                WidgetAction("next", "Next Track", "skip_next"),
                WidgetAction("like", "Favorite", "favorite"),
                WidgetAction("queue", "Queue", "queue_music")
            )
            UiContext.FOCUS -> listOf(
                WidgetAction("play_pause", "Play/Pause", "play", isPrimary = true),
                WidgetAction("calm_mode", "Low Energy Vibe", "bedtime"),
                WidgetAction("timer", "Sleep Timer", "timer")
            )
            UiContext.WORKOUT -> listOf(
                WidgetAction("high_energy", "High Energy FLOW", "trending_up", isPrimary = true),
                WidgetAction("next", "Next Track", "skip_next"),
                WidgetAction("like", "Favorite", "favorite")
            )
            UiContext.RELAX -> listOf(
                WidgetAction("ambient_flow", "Ambient Chill", "water_drop", isPrimary = true),
                WidgetAction("lyrics", "Lyrics", "lyrics"),
                WidgetAction("translate", "Translate", "translate")
            )
            UiContext.DRIVING -> listOf(
                WidgetAction("play_pause", "Play/Pause", "play", isPrimary = true),
                WidgetAction("next", "Next Track", "skip_next"),
                WidgetAction("voice_ai", "Voice Assistant", "mic")
            )
            UiContext.PARTY -> listOf(
                WidgetAction("room_queue", "Room Queue", "group", isPrimary = true),
                WidgetAction("vote", "Upvote Track", "thumb_up_like"),
                WidgetAction("dj_surprise", "AI DJ Surprise", "sparks")
            )
            UiContext.SLEEP -> listOf(
                WidgetAction("sleep_timer", "Set 30m Timer", "sleep_timer", isPrimary = true),
                WidgetAction("fade_out", "Fade Audio", "volume_down"),
                WidgetAction("pause", "Pause", "pause")
            )
            UiContext.DISCOVERY -> listOf(
                WidgetAction("flow_surprise", "NØTUNE FLOW", "discover_tune", isPrimary = true),
                WidgetAction("more_like_this", "More Like This", "similar"),
                WidgetAction("dna", "Music DNA", "biotech")
            )
        }
    }
}
