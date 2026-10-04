package com.music.echo.notune.ai.music

data class TrackFeatures(
    val id: String,
    val title: String,
    val artist: String,
    val genre: String,
    val language: String,
    val energy: Float,     // 0.0 to 1.0
    val valence: Float,    // 0.0 to 1.0 (happiness/sadness)
    val tempoBpm: Float,
    val eraYear: Int,
    val isExplicit: Boolean = false
)

data class CandidateScoreResult(
    val track: TrackFeatures,
    val totalScore: Float,
    val tasteScore: Float,
    val similarityScore: Float,
    val contextScore: Float,
    val matchPercentage: Int
)

object MusicBrain {

    fun scoreCandidate(
        candidate: TrackFeatures,
        currentTrack: TrackFeatures?,
        userTasteLanguage: String = "Tamil",
        targetEnergy: Float = 0.7f,
        targetValence: Float = 0.6f
    ): CandidateScoreResult {
        // Taste score (25%)
        val tasteScore = if (candidate.language.equals(userTasteLanguage, ignoreCase = true)) 1.0f else 0.5f
        
        // Similarity score (15%)
        val similarityScore = if (currentTrack != null) {
            val energyDiff = kotlin.math.abs(candidate.energy - currentTrack.energy)
            val valenceDiff = kotlin.math.abs(candidate.valence - currentTrack.valence)
            (1.0f - (energyDiff * 0.5f + valenceDiff * 0.5f)).coerceIn(0.0f, 1.0f)
        } else 0.8f

        // Context / Energy match (30%)
        val energyMatch = 1.0f - kotlin.math.abs(candidate.energy - targetEnergy)
        val valenceMatch = 1.0f - kotlin.math.abs(candidate.valence - targetValence)
        val contextScore = (energyMatch * 0.5f + valenceMatch * 0.5f).coerceIn(0.0f, 1.0f)

        // Novelty & Discovery (30%)
        val noveltyScore = 0.85f

        // Weighted total formula
        val total = (tasteScore * 0.25f) + (similarityScore * 0.15f) + (contextScore * 0.30f) + (noveltyScore * 0.30f)
        val percentage = (total * 100).toInt().coerceIn(1, 99)

        return CandidateScoreResult(
            track = candidate,
            totalScore = total,
            tasteScore = tasteScore,
            similarityScore = similarityScore,
            contextScore = contextScore,
            matchPercentage = percentage
        )
    }
}
