package echo.music.iad1tya.notune.ai.adaptive

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AdaptiveIntelligenceTest {

    private val scoringConfig = AdaptiveScoringConfig(
        skipPenalty = -0.15f,
        replayBoost = 0.20f,
        maxBpmTransitionDelta = 0.15f,
        artistRepetitionLimit = 2
    )

    private val scoringEngine = AdaptiveScoringEngine(scoringConfig)
    private val queueManager = AdaptiveQueueManager(scoringEngine)
    private val recommendationEngine = AdaptiveRecommendationEngine(scoringEngine, queueManager)

    private val sampleTrackA = AdaptiveTrackContext(
        trackId = "track_a",
        title = "Song A",
        artist = "Artist One",
        genre = "Pop",
        language = "Tamil",
        bpm = 120.0f,
        valence = 0.8f,
        energy = 0.7f
    )

    private val sampleTrackB = AdaptiveTrackContext(
        trackId = "track_b",
        title = "Song B",
        artist = "Artist One",
        genre = "Pop",
        language = "Tamil",
        bpm = 122.0f,
        valence = 0.82f,
        energy = 0.75f
    )

    private val sampleTrackC = AdaptiveTrackContext(
        trackId = "track_c",
        title = "Song C",
        artist = "Artist Two",
        genre = "Rock",
        language = "English",
        bpm = 175.0f, // Large BPM delta from 120 (45%)
        valence = 0.3f,
        energy = 0.9f
    )

    private val sampleTrackD = AdaptiveTrackContext(
        trackId = "track_d",
        title = "Song D",
        artist = "Artist Three",
        genre = "Pop",
        language = "Tamil",
        bpm = 124.0f,
        valence = 0.78f,
        energy = 0.68f
    )

    // 1. Skip penalty test
    @Test
    fun testSkipPenaltyDecreasesScore() {
        val requestNormal = RecommendationRequest(currentTrack = sampleTrackA)
        val requestSkipped = RecommendationRequest(
            currentTrack = sampleTrackA,
            skippedTracks = listOf(sampleTrackB.trackId)
        )

        val scoreNormal = scoringEngine.scoreCandidate(sampleTrackB, requestNormal)
        val scoreSkipped = scoringEngine.scoreCandidate(sampleTrackB, requestSkipped)

        assertTrue(
            "Skipped track score ($scoreSkipped) should be lower than normal score ($scoreNormal)",
            scoreSkipped.score < scoreNormal.score
        )
    }

    // 2. Replay boost test
    @Test
    fun testReplayBoostIncreasesScore() {
        val requestNormal = RecommendationRequest(currentTrack = sampleTrackA)
        val requestReplayed = RecommendationRequest(
            currentTrack = sampleTrackA,
            replayedTracks = listOf(sampleTrackB.trackId)
        )

        val scoreNormal = scoringEngine.scoreCandidate(sampleTrackB, requestNormal)
        val scoreReplayed = scoringEngine.scoreCandidate(sampleTrackB, requestReplayed)

        assertTrue(
            "Replayed track score ($scoreReplayed) should be higher than normal score ($scoreNormal)",
            scoreReplayed.score > scoreNormal.score
        )
    }

    // 3. BPM transition filtering test
    @Test
    fun testBpmTransitionFiltering() {
        val scoredCandidates = listOf(
            scoringEngine.scoreCandidate(sampleTrackB, RecommendationRequest(currentTrack = sampleTrackA)),
            scoringEngine.scoreCandidate(sampleTrackC, RecommendationRequest(currentTrack = sampleTrackA))
        )

        val filtered = queueManager.filterAntiFatigue(scoredCandidates, emptyList(), sampleTrackA)

        assertTrue("Track B (122 BPM) should pass filter for Track A (120 BPM)", filtered.any { it.track.trackId == "track_b" })
        assertFalse("Track C (175 BPM, >15% delta) should be filtered out for Track A (120 BPM)", filtered.any { it.track.trackId == "track_c" })
    }

    // 4. Artist fatigue test
    @Test
    fun testArtistFatigueLimit() {
        val recentArtists = mapOf("artist one" to 2) // Artist One already played 2 times
        val request = RecommendationRequest(currentTrack = sampleTrackA)

        val scored = scoringEngine.scoreCandidate(sampleTrackB, request, recentArtistCounts = recentArtists)

        // Verify candidate B (Artist One) receives artist fatigue penalty
        val normalScored = scoringEngine.scoreCandidate(sampleTrackB, request)
        assertTrue("Artist One score under fatigue ($scored) should be lower than normal ($normalScored)", scored.score < normalScored.score)
    }

    // 5. Genre fatigue test
    @Test
    fun testGenreFatigueLimit() {
        val recentGenres = mapOf("pop" to 3) // Pop played 3 times
        val request = RecommendationRequest(currentTrack = sampleTrackA)

        val scoredPop = scoringEngine.scoreCandidate(sampleTrackB, request, recentGenreCounts = recentGenres)
        val normalPop = scoringEngine.scoreCandidate(sampleTrackB, request, recentGenreCounts = emptyMap())

        assertTrue("Pop track score under genre fatigue ($scoredPop) should be lower than normal ($normalPop)", scoredPop.score < normalPop.score)
    }

    // 6. Duplicate/recent-track filtering test
    @Test
    fun testRecentTrackFiltering() {
        val history = listOf(sampleTrackB)
        val scoredCandidates = listOf(
            scoringEngine.scoreCandidate(sampleTrackB, RecommendationRequest()),
            scoringEngine.scoreCandidate(sampleTrackD, RecommendationRequest())
        )

        val filtered = queueManager.filterAntiFatigue(scoredCandidates, history, null)

        assertFalse("Recently played Track B should be filtered out", filtered.any { it.track.trackId == "track_b" })
        assertTrue("Unplayed Track D should remain in candidates", filtered.any { it.track.trackId == "track_d" })
    }

    // 7. Score ordering test
    @Test
    fun testScoreOrdering() {
        val candidates = listOf(sampleTrackC, sampleTrackB, sampleTrackD)
        val request = RecommendationRequest(currentTrack = sampleTrackA)

        val response = recommendationEngine.generateRecommendations(request, candidates, ModelSource.ANDROID_LITERT)

        val scores = response.recommendedTracks.map { it.score }
        assertEquals("Scores should be sorted in descending order", scores, scores.sortedDescending())
        assertEquals("Top recommendation model should be ANDROID_LITERT", ModelSource.ANDROID_LITERT, response.model)
    }

    // 8. Queue replacement / adaptation test
    @Test
    fun testQueueAdaptationOnSkip() {
        queueManager.resetSession()

        // Initial queue: B, C, D
        val initialQueue = listOf(sampleTrackB, sampleTrackC, sampleTrackD)
        val candidatePool = listOf(
            AdaptiveTrackContext(trackId = "track_e", title = "Song E", artist = "Artist Four", genre = "Pop", bpm = 121.0f),
            AdaptiveTrackContext(trackId = "track_f", title = "Song F", artist = "Artist Five", genre = "Pop", bpm = 122.0f)
        )

        // User skips Track C
        queueManager.recordFeedback(FeedbackEvent(eventType = FeedbackEventType.SKIP, trackId = "track_c"))

        val adaptedQueue = queueManager.adaptQueue(sampleTrackA, initialQueue, candidatePool, emptyList())

        assertFalse("Skipped Track C should be filtered/re-ranked out of high-priority queue", adaptedQueue.take(2).any { it.trackId == "track_c" })
        assertTrue("Adapted queue should contain valid candidates", adaptedQueue.isNotEmpty())
    }

    // 9. Feedback event generation test
    @Test
    fun testFeedbackEventGeneration() {
        queueManager.resetSession()

        val eventLike = FeedbackEvent(eventType = FeedbackEventType.LIKE, trackId = "track_b")
        val eventSkip = FeedbackEvent(eventType = FeedbackEventType.SKIP, trackId = "track_c")

        queueManager.recordFeedback(eventLike)
        queueManager.recordFeedback(eventSkip)

        val request = RecommendationRequest(
            currentTrack = sampleTrackA,
            likedTrackIds = setOf("track_b"),
            skippedTracks = listOf("track_c")
        )

        val scoreLiked = scoringEngine.scoreCandidate(sampleTrackB, request)
        val scoreSkipped = scoringEngine.scoreCandidate(sampleTrackC, request)

        assertTrue("Liked track score ($scoreLiked) must be higher than skipped track score ($scoreSkipped)", scoreLiked.score > scoreSkipped.score)
    }

    // 10. Empty candidate list fallback test
    @Test
    fun testEmptyCandidateFallback() {
        val request = RecommendationRequest(currentTrack = sampleTrackA)

        val response = recommendationEngine.generateRecommendations(request, emptyList(), ModelSource.ANDROID_LITERT)

        assertTrue("Recommended tracks should be empty", response.recommendedTracks.isEmpty())
        assertEquals("Confidence should be 0.0 for empty candidates", 0.0f, response.confidence, 0.001f)
        assertEquals("Model should fallback to FALLBACK", ModelSource.FALLBACK, response.model)
    }

    // 11. Missing metadata fallback test
    @Test
    fun testMissingMetadataFallback() {
        val bareTrack = AdaptiveTrackContext(trackId = "bare_1", title = "Bare Song", artist = "Bare Artist")
        val request = RecommendationRequest(currentTrack = null)

        val scored = scoringEngine.scoreCandidate(bareTrack, request)

        assertNotNull("Scored track should not be null", scored)
        assertTrue("Score should be non-negative", scored.score >= 0.0f)
        assertTrue("Explainability reason should be populated", scored.reason.isNotBlank())
    }

    // 12. Deterministic results for identical inputs test
    @Test
    fun testDeterministicResults() {
        val request = RecommendationRequest(currentTrack = sampleTrackA)
        val candidates = listOf(sampleTrackB, sampleTrackC, sampleTrackD)

        val run1 = recommendationEngine.generateRecommendations(request, candidates, ModelSource.ANDROID_LITERT)
        val run2 = recommendationEngine.generateRecommendations(request, candidates, ModelSource.ANDROID_LITERT)

        assertEquals("Run 1 and Run 2 track IDs must match exactly", run1.recommendedTracks.map { it.track.trackId }, run2.recommendedTracks.map { it.track.trackId })
        assertEquals("Run 1 and Run 2 scores must match exactly", run1.recommendedTracks.map { it.score }, run2.recommendedTracks.map { it.score })
    }
}
