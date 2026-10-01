package com.music.echo.notune.intelligence.recommendation

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiversityController @Inject constructor() {

    fun applyDiversityFilter(
        rankedCandidates: List<RankedTrackResult>,
        maxPerArtist: Int = 2,
        discoveryTolerance: Float = 0.5f
    ): List<RankedTrackResult> {
        val result = mutableListOf<RankedTrackResult>()
        val artistCounts = mutableMapOf<String, Int>()

        val effectiveMaxPerArtist = if (discoveryTolerance > 0.7f) 1 else maxPerArtist

        for (candidate in rankedCandidates) {
            val primaryArtist = candidate.track.artist.split(",").firstOrNull()?.trim()?.lowercase() ?: "unknown"
            val count = artistCounts.getOrDefault(primaryArtist, 0)

            if (count < effectiveMaxPerArtist) {
                result.add(candidate)
                artistCounts[primaryArtist] = count + 1
            }
        }
        return result
    }
}
