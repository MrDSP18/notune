package com.music.echo.notune.intelligence.feedback

sealed class UserEvent {
    data class Play(val trackId: String, val title: String, val artistName: String, val timestampMs: Long = System.currentTimeMillis()) : UserEvent()
    data class Pause(val trackId: String, val positionMs: Long, val timestampMs: Long = System.currentTimeMillis()) : UserEvent()
    data class Resume(val trackId: String, val timestampMs: Long = System.currentTimeMillis()) : UserEvent()
    data class Skip(val trackId: String, val title: String, val artistName: String, val playedDurationSec: Float, val genre: String? = null, val timestampMs: Long = System.currentTimeMillis()) : UserEvent()
    data class Completed(val trackId: String, val title: String, val artistName: String, val genre: String? = null, val language: String? = null, val timestampMs: Long = System.currentTimeMillis()) : UserEvent()
    data class Replay(val trackId: String, val title: String, val artistName: String, val genre: String? = null, val timestampMs: Long = System.currentTimeMillis()) : UserEvent()
    data class Like(val trackId: String, val artistName: String, val timestampMs: Long = System.currentTimeMillis()) : UserEvent()
    data class Dislike(val trackId: String, val artistName: String, val timestampMs: Long = System.currentTimeMillis()) : UserEvent()
    data class Save(val trackId: String, val artistName: String, val timestampMs: Long = System.currentTimeMillis()) : UserEvent()
    data class Search(val query: String, val timestampMs: Long = System.currentTimeMillis()) : UserEvent()
    data class TeachRule(val rawDirective: String, val timestampMs: Long = System.currentTimeMillis()) : UserEvent()
    data class ToggleTasteExclusion(val excludeCurrentSession: Boolean, val timestampMs: Long = System.currentTimeMillis()) : UserEvent()
}
