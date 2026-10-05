package com.music.echo.notune.intelligence.search

import com.music.echo.notune.intelligence.musicbrain.TrackEmbedding
import com.music.echo.notune.intelligence.musicbrain.TrackScoreResult

data class SearchIntent(
    val rawQuery: String,
    val targetMood: String? = null,
    val targetGenre: String? = null,
    val targetLanguage: String? = null,
    val targetEra: String? = null,
    val targetArtist: String? = null,
    val minEnergy: Float? = null,
    val maxEnergy: Float? = null,
    val isUnderratedRequest: Boolean = false,
    val isDiscoveryRequest: Boolean = false,
    val intentDescription: String = "General Music Search"
)

data class SearchCandidate(
    val id: String,
    val title: String,
    val artistName: String,
    val albumName: String? = null,
    val embedding: TrackEmbedding,
    val isAvailableOfflineOrStream: Boolean = true
)

data class RankedSearchResult(
    val candidate: SearchCandidate,
    val scoreResult: TrackScoreResult,
    val explanation: String
)
