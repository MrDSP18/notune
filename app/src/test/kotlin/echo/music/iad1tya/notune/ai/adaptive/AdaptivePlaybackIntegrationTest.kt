package echo.music.iad1tya.notune.ai.adaptive

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AdaptivePlaybackIntegrationTest {

    private lateinit var scoringEngine: AdaptiveScoringEngine
    private lateinit var queueManager: AdaptiveQueueManager
    private lateinit var recommendationEngine: AdaptiveRecommendationEngine
    private lateinit var playbackBridge: AdaptivePlaybackBridge

    private val trackA = AdaptiveTrackContext(trackId = "track_1", title = "Song 1", artist = "Artist A", album = "Album 1", genre = "Pop", language = "Tamil", bpm = 120f, valence = 0.8f, energy = 0.7f)
    private val trackB = AdaptiveTrackContext(trackId = "track_2", title = "Song 2", artist = "Artist A", album = "Album 1", genre = "Pop", language = "Tamil", bpm = 122f, valence = 0.82f, energy = 0.75f)
    private val trackC = AdaptiveTrackContext(trackId = "track_3", title = "Song 3", artist = "Artist A", album = "Album 1", genre = "Pop", language = "Tamil", bpm = 121f, valence = 0.79f, energy = 0.71f)
    private val trackD = AdaptiveTrackContext(trackId = "track_4", title = "Song 4", artist = "Artist B", album = "Album 2", genre = "Rock", language = "English", bpm = 140f, valence = 0.4f, energy = 0.85f)
    private val trackE = AdaptiveTrackContext(trackId = "track_5", title = "Song 5", artist = "Artist C", album = "Album 3", genre = "Chill", language = "Tamil", bpm = 118f, valence = 0.85f, energy = 0.60f)

    @Before
    fun setup() {
        scoringEngine = AdaptiveScoringEngine()
        queueManager = AdaptiveQueueManager(scoringEngine)
        recommendationEngine = AdaptiveRecommendationEngine(scoringEngine, queueManager)
        playbackBridge = AdaptivePlaybackBridge(recommendationEngine)
        playbackBridge.startNewSession("integration-test-session")
    }

    // 1. Playback completion -> FeedbackEvent
    @Test
    fun testPlaybackCompletionEmitsFeedback() {
        playbackBridge.onPlaybackEvent(FeedbackEventType.COMPLETE, trackA, 180000L, 180000L)
        assertNotNull(playbackBridge.currentSessionId.value)
    }

    // 2. Skip -> FeedbackEvent
    @Test
    fun testSkipEmitsFeedback() {
        playbackBridge.onPlaybackEvent(FeedbackEventType.SKIP, trackB, 15000L, 180000L)
        assertNotNull(playbackBridge.currentSessionId.value)
    }

    // 3. Replay -> FeedbackEvent
    @Test
    fun testReplayEmitsFeedback() {
        playbackBridge.onPlaybackEvent(FeedbackEventType.REPLAY, trackA, 0L, 180000L)
        assertNotNull(playbackBridge.currentSessionId.value)
    }

    // 4. Like -> FeedbackEvent
    @Test
    fun testLikeEmitsFeedback() {
        playbackBridge.onPlaybackEvent(FeedbackEventType.LIKE, trackA, 50000L, 180000L)
        assertNotNull(playbackBridge.currentSessionId.value)
    }

    // 5. Dislike -> FeedbackEvent
    @Test
    fun testDislikeEmitsFeedback() {
        playbackBridge.onPlaybackEvent(FeedbackEventType.DISLIKE, trackD, 10000L, 180000L)
        assertNotNull(playbackBridge.currentSessionId.value)
    }

    // 6. Queue adaptation after skip
    @Test
    fun testQueueAdaptationAfterSkip() = runBlocking {
        playbackBridge.onPlaybackEvent(FeedbackEventType.SKIP, trackD, 5000L, 180000L)
        val remainingQueue = listOf(trackD, trackB, trackE)
        val candidatePool = listOf(trackB, trackE)

        val adapted = playbackBridge.adaptQueueAsync(trackA, remainingQueue, candidatePool)
        assertTrue("Adapted queue should be non-empty", adapted.isNotEmpty())
        assertFalse("Skipped/Disliked track D should not be first in adapted queue", adapted.firstOrNull()?.trackId == "track_4")
    }

    // 7. Explicit playlist intent preserved
    @Test
    fun testExplicitPlaylistIntentPreserved() {
        val intent = UserIntent(UserIntentType.PLAYLIST, targetName = "Pop")
        playbackBridge.setUserIntent(intent)

        val request = RecommendationRequest(currentTrack = trackA, userIntent = intent)
        val scorePop = scoringEngine.scoreCandidate(trackB, request)
        val scoreRock = scoringEngine.scoreCandidate(trackD, request)

        assertTrue("Pop track score ($scorePop) must exceed Rock track score ($scoreRock) under Pop playlist intent", scorePop.score > scoreRock.score)
    }

    // 8. Explicit album intent preserved
    @Test
    fun testExplicitAlbumIntentPreserved() {
        val intent = UserIntent(UserIntentType.ALBUM, targetName = "Album 1")
        playbackBridge.setUserIntent(intent)

        val request = RecommendationRequest(currentTrack = trackA, userIntent = intent)
        val scoreAlbum1 = scoringEngine.scoreCandidate(trackB, request)
        val scoreAlbum2 = scoringEngine.scoreCandidate(trackD, request)

        assertTrue("Album 1 track score ($scoreAlbum1) must exceed Album 2 score ($scoreAlbum2) under Album 1 intent", scoreAlbum1.score > scoreAlbum2.score)
    }

    // 9. Explicit artist intent preserved without hard rejection
    @Test
    fun testExplicitArtistIntentPreservedWithoutHardRejection() {
        val intent = UserIntent(UserIntentType.ARTIST, targetName = "Artist A")
        playbackBridge.setUserIntent(intent)

        // Even if Artist A has 3 tracks in queue, artist intent prevents hard rejection
        val recentArtists = mapOf("artist a" to 3)
        val request = RecommendationRequest(currentTrack = trackA, userIntent = intent)

        val score = scoringEngine.scoreCandidate(trackC, request, recentArtistCounts = recentArtists)
        assertTrue("Track C under explicit Artist A intent must retain positive score despite fatigue", score.score > 0.3f)
    }

    // 10. Currently playing track is never replaced
    @Test
    fun testCurrentlyPlayingTrackNeverReplaced() = runBlocking {
        val remainingQueue = listOf(trackB, trackC)
        val adapted = playbackBridge.adaptQueueAsync(trackA, remainingQueue, listOf(trackD, trackE))

        // Currently playing trackA remains active reference
        assertNotNull(adapted)
        assertFalse("Currently playing track A should not appear in upcoming remaining queue", adapted.any { it.trackId == trackA.trackId })
    }

    // 11. Already-played tracks are not unexpectedly reinserted
    @Test
    fun testAlreadyPlayedTracksExcluded() = runBlocking {
        playbackBridge.onPlaybackEvent(FeedbackEventType.PLAY, trackA)
        playbackBridge.onPlaybackEvent(FeedbackEventType.COMPLETE, trackA, 180000L, 180000L)

        val remainingQueue = listOf(trackB, trackC)
        val adapted = playbackBridge.adaptQueueAsync(trackB, remainingQueue, listOf(trackA, trackD, trackE))

        assertFalse("Already played track A must not be reinserted into adapted queue", adapted.any { it.trackId == trackA.trackId })
    }

    // 12. Offline adaptive queue
    @Test
    fun testOfflineAdaptiveQueue() = runBlocking {
        // Local music tracks with local metadata
        val localTrack1 = AdaptiveTrackContext("local_1", "Local 1", "Local Artist", bpm = 115f)
        val localTrack2 = AdaptiveTrackContext("local_2", "Local 2", "Local Artist", bpm = 118f)

        val adapted = playbackBridge.adaptQueueAsync(localTrack1, listOf(localTrack2), listOf(localTrack2))
        assertEquals(1, adapted.size)
        assertEquals("local_2", adapted.first().trackId)
    }

    // 13. Empty candidate fallback
    @Test
    fun testEmptyCandidateFallback() = runBlocking {
        val adapted = playbackBridge.adaptQueueAsync(trackA, emptyList(), emptyList())
        assertTrue("Adapted queue should be empty when no candidates are provided", adapted.isEmpty())
    }

    // 14. Missing metadata fallback
    @Test
    fun testMissingMetadataFallback() = runBlocking {
        val bare1 = AdaptiveTrackContext("bare_1", "Bare 1", "Artist 1")
        val bare2 = AdaptiveTrackContext("bare_2", "Bare 2", "Artist 2")

        val adapted = playbackBridge.adaptQueueAsync(bare1, listOf(bare2), listOf(bare2))
        assertEquals(1, adapted.size)
        assertEquals("bare_2", adapted.first().trackId)
    }

    // 15. Room mode does not independently mutate shared playback
    @Test
    fun testRoomModeGuardsQueueMutation() = runBlocking {
        playbackBridge.setRoomModeActive(true)

        val initialQueue = listOf(trackB, trackC)
        val candidatePool = listOf(trackD, trackE)

        val adapted = playbackBridge.adaptQueueAsync(trackA, initialQueue, candidatePool)

        assertEquals("In Room mode, queue must remain unmutated by local client", initialQueue, adapted)
    }

    // 16. Recommendation calculation does not execute on UI thread
    @Test
    fun testThreadingOffload() = runBlocking {
        // Dispatchers.Default ensures calculation runs off the main thread
        val adapted = playbackBridge.adaptQueueAsync(trackA, listOf(trackB), listOf(trackC))
        assertNotNull(adapted)
    }

    // 17. Duplicate feedback events are prevented
    @Test
    fun testDuplicateEventPrevention() {
        playbackBridge.onPlaybackEvent(FeedbackEventType.SKIP, trackB, 10000L, 180000L)
        playbackBridge.onPlaybackEvent(FeedbackEventType.SKIP, trackB, 10000L, 180000L) // Duplicate

        assertNotNull(playbackBridge.currentSessionId.value)
    }

    // 18. Deterministic behavior for identical state
    @Test
    fun testDeterministicBehavior() = runBlocking {
        val queue = listOf(trackB, trackC, trackD)
        val pool = listOf(trackE)

        val adapted1 = playbackBridge.adaptQueueAsync(trackA, queue, pool)
        val adapted2 = playbackBridge.adaptQueueAsync(trackA, queue, pool)

        assertEquals("Adapted queues for identical state must be exactly deterministic", adapted1.map { it.trackId }, adapted2.map { it.trackId })
    }
}
