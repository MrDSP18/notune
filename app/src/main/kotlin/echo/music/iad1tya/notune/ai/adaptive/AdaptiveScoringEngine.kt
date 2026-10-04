package echo.music.iad1tya.notune.ai.adaptive

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Deterministic scoring engine for calculating candidate track suitability.
 */
class AdaptiveScoringEngine(
    val config: AdaptiveScoringConfig = AdaptiveScoringConfig()
) {

    /**
     * Score a single candidate track against the current recommendation request.
     */
    fun scoreCandidate(
        candidate: AdaptiveTrackContext,
        request: RecommendationRequest,
        recentArtistCounts: Map<String, Int> = emptyMap(),
        recentGenreCounts: Map<String, Int> = emptyMap()
    ): ScoredTrack {
        val current = request.currentTrack

        // 1. Acoustic / Metadata Similarity
        val similarity = calculateSimilarity(candidate, current)

        // 2. Valence / Mood Fit
        val moodFit = calculateValenceFit(candidate, current, request.mood)

        // 3. Energy Fit
        val energyFit = calculateEnergyFit(candidate, current, request.energy)

        // 4. Tempo / BPM Fit
        val tempoFit = calculateTempoFit(candidate, current, request.tempoBpm)

        // 5. User Preference Signal (Likes / Dislikes)
        val userPreference = calculateUserPreference(candidate, request)

        // 6. Replay Preference Signal
        val replayPreference = if (request.replayedTracks.contains(candidate.trackId)) config.replayBoost else 0.0f

        // 7. Skip Penalty
        val skipPenalty = if (request.skippedTracks.contains(candidate.trackId)) abs(config.skipPenalty) else 0.0f

        // 8. Artist Fatigue
        val artistCount = recentArtistCounts[candidate.artist.lowercase().trim()] ?: 0
        val artistFatigue = if (artistCount >= config.artistRepetitionLimit) abs(config.artistFatiguePenalty) else 0.0f

        // 9. Genre Fatigue
        val genreKey = candidate.genre?.lowercase()?.trim() ?: ""
        val genreCount = if (genreKey.isNotEmpty()) recentGenreCounts[genreKey] ?: 0 else 0
        val genreFatigue = if (genreCount >= 3) abs(config.genreFatiguePenalty) else 0.0f

        // 10. Repetition Penalty (Recently played track)
        val recentlyPlayed = request.history.any { it.trackId == candidate.trackId }
        val repetitionPenalty = if (recentlyPlayed) abs(config.recentTrackRepetitionPenalty) else 0.0f

        // Combine into raw score according to formula:
        // score = similarity + moodFit + energyFit + tempoFit + userPreference + replayPreference - skipPenalty - artistFatigue - genreFatigue - repetitionPenalty
        val rawScore = (similarity * config.similarityWeight) +
                (moodFit * config.valenceMatchWeight) +
                (energyFit * config.energyMatchWeight) +
                (tempoFit * config.tempoMatchWeight) +
                (userPreference * config.userPreferenceWeight) +
                replayPreference -
                skipPenalty -
                artistFatigue -
                genreFatigue -
                repetitionPenalty

        val finalScore = max(0.0f, min(1.0f, rawScore))
        val reason = generateExplainabilityReason(candidate, current, request, moodFit, energyFit, userPreference, replayPreference, skipPenalty)

        return ScoredTrack(
            track = candidate,
            score = finalScore,
            reason = reason,
            similarity = similarity
        )
    }

    /**
     * Compute acoustic or metadata similarity score between two tracks (0.0 to 1.0).
     */
    fun calculateSimilarity(target: AdaptiveTrackContext, reference: AdaptiveTrackContext?): Float {
        if (reference == null) return 0.5f // Default baseline

        var matches = 0.0f
        var totalWeight = 0.0f

        // Artist similarity
        if (target.artist.equals(reference.artist, ignoreCase = true)) {
            matches += 0.4f
        }
        totalWeight += 0.4f

        // Genre similarity
        if (!target.genre.isNullOrBlank() && !reference.genre.isNullOrBlank()) {
            if (target.genre.equals(reference.genre, ignoreCase = true)) {
                matches += 0.3f
            }
            totalWeight += 0.3f
        }

        // Language similarity
        if (!target.language.isNullOrBlank() && !reference.language.isNullOrBlank()) {
            if (target.language.equals(reference.language, ignoreCase = true)) {
                matches += 0.2f
            }
            totalWeight += 0.2f
        }

        // Acoustic feature similarity (Valence & Energy)
        if (target.valence != null && reference.valence != null && target.energy != null && reference.energy != null) {
            val deltaV = abs(target.valence - reference.valence)
            val deltaE = abs(target.energy - reference.energy)
            val acousticSim = max(0.0f, 1.0f - ((deltaV + deltaE) / 2.0f))
            matches += acousticSim * 0.1f
            totalWeight += 0.1f
        }

        return if (totalWeight > 0f) matches / totalWeight else 0.5f
    }

    private fun calculateValenceFit(candidate: AdaptiveTrackContext, current: AdaptiveTrackContext?, requestMood: String?): Float {
        if (candidate.valence != null && current?.valence != null) {
            val delta = abs(candidate.valence - current.valence)
            return max(0.0f, 1.0f - delta)
        }
        return 0.5f
    }

    private fun calculateEnergyFit(candidate: AdaptiveTrackContext, current: AdaptiveTrackContext?, requestEnergy: Float?): Float {
        val targetEnergy = requestEnergy ?: current?.energy ?: return 0.5f
        if (candidate.energy != null) {
            val delta = abs(candidate.energy - targetEnergy)
            return max(0.0f, 1.0f - delta)
        }
        return 0.5f
    }

    private fun calculateTempoFit(candidate: AdaptiveTrackContext, current: AdaptiveTrackContext?, targetBpm: Float?): Float {
        val referenceBpm = targetBpm ?: current?.bpm ?: return 0.5f
        if (candidate.bpm != null && candidate.bpm > 0 && referenceBpm > 0) {
            val delta = abs(candidate.bpm - referenceBpm) / referenceBpm
            // Max transition delta threshold
            return if (delta <= config.maxBpmTransitionDelta) {
                1.0f - (delta / config.maxBpmTransitionDelta)
            } else {
                max(0.0f, 0.5f - delta)
            }
        }
        return 0.5f
    }

    private fun calculateUserPreference(candidate: AdaptiveTrackContext, request: RecommendationRequest): Float {
        var score = 0.5f
        if (request.likedTrackIds.contains(candidate.trackId)) {
            score += config.likeBoost
        }
        if (request.dislikedTrackIds.contains(candidate.trackId)) {
            score += config.dislikePenalty
        }
        return max(0.0f, min(1.0f, score))
    }

    private fun generateExplainabilityReason(
        candidate: AdaptiveTrackContext,
        current: AdaptiveTrackContext?,
        request: RecommendationRequest,
        moodFit: Float,
        energyFit: Float,
        userPreference: Float,
        replayPreference: Float,
        skipPenalty: Float
    ): String {
        return when {
            userPreference > 0.7f -> "Based on your liked tracks"
            replayPreference > 0.0f -> "Because you replayed this track recently"
            skipPenalty > 0.0f -> "Low confidence due to recent skip"
            current != null && candidate.artist.equals(current.artist, ignoreCase = true) -> "More from ${candidate.artist}"
            energyFit > 0.85f -> "Matches your current listening energy"
            moodFit > 0.85f -> "Keeping the same acoustic mood"
            !candidate.genre.isNullOrBlank() && current?.genre.equals(candidate.genre, ignoreCase = true) -> "Similar ${candidate.genre} track"
            else -> "Recommended for your session context"
        }
    }
}
