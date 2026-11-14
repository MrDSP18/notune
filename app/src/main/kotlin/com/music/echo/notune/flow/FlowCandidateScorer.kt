package echo.music.iad1tya.notune.flow

import echo.music.iad1tya.models.MediaMetadata
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlowCandidateScorer @Inject constructor() {
    private val genreAliases = mapOf(
        "tamil" to listOf("tamil", "kollywood", "ar rahman", "anirudh", "yuvan", "harris jayaraj", "sid sriram"),
        "hip-hop" to listOf("hip hop", "hip-hop", "rap", "trap", "drill", "kendrick", "drake", "eminem"),
        "pop" to listOf("pop", "ariana", "taylor swift", "weeknd", "dua lipa", "billie eilish"),
        "rock" to listOf("rock", "metal", "punk", "linkin park", "nirvana", "foo fighters", "arctic monkeys"),
        "electronic" to listOf("edm", "electronic", "techno", "house", "trance", "dubstep", "avicii", "daft punk"),
        "bollywood" to listOf("bollywood", "hindi", "arijit singh", "pritam", "atif aslam"),
        "classical" to listOf("classical", "symphony", "orchestra", "beethoven", "mozart", "bach", "chopin"),
        "jazz" to listOf("jazz", "bebop", "swing", "coltrane", "miles davis"),
        "ambient" to listOf("ambient", "chill", "lofi", "lo-fi", "sleep", "meditation", "brian eno")
    )

    fun scoreCandidates(
        candidates: List<FlowCandidate>,
        currentTrack: MediaMetadata?,
        mode: FlowMode,
        contextMode: FlowContextMode,
        discoveryRatio: Float,
        sessionProfile: LanguageSessionProfile = LanguageSessionProfile(),
        topArtistIds: List<String> = emptyList(),
        favoriteTrackIds: Set<String> = emptySet(),
        recentlySkippedTrackIds: Map<String, Long> = emptyMap(),
        recommendationHistory: Map<String, RecommendationHistoryRecord> = emptyMap(),
        preferredGenres: List<String> = emptyList()
    ): List<FlowScore> {
        val currentArtistId = currentTrack?.artists?.firstOrNull()?.id
        val discovery = discoveryRatio.coerceIn(0f, 1f)

        return candidates.map { candidate ->
            val metadata = candidate.mediaMetadata
            val artistId = metadata.artists.firstOrNull()?.id
            val searchable = buildString {
                append(metadata.title.lowercase(Locale.ROOT))
                append(' ')
                metadata.artists.forEach { append(it.name.lowercase(Locale.ROOT)).append(' ') }
                append(metadata.album?.title?.lowercase(Locale.ROOT).orEmpty())
            }

            var artistScore = 0f
            var genreScore = 0f
            var languageScore = 0f
            var tasteScore = 0f
            var discoveryBonus = 0f
            var energyScore = 0f
            var skipPenalty = 0f
            var recPenalty = 0f

            val candidateLang = candidate.language ?: "UNKNOWN"
            if (sessionProfile.primaryLanguage != "UNKNOWN" && candidateLang != "UNKNOWN") {
                if (candidateLang.equals(sessionProfile.primaryLanguage, ignoreCase = true)) {
                    languageScore += 25f * sessionProfile.confidence.coerceIn(0.6f, 1f)
                } else if (sessionProfile.secondaryLanguage != null &&
                    candidateLang.equals(sessionProfile.secondaryLanguage, ignoreCase = true)
                ) {
                    languageScore += 12f * sessionProfile.confidence.coerceIn(0f, 1f)
                } else if (sessionProfile.confidence > 0.6f) {
                    languageScore -= 18f
                }
            }

            if (artistId != null && artistId == currentArtistId) artistScore += 22f
            if (artistId != null && artistId in topArtistIds) artistScore += 16f
            if (favoriteTrackIds.contains(metadata.id) || metadata.liked) tasteScore += 18f

            when (candidate.candidateSource) {
                "Related Track" -> artistScore += 15f
                "Same Artist" -> artistScore += 12f
                "Favorite Track" -> tasteScore += 15f
                "Listening History" -> tasteScore += 10f
                "Favorite Artist" -> artistScore += 12f
                "Discovery", "Library Fallback" -> discoveryBonus += 5f
            }

            val genreMatchIndex = preferredGenres.indexOfFirst { genre ->
                val key = genre.lowercase(Locale.ROOT)
                val aliases = genreAliases.entries.firstOrNull { (name, _) -> name in key }?.value
                    ?: key.split(Regex("[^\\p{L}\\p{N}]+")).filter { it.length > 2 }
                aliases.any { searchable.contains(it, ignoreCase = true) }
            }
            if (genreMatchIndex >= 0) genreScore += (20f - genreMatchIndex * 3f).coerceAtLeast(5f)

            val contextKeywords = when (contextMode) {
                FlowContextMode.WORKOUT, FlowContextMode.PARTY ->
                    listOf("workout", "gym", "run", "dance", "party", "upbeat", "edm", "rock", "hip hop", "energetic")
                FlowContextMode.FOCUS, FlowContextMode.RELAX, FlowContextMode.SLEEP ->
                    listOf("chill", "ambient", "instrumental", "piano", "lofi", "lo-fi", "acoustic", "meditation", "classical")
                FlowContextMode.DRIVING ->
                    listOf("rock", "road", "drive", "synthwave", "electronic", "upbeat")
                FlowContextMode.DISCOVERY ->
                    listOf("indie", "alternative", "experimental", "world", "folk")
                else -> emptyList()
            }
            if (contextKeywords.any { searchable.contains(it) }) energyScore += 10f

            when (mode) {
                FlowMode.FAVORITES_FLOW -> tasteScore *= 1.5f
                FlowMode.DISCOVERY_FLOW -> discoveryBonus += discovery * 30f
                FlowMode.ARTIST_RADIO -> if (artistId == currentArtistId) artistScore *= 1.8f
                FlowMode.SONG_RADIO -> if (artistId == currentArtistId) artistScore += 8f
                FlowMode.MOOD_FLOW, FlowMode.AI_FLOW -> energyScore *= 1.25f
                FlowMode.COUPLE_FLOW, FlowMode.ROOM_FLOW -> {
                    genreScore *= 0.8f
                    discoveryBonus += discovery * 8f
                }
                FlowMode.AUTO_FLOW -> discoveryBonus += discovery * 15f
            }

            if (recentlySkippedTrackIds.containsKey(metadata.id)) skipPenalty += 25f
            recommendationHistory[metadata.id]?.let { record ->
                if (record.skippedCount > 0) skipPenalty += (record.skippedCount * 8f).coerceAtMost(24f)
                if (record.playedCount == 0 && record.timesRecommended > 1) {
                    recPenalty += ((record.timesRecommended - 1) * 6f).coerceAtMost(24f)
                }
            }

            val totalScore = (
                artistScore + genreScore + languageScore + tasteScore +
                    discoveryBonus + energyScore - skipPenalty - recPenalty
                ).coerceAtLeast(0f)

            val reason = when {
                languageScore > 15f -> FlowReason(FlowReasonType.LANGUAGE_MATCH, "Matches your current session language (${sessionProfile.primaryLanguage})")
                genreScore > 0f -> FlowReason(FlowReasonType.GENRE_MATCH, "Matches your listening genres")
                artistId != null && artistId == currentArtistId -> FlowReason(FlowReasonType.SIMILAR_ARTIST, "Similar to current artist")
                favoriteTrackIds.contains(metadata.id) || metadata.liked -> FlowReason(FlowReasonType.FAVORITE_ARTIST, "From your favorites")
                energyScore > 0f -> FlowReason(FlowReasonType.ENERGY_MATCH, "Fits your current listening context")
                discoveryBonus > 8f -> FlowReason(FlowReasonType.DISCOVERY, "Fresh discovery for you")
                else -> FlowReason(FlowReasonType.PERSONAL_TASTE, "Based on your taste profile")
            }

            FlowScore(
                candidate = candidate,
                totalScore = totalScore,
                artistAffinity = artistScore,
                genreAffinity = genreScore,
                languageMatch = languageScore,
                energyMatch = energyScore,
                discoveryBonus = discoveryBonus,
                skipPenalty = skipPenalty,
                repetitionPenalty = recPenalty,
                primaryReason = reason
            )
        }.sortedByDescending { it.totalScore }
    }
}
