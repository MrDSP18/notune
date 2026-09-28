package com.music.echo.notune.player

enum class PlayerMode {
    MINIMAL,   // Clean everyday player (artwork, title, artist, progress, main playback, favorite)
    STANDARD,  // Complete OS player (Smart Context Bar, intelligence header, details, quick actions)
    IMMERSIVE  // Fullscreen beat-reactive visualizer / Music DNA telemetry mode
}

data class SmartContextInfo(
    val flowModeName: String = "SMART FLOW",
    val confidencePercentage: Int = 87,
    val contextTag: String = "YOUR FLOW",
    val matchDescription: String = "87% TASTE MATCH",
    val transitionQuality: String = "SMOOTH TRANSITION"
)

data class SongIntelligenceInfo(
    val primaryReason: String = "Based on your frequent replays of this artist",
    val secondaryReasons: List<String> = listOf(
        "Matches current Flow energy and tempo profile",
        "Top played genre in your Music DNA this week"
    ),
    val energyLevel: Float = 0.75f,
    val tempoBpm: Int = 124,
    val genre: String = "Pop / Melodic",
    val language: String = "Multilingual",
    val acousticness: Float = 0.35f
)
