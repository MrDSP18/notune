package com.music.echo.notune.intelligence.search

import com.music.echo.notune.intelligence.musicbrain.MusicBrain
import com.music.echo.notune.intelligence.personalization.TasteProfileStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PersonalizedSearchEngine @Inject constructor(
    private val intentParser: SearchIntentParser,
    private val musicBrain: MusicBrain,
    private val tasteProfileStore: TasteProfileStore
) {

    fun rankCandidates(
        rawQuery: String,
        candidates: List<SearchCandidate>
    ): List<RankedSearchResult> {
        val intent = intentParser.parseIntent(rawQuery)
        val userDna = tasteProfileStore.getDnaSnapshot()

        val results = candidates.map { candidate ->
            // Compute query relevance
            var queryRelevance = 0.5f
            val lowerTitle = candidate.title.lowercase()
            val lowerArtist = candidate.artistName.lowercase()
            val lowerQuery = rawQuery.lowercase()

            if (lowerTitle.contains(lowerQuery) || lowerArtist.contains(lowerQuery)) {
                queryRelevance = 1.0f
            } else if (intent.targetArtist != null && lowerArtist.contains(intent.targetArtist.lowercase())) {
                queryRelevance = 0.9f
            } else if (intent.targetLanguage != null && candidate.embedding.language.equals(intent.targetLanguage, ignoreCase = true)) {
                queryRelevance = 0.8f
            }

            // Energy filtering
            if (intent.minEnergy != null && candidate.embedding.energy < intent.minEnergy) {
                queryRelevance *= 0.4f
            }
            if (intent.maxEnergy != null && candidate.embedding.energy > intent.maxEnergy) {
                queryRelevance *= 0.4f
            }

            // Underrated filter
            if (intent.isUnderratedRequest && candidate.embedding.popularity > 0.6f) {
                queryRelevance *= 0.3f
            }

            val scoreResult = musicBrain.scoreTrack(candidate.embedding, userDna, queryRelevance)

            val explanation = buildString {
                append("Score: ${(scoreResult.totalScore * 100).toInt()}% • ")
                if (queryRelevance >= 0.8f) append("Strong query match • ")
                if (scoreResult.userTaste >= 0.6f) append("Matches core taste • ")
                if (scoreResult.languageMatch >= 0.8f) append("Preferred ${candidate.embedding.language} language • ")
                if (intent.targetMood != null) append("Fits '${intent.targetMood}' mood")
            }

            RankedSearchResult(
                candidate = candidate,
                scoreResult = scoreResult,
                explanation = explanation
            )
        }

        return results
            .filter { it.candidate.isAvailableOfflineOrStream && it.scoreResult.totalScore > 0f }
            .sortedByDescending { it.scoreResult.totalScore }
    }

    suspend fun searchRealCatalog(
        rawQuery: String,
        providerRegistry: echo.music.iad1tya.notune.provider.ProviderRegistry,
        searchQualityGate: SearchQualityGate
    ): List<RankedSearchResult> {
        val userDna = tasteProfileStore.getDnaSnapshot()
        val unifiedTracks = providerRegistry.searchUnifiedCatalog(rawQuery)

        val candidates = unifiedTracks.map { track ->
            val embedding = com.music.echo.notune.intelligence.musicbrain.TrackEmbedding(
                trackId = track.id,
                title = track.title,
                artistName = track.artist,
                genre = userDna.coreTaste.genres.keys.firstOrNull() ?: "Pop",
                language = userDna.coreTaste.languages.keys.firstOrNull() ?: "English"
            )
            SearchCandidate(
                id = track.id,
                title = track.title,
                artistName = track.artist,
                albumName = track.album,
                embedding = embedding,
                isAvailableOfflineOrStream = track.rights.isStreamable || track.rights.isDownloadable
            )
        }

        val passedCandidates = candidates.filter { searchQualityGate.evaluateCandidate(it, userDna).isPassed }
        return rankCandidates(rawQuery, passedCandidates)
    }
}
