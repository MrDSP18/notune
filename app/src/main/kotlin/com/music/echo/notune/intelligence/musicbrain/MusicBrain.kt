package com.music.echo.notune.intelligence.musicbrain

import com.music.echo.notune.intelligence.personalization.NotuneUserDNA
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

data class AdaptiveRankingWeights(
    val queryMatchWeight: Float = 0.25f,
    val userTasteWeight: Float = 0.20f,
    val sessionMatchWeight: Float = 0.15f,
    val artistAffinityWeight: Float = 0.10f,
    val languageMatchWeight: Float = 0.08f,
    val moodMatchWeight: Float = 0.07f,
    val audioSimilarityWeight: Float = 0.05f,
    val freshnessWeight: Float = 0.05f,
    val discoveryValueWeight: Float = 0.05f
)

data class TrackScoreResult(
    val trackId: String,
    val totalScore: Float,
    val queryMatch: Float,
    val userTaste: Float,
    val sessionMatch: Float,
    val artistAffinity: Float,
    val languageMatch: Float,
    val moodMatch: Float,
    val audioSimilarity: Float,
    val freshness: Float,
    val discoveryValue: Float,
    val activeWeights: AdaptiveRankingWeights
)

@Singleton
class MusicBrain @Inject constructor(
    private val similarityEngine: SimilarityEngine,
    private val moodEngine: MoodEngine,
    private val energyEngine: EnergyEngine,
    private val languageEngine: LanguageEngine
) {

    /**
     * Scores a track against the user's DNA, active session context, and query relevance.
     */
    fun scoreTrack(
        track: TrackEmbedding,
        userDna: NotuneUserDNA,
        queryRelevance: Float = 1.0f,
        customWeights: AdaptiveRankingWeights? = null
    ): TrackScoreResult {

        val weights = customWeights ?: deriveAdaptiveWeights(userDna)

        // 1. Vector similarity
        val userVector = UserEmbedding(
            targetEnergy = userDna.sessionTaste.currentEnergy,
            noveltyPreference = userDna.discoveryProfile.noveltyTolerance
        ).toNormalizedVector()
        val trackVector = track.toNormalizedVector()
        val audioSim = similarityEngine.cosineSimilarity(userVector, trackVector)

        // 2. User Taste Match (genres & artists)
        val genreScore = userDna.coreTaste.genres[track.genre] ?: 0.3f
        val artistScore = userDna.coreTaste.artists[track.artistName] ?: 0.2f
        val userTaste = (genreScore * 0.6f + artistScore * 0.4f).coerceIn(0f, 1f)

        // 3. Session Match
        val energyMatch = energyEngine.scoreEnergyMatch(userDna.sessionTaste.currentEnergy, track.energy)
        val sessionMatch = energyMatch

        // 4. Artist Affinity
        val artistAffinity = if (userDna.coreTaste.artists.containsKey(track.artistName)) {
            userDna.coreTaste.artists[track.artistName] ?: 0.5f
        } else {
            0.2f
        }

        // 5. Language Match
        val langMatch = languageEngine.scoreLanguageMatch(userDna.coreTaste.languages, track.language)

        // 6. Mood Match
        val moodMatch = moodEngine.scoreMoodMatch(userDna.sessionTaste.currentMood, track.moodValence)

        // 7. Freshness & Discovery
        val freshness = 1.0f - (track.popularity * 0.4f)
        val discoveryValue = track.novelty * userDna.discoveryProfile.explorationRate

        // Calculate weighted sum
        val total = (queryRelevance * weights.queryMatchWeight) +
                (userTaste * weights.userTasteWeight) +
                (sessionMatch * weights.sessionMatchWeight) +
                (artistAffinity * weights.artistAffinityWeight) +
                (langMatch * weights.languageMatchWeight) +
                (moodMatch * weights.moodMatchWeight) +
                (audioSim * weights.audioSimilarityWeight) +
                (freshness * weights.freshnessWeight) +
                (discoveryValue * weights.discoveryValueWeight)

        // Check explicit never recommend filter
        val isNever = userDna.explicitPreferences.neverRecommendArtists.any { it.equals(track.artistName, ignoreCase = true) } ||
                userDna.explicitPreferences.neverRecommendSongs.contains(track.trackId)
        val finalScore = if (isNever) 0.0f else total.coerceIn(0f, 1f)

        return TrackScoreResult(
            trackId = track.trackId,
            totalScore = finalScore,
            queryMatch = queryRelevance,
            userTaste = userTaste,
            sessionMatch = sessionMatch,
            artistAffinity = artistAffinity,
            languageMatch = langMatch,
            moodMatch = moodMatch,
            audioSimilarity = audioSim,
            freshness = freshness,
            discoveryValue = discoveryValue,
            activeWeights = weights
        )
    }

    /**
     * Derives adaptive ranking weights dynamically per user based on their DNA.
     */
    fun deriveAdaptiveWeights(dna: NotuneUserDNA): AdaptiveRankingWeights {
        var langW = 0.08f
        var artistW = 0.10f
        var discW = 0.05f

        // If user has strong language preference
        val topLangScore = dna.coreTaste.languages.values.maxOrNull() ?: 0.5f
        if (topLangScore > 0.85f) langW = 0.16f

        // High exploration preference
        if (dna.discoveryProfile.explorationRate > 0.6f) discW = 0.12f

        // High artist commitment
        if (dna.coreTaste.artists.size > 10) artistW = 0.15f

        val sum = 0.25f + 0.20f + 0.15f + artistW + langW + 0.07f + 0.05f + 0.05f + discW
        return AdaptiveRankingWeights(
            queryMatchWeight = 0.25f / sum,
            userTasteWeight = 0.20f / sum,
            sessionMatchWeight = 0.15f / sum,
            artistAffinityWeight = artistW / sum,
            languageMatchWeight = langW / sum,
            moodMatchWeight = 0.07f / sum,
            audioSimilarityWeight = 0.05f / sum,
            freshnessWeight = 0.05f / sum,
            discoveryValueWeight = discW / sum
        )
    }
}
