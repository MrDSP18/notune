package com.music.echo.notune.onboarding

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
enum class TourStepId(val title: String, val category: String) {
    HOME("Your Music Hub", "Navigation"),
    SEARCH("Search Anything", "Discovery"),
    LIBRARY("Your Library", "Collection"),
    PLAYER("Audio & Player Controls", "Playback"),
    FLOW("NØTUNE FLOW Engine", "Recommendations"),
    AI("NØTUNE AI Assistant", "Intelligence"),
    LYRICS("Multilingual Sing-Along", "Lyrics"),
    ROOMS("Listen Together Rooms", "Social"),
    GESTURES("Gesture Controls", "Interaction"),
    MUSIC_DNA("Your Music DNA", "Analytics"),
    REPLAY("NØTUNE Replay", "History"),
    DOWNLOADS("Offline Downloads", "Storage"),
    CUSTOMIZATION("Themes & Customization", "Appearance"),
    SETTINGS("Settings Overview", "Control"),
    PRIVACY("Privacy & Local Control", "Security")
}

@Immutable
@Serializable
data class TourStep(
    val id: TourStepId,
    val headline: String,
    val description: String,
    val actionHint: String = "Tap Next or Skip Tour to enter Home immediately",
    val featureRoute: String = "home"
)

object TourStepsCatalog {
    val ALL_STEPS: List<TourStep> = listOf(
        TourStep(
            id = TourStepId.HOME,
            headline = "Your music starts here",
            description = "Continue listening, discover something new, see your personalized recommendations, and jump directly into NØTUNE FLOW.",
            featureRoute = "home"
        ),
        TourStep(
            id = TourStepId.SEARCH,
            headline = "Search naturally across all sources",
            description = "Find songs, artists, albums, playlists, and supported music sources with real-time filters and search history.",
            featureRoute = "search"
        ),
        TourStep(
            id = TourStepId.LIBRARY,
            headline = "Your music, organized your way",
            description = "Access your songs, albums, artists, playlists, downloads, folders, and recently played tracks in one place.",
            featureRoute = "library"
        ),
        TourStep(
            id = TourStepId.PLAYER,
            headline = "Full-featured playback & gesture control",
            description = "Control play/pause, queue, shuffle, repeat, favorite, lyrics, AI intent, and customize playback gestures.",
            featureRoute = "player"
        ),
        TourStep(
            id = TourStepId.FLOW,
            headline = "Meet NØTUNE FLOW",
            description = "Your music doesn't have to be a fixed playlist. FLOW understands your current vibe and dynamically generates what comes next.",
            featureRoute = "flow"
        ),
        TourStep(
            id = TourStepId.AI,
            headline = "Meet NØTUNE AI",
            description = "Ask NØTUNE AI to adjust mood, create playlists, or DJ your queue. Works zero-config with NØTUNE Basic AI out of the box.",
            featureRoute = "ask_notune"
        ),
        TourStep(
            id = TourStepId.LYRICS,
            headline = "Sing along in your language",
            description = "View synchronized lyrics with Original, Translation, and Sing-Along phonetic transliteration across 15 Indian languages.",
            featureRoute = "lyrics"
        ),
        TourStep(
            id = TourStepId.ROOMS,
            headline = "Music is better together",
            description = "Join Public or Private Rooms, build shared queues, vote on upcoming tracks, and listen synchronously with friends.",
            featureRoute = "rooms"
        ),
        TourStep(
            id = TourStepId.GESTURES,
            headline = "Control NØTUNE without touching everything",
            description = "Swipe up for next, swipe down for previous, double tap to pause, or long press for quick actions.",
            featureRoute = "settings/gestures"
        ),
        TourStep(
            id = TourStepId.MUSIC_DNA,
            headline = "Your music personality",
            description = "Inspect your language affinities, genre breakdowns, listening eras, and recommendation signals transparently.",
            featureRoute = "taste_profile"
        ),
        TourStep(
            id = TourStepId.REPLAY,
            headline = "Your year in music",
            description = "Review your top songs, artists, genres, and listening time for This Week, This Month, This Year, or All Time.",
            featureRoute = "replay"
        ),
        TourStep(
            id = TourStepId.DOWNLOADS,
            headline = "Take your music with you",
            description = "Download tracks for offline playback, manage storage, and configure Wi-Fi-only downloading.",
            featureRoute = "library/downloads"
        ),
        TourStep(
            id = TourStepId.CUSTOMIZATION,
            headline = "Make NØTUNE yours",
            description = "Choose from dynamic themes, accent colors, custom fonts, player card styles, and visual animations.",
            featureRoute = "settings/appearance"
        ),
        TourStep(
            id = TourStepId.SETTINGS,
            headline = "Everything you need is here",
            description = "Manage playback, privacy, AI, social rooms, gestures, notifications, and accessibility settings.",
            featureRoute = "settings"
        ),
        TourStep(
            id = TourStepId.PRIVACY,
            headline = "Your music belongs to you",
            description = "Local-first architecture. Enable Private Session, clear listening history, control cloud AI sharing, or export your data anytime.",
            featureRoute = "settings/privacy"
        )
    )
}
