package com.music.echo.notune.intelligence.search

import com.music.echo.notune.intelligence.musicbrain.TrackEmbedding
import com.music.echo.notune.intelligence.personalization.NotuneUserDNA
import javax.inject.Inject
import javax.inject.Singleton

enum class GateResultStatus {
    PASS,
    REJECT_UNPLAYABLE,
    REJECT_MISSING_METADATA,
    REJECT_DUPLICATE,
    REJECT_OVERPLAYED,
    REJECT_LOW_TASTE_MATCH,
    REJECT_EXCLUDED_ARTIST
}

data class QualityGateDecision(
    val status: GateResultStatus,
    val candidateId: String,
    val isPassed: Boolean,
    val confidenceScore: Float,
    val rejectionReason: String? = null
)

@Singleton
class SearchQualityGate @Inject constructor() {

    fun evaluateCandidate(
        candidate: SearchCandidate,
        userDna: NotuneUserDNA,
        recentPlayedIds: List<String> = emptyList()
    ): QualityGateDecision {
        val emb = candidate.embedding

        // 1. Playability check
        if (!candidate.isAvailableOfflineOrStream) {
            return QualityGateDecision(GateResultStatus.REJECT_UNPLAYABLE, candidate.id, false, 0f, "Track is unavailable for stream or offline playback")
        }

        // 2. Metadata check
        if (candidate.title.isBlank() || candidate.artistName.isBlank()) {
            return QualityGateDecision(GateResultStatus.REJECT_MISSING_METADATA, candidate.id, false, 0f, "Missing valid title or artist metadata")
        }

        // 3. Excluded listening check
        val isExplicitNever = userDna.explicitPreferences.neverRecommendArtists.any { it.equals(candidate.artistName, ignoreCase = true) } ||
                userDna.explicitPreferences.neverRecommendSongs.contains(candidate.id)
        if (isExplicitNever) {
            return QualityGateDecision(GateResultStatus.REJECT_EXCLUDED_ARTIST, candidate.id, false, 0f, "Artist or track is explicitly marked 'Never Recommend'")
        }

        // 4. Overplayed check
        if (recentPlayedIds.takeLast(10).contains(candidate.id)) {
            return QualityGateDecision(GateResultStatus.REJECT_OVERPLAYED, candidate.id, false, 0.2f, "Track played recently in last 10 songs")
        }

        // 5. Pass gate
        return QualityGateDecision(
            status = GateResultStatus.PASS,
            candidateId = candidate.id,
            isPassed = true,
            confidenceScore = 0.92f
        )
    }
}
