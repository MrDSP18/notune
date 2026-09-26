package com.music.echo.notune.intelligence.queue

import com.music.echo.notune.intelligence.musicbrain.TrackEmbedding

data class QueueTrack(
    val id: String,
    val title: String,
    val artistName: String,
    val embedding: TrackEmbedding,
    val isLockedByUser: Boolean = false,
    val scoreDetails: TrackScoreDetails = TrackScoreDetails()
)

data class TrackScoreDetails(
    val totalScore: Float = 0.5f,
    val tasteMatch: Float = 0.5f,
    val currentMoodMatch: Float = 0.5f,
    val transitionQuality: Float = 0.8f,
    val repetitionPenalty: Float = 0.0f,
    val skipRisk: Float = 0.1f,
    val explanation: String = ""
)

data class TransitionScore(
    val fromTrackTitle: String,
    val toTrackTitle: String,
    val transitionQuality: Float, // 0.0 to 1.0 (higher = seamless flow)
    val energyDelta: Float,
    val bpmRatio: Float,
    val flowLabel: String
)

data class QueueState(
    val currentlyPlaying: QueueTrack? = null,
    val upcomingQueue: List<QueueTrack> = emptyList(),
    val playedHistory: List<QueueTrack> = emptyList(),
    val overallFlowScore: Float = 0.85f,
    val flowMode: NotuneFlowMode = NotuneFlowMode.SMART,
    val isAutoAdaptiveEnabled: Boolean = true
)
