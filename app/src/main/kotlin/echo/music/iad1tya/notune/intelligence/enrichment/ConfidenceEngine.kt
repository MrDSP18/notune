package echo.music.iad1tya.notune.intelligence.enrichment

import echo.music.iad1tya.notune.intelligence.knowledge.SongDetails
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs
import kotlin.math.max

/**
 * Confidence Engine for evaluating identity matches and preventing false metadata attachment.
 * Enforces NO FABRICATION rules for uncertain internet matches.
 */
@Singleton
class ConfidenceEngine @Inject constructor() {

    /**
     * Evaluate identity match score between target identity and candidate SongDetails.
     */
    fun evaluateMatch(
        target: SongIdentity,
        candidate: SongDetails,
        providerId: String
    ): SongIdentityMatch {
        val matchingFields = mutableListOf<String>()
        var score = 0.0f

        // Title match (0.45 weight)
        val normalizer = SongIdentityNormalizer()
        val candidateIdentity = normalizer.normalize(candidate.title, candidate.artists.firstOrNull()?.name ?: "")
        val candidateTitleToUse = if (candidateIdentity.cleanTitle.isNotBlank()) candidateIdentity.cleanTitle else candidate.title

        val titleLev = calculateLevenshteinSimilarity(
            target.cleanTitle.lowercase(),
            candidateTitleToUse.lowercase()
        )
        val titleToken = calculateTokenSetSimilarity(
            target.cleanTitle.lowercase(),
            candidateTitleToUse.lowercase()
        )
        val titleSim = maxOf(titleLev, titleToken)
        score += titleSim * 0.45f
        if (titleSim >= 0.80f) matchingFields.add("title")

        // Artist match (0.35 weight)
        val candidateArtist = candidate.artists.firstOrNull()?.name ?: ""
        val artistLev = calculateLevenshteinSimilarity(
            target.cleanArtist.lowercase(),
            candidateArtist.lowercase()
        )
        val artistToken = calculateTokenSetSimilarity(
            target.cleanArtist.lowercase(),
            candidateArtist.lowercase()
        )
        val artistSim = maxOf(artistLev, artistToken)
        score += artistSim * 0.35f
        if (artistSim >= 0.75f) matchingFields.add("artist")

        // Album / Movie match (0.10 weight)
        if (target.cleanAlbum != null && candidate.albumTitle != null) {
            val albumSim = calculateLevenshteinSimilarity(
                target.cleanAlbum.lowercase(),
                candidate.albumTitle.lowercase()
            )
            score += albumSim * 0.10f
            if (albumSim >= 0.75f) matchingFields.add("album")
        } else if (target.cleanMovie != null && candidate.movieTitle != null) {
            val movieSim = calculateLevenshteinSimilarity(
                target.cleanMovie.lowercase(),
                candidate.movieTitle.lowercase()
            )
            score += movieSim * 0.10f
            if (movieSim >= 0.75f) matchingFields.add("movie")
        }

        // Duration delta match (0.10 weight)
        if (target.durationMs > 0 && candidate.durationMs > 0) {
            val deltaMs = abs(target.durationMs - candidate.durationMs)
            if (deltaMs < 5000) { // within 5 seconds
                score += 0.10f
                matchingFields.add("duration")
            } else if (deltaMs < 15000) {
                score += 0.05f
            }
        }

        val confidenceLevel = when {
            score >= 0.92f -> MatchConfidence.EXACT
            score >= 0.78f -> MatchConfidence.HIGH
            score >= 0.58f -> MatchConfidence.MEDIUM
            score >= 0.35f -> MatchConfidence.LOW
            else -> MatchConfidence.UNKNOWN
        }

        return SongIdentityMatch(
            identity = target,
            matchConfidence = confidenceLevel,
            confidenceScore = score,
            matchingFields = matchingFields,
            providerId = providerId,
            source = candidate.source
        )
    }

    /**
     * Check if identity match confidence is sufficiently high for attaching movie/person details.
     * Prevents low-confidence matches from corrupting the Knowledge Graph.
     */
    fun isMatchValidForAttachment(match: SongIdentityMatch): Boolean {
        return match.matchConfidence == MatchConfidence.EXACT ||
               match.matchConfidence == MatchConfidence.HIGH ||
               match.matchConfidence == MatchConfidence.MEDIUM
    }

    private fun calculateLevenshteinSimilarity(s1: String, s2: String): Float {
        if (s1 == s2) return 1.0f
        if (s1.isBlank() || s2.isBlank()) return 0.0f

        val len1 = s1.length
        val len2 = s2.length
        val distance = Array(len1 + 1) { IntArray(len2 + 1) }

        for (i in 0..len1) distance[i][0] = i
        for (j in 0..len2) distance[0][j] = j

        for (i in 1..len1) {
            for (j in 1..len2) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                distance[i][j] = minOf(
                    distance[i - 1][j] + 1,
                    distance[i][j - 1] + 1,
                    distance[i - 1][j - 1] + cost
                )
            }
        }

        val maxLen = max(len1, len2)
        return (1.0f - distance[len1][len2].toFloat() / maxLen).coerceIn(0.0f, 1.0f)
    }

    private fun calculateTokenSetSimilarity(s1: String, s2: String): Float {
        val tokens1 = s1.lowercase().split(Regex("[^\\p{L}\\p{N}]+")).filter { it.isNotBlank() }.toSet()
        val tokens2 = s2.lowercase().split(Regex("[^\\p{L}\\p{N}]+")).filter { it.isNotBlank() }.toSet()
        if (tokens1.isEmpty() || tokens2.isEmpty()) return 0.0f
        val intersection = tokens1.intersect(tokens2).size.toFloat()
        val union = tokens1.union(tokens2).size.toFloat()
        return if (union > 0f) (intersection / union).coerceIn(0.0f, 1.0f) else 0.0f
    }
}
