package com.music.echo.notune.intelligence.queue

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepetitionController @Inject constructor() {

    fun calculateOverexposurePenalty(
        candidateArtist: String,
        candidateTrackId: String,
        recentPlayed: List<QueueTrack>,
        upcomingQueue: List<QueueTrack>
    ): Float {
        var penalty = 0f

        // 1. Same track recently played
        if (recentPlayed.takeLast(10).any { it.id == candidateTrackId }) {
            penalty += 0.50f
        }

        // 2. Same artist played within last 3 songs
        val recentArtists = recentPlayed.takeLast(3).map { it.artistName.lowercase() }
        if (recentArtists.contains(candidateArtist.lowercase())) {
            penalty += 0.25f
        }

        // 3. Same artist already upcoming in next 2 positions
        val upcomingArtists = upcomingQueue.take(2).map { it.artistName.lowercase() }
        if (upcomingArtists.contains(candidateArtist.lowercase())) {
            penalty += 0.20f
        }

        return penalty.coerceIn(0f, 1f)
    }
}
