package echo.music.iad1tya.notune.flow

import echo.music.iad1tya.models.MediaMetadata
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.music.echo.notune.intelligence.NotuneIntelligenceEngine
import com.music.echo.notune.intelligence.feedback.UserEvent
import com.music.echo.notune.intelligence.session.Media3PlaybackListener
import com.music.echo.notune.intelligence.session.PersonalMusicSession
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AdaptiveEndlessQueueEngineTest {

    private lateinit var filter: FlowCandidateFilter
    private lateinit var scorer: FlowCandidateScorer
    private lateinit var optimizer: FlowQueueOptimizer
    private lateinit var languageTracker: LanguageSessionTracker

    @Before
    fun setUp() {
        filter = FlowCandidateFilter()
        scorer = FlowCandidateScorer()
        optimizer = FlowQueueOptimizer()
        languageTracker = LanguageSessionTracker()
    }

    @Test
    fun testPlaybackFeedbackUsesOutgoingTrackMetadata() {
        val intelligence = mockk<NotuneIntelligenceEngine>(relaxed = true)
        val session = PersonalMusicSession()
        assertEquals("", session.liveTasteSnapshot.value.primaryGenre)
        assertEquals(null, session.liveTasteSnapshot.value.longTermAffinityPct)
        val listener = Media3PlaybackListener(intelligence, session)
        fun item(id: String, title: String, artist: String) = MediaItem.Builder()
            .setMediaId(id)
            .setMediaMetadata(androidx.media3.common.MediaMetadata.Builder().setTitle(title).setArtist(artist).build())
            .build()

        listener.onMediaItemTransition(item("first", "First", "Artist A"), Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED)
        listener.onMediaItemTransition(item("second", "Second", "Artist B"), Player.MEDIA_ITEM_TRANSITION_REASON_AUTO)
        verify { intelligence.recordFeedback(match {
            it is UserEvent.Completed && it.trackId == "first" && it.title == "First" && it.artistName == "Artist A"
        }) }
        listener.onMediaItemTransition(item("third", "Third", "Artist C"), Player.MEDIA_ITEM_TRANSITION_REASON_SEEK)
        verify { intelligence.recordFeedback(match {
            it is UserEvent.Skip && it.trackId == "second" && it.title == "Second" && it.artistName == "Artist B"
        }) }
        assertEquals("Artist C", session.sessionState.value.currentTrack?.artistName)
        listener.onMediaItemTransition(item("third", "Third", "Artist C"), Player.MEDIA_ITEM_TRANSITION_REASON_REPEAT)
        verify { intelligence.recordFeedback(match {
            it is UserEvent.Replay && it.trackId == "third" && it.title == "Third"
        }) }
        assertEquals(1, session.sessionState.value.recentReplaysCount)
    }

    @Test
    fun testQueueReplenishmentTriggerWhenUpcomingLow() {
        val shouldRefillAt4 = optimizer.shouldRefillQueue(4)
        val shouldRefillAt10 = optimizer.shouldRefillQueue(10)

        assertTrue("Queue should refill when upcoming items count is 4 (<=5)", shouldRefillAt4)
        assertFalse("Queue should not refill when upcoming items count is 10 (>5)", shouldRefillAt10)
    }

    @Test
    fun testGeneratedQueueNeverDuplicatesExistingManualEntries() {
        val candidate = FlowQueueItem(
            mediaMetadata = MediaMetadata(id = "manual", title = "Manual", artists = emptyList(), duration = 180),
            source = ItemSource.FLOW,
            isLocked = false,
            reason = FlowReason(FlowReasonType.PERSONAL_TASTE, "Taste"),
            generationId = 1L
        )
        val fresh = candidate.copy(mediaMetadata = candidate.mediaMetadata.copy(id = "new", title = "New"))
        assertEquals(listOf("new"), optimizer.appendableItems(listOf("playing", "manual"), listOf(candidate, fresh, fresh))
            .map { it.mediaMetadata.id })
    }

    @Test
    fun testRankedCandidatesCannotAddSameTrackTwice() {
        val candidate = FlowCandidate(
            MediaMetadata(id = "repeat", title = "Repeat", artists = emptyList(), duration = 180), "Catalog"
        )
        val scored = FlowScore(candidate, 20f, primaryReason = FlowReason(FlowReasonType.PERSONAL_TASTE, "Taste"))
        val result = optimizer.buildOptimizedQueue(emptyList(), listOf(scored, scored), 1L)
        assertEquals(listOf("repeat"), result.map { it.mediaMetadata.id })
    }

    @Test
    fun testFavoriteFeedbackUpdatesFlowCandidates() {
        val feedback = FlowFeedbackProcessor(languageTracker)
        feedback.recordFavorite("liked", true)
        assertTrue("liked" in feedback.getFavoriteTrackIds())
        feedback.recordFavorite("liked", false)
        assertFalse("liked" in feedback.getFavoriteTrackIds())
    }

    @Test
    fun testHardAntiRepetitionNoDuplicatesInActiveQueue() {
        val currentTrack = MediaMetadata(id = "s1", title = "Song 1", artists = emptyList(), duration = 180)
        val candidate1 = FlowCandidate(MediaMetadata(id = "s1", title = "Song 1", artists = emptyList(), duration = 180), "Source")
        val candidate2 = FlowCandidate(MediaMetadata(id = "s2", title = "Song 2", artists = emptyList(), duration = 180), "Source")
        val candidate3 = FlowCandidate(MediaMetadata(id = "s3", title = "Song 3", artists = emptyList(), duration = 180), "Source")

        val filtered = filter.filterCandidates(
            candidates = listOf(candidate1, candidate2, candidate3),
            currentTrack = currentTrack,
            activeQueueTrackIds = listOf("s2")
        )

        assertEquals(1, filtered.size)
        assertEquals("s3", filtered.first().mediaMetadata.id)
    }

    @Test
    fun testLanguageContinuityPreservesPrimaryLanguage() {
        val sessionProfile = LanguageSessionProfile(
            primaryLanguage = "Tamil",
            confidence = 0.8f
        )

        val candidateTamil = FlowCandidate(
            mediaMetadata = MediaMetadata(id = "t1", title = "Tamil Hit Song", artists = emptyList(), duration = 200),
            candidateSource = "Library",
            language = "Tamil"
        )
        val candidateEnglish = FlowCandidate(
            mediaMetadata = MediaMetadata(id = "e1", title = "English Pop Song", artists = emptyList(), duration = 200),
            candidateSource = "Library",
            language = "English"
        )

        val scores = scorer.scoreCandidates(
            candidates = listOf(candidateTamil, candidateEnglish),
            currentTrack = null,
            mode = FlowMode.AUTO_FLOW,
            contextMode = FlowContextMode.NORMAL,
            discoveryRatio = 0.3f,
            sessionProfile = sessionProfile
        )

        val tamilScore = scores.find { it.candidate.mediaMetadata.id == "t1" }!!
        val englishScore = scores.find { it.candidate.mediaMetadata.id == "e1" }!!

        assertTrue("Tamil song should score higher than English song in a Tamil session context", tamilScore.totalScore > englishScore.totalScore)
        assertEquals(FlowReasonType.LANGUAGE_MATCH, tamilScore.primaryReason.type)
    }

    @Test
    fun testArtistAndAlbumSpacingPreventsMonoArtistCluster() {
        val artistA = MediaMetadata.Artist("art1", "Artist A")
        val candidate1 = FlowScore(FlowCandidate(MediaMetadata("s1", "Song 1", listOf(artistA), 180), "src"), 20f, primaryReason = FlowReason(FlowReasonType.PERSONAL_TASTE, "taste"))
        val candidate2 = FlowScore(FlowCandidate(MediaMetadata("s2", "Song 2", listOf(artistA), 180), "src"), 19f, primaryReason = FlowReason(FlowReasonType.PERSONAL_TASTE, "taste"))
        val candidate3 = FlowScore(FlowCandidate(MediaMetadata("s3", "Song 3", listOf(artistA), 180), "src"), 18f, primaryReason = FlowReason(FlowReasonType.PERSONAL_TASTE, "taste"))

        val diversified = filter.diversifyCandidates(listOf(candidate1, candidate2, candidate3))
        assertEquals("Diversification should cap consecutive artist songs at 2", 2, diversified.size)
    }

    @Test
    fun testABABAAlternatingPatternPrevention() {
        val artistA = MediaMetadata.Artist("art1", "Artist A")
        val artistB = MediaMetadata.Artist("art2", "Artist B")

        val s1 = FlowScore(FlowCandidate(MediaMetadata("s1", "Song A", listOf(artistA), 180), "src"), 20f, primaryReason = FlowReason(FlowReasonType.PERSONAL_TASTE, "taste"))
        val s2 = FlowScore(FlowCandidate(MediaMetadata("s2", "Song B", listOf(artistB), 180), "src"), 19f, primaryReason = FlowReason(FlowReasonType.PERSONAL_TASTE, "taste"))
        val s3 = FlowScore(FlowCandidate(MediaMetadata("s1", "Song A", listOf(artistA), 180), "src"), 18f, primaryReason = FlowReason(FlowReasonType.PERSONAL_TASTE, "taste"))

        val diversified = filter.diversifyCandidates(listOf(s1, s2, s3))
        val ids = diversified.map { it.candidate.mediaMetadata.id }

        assertFalse("Diversification should prevent immediate A-B-A pattern flip-flop", ids == listOf("s1", "s2", "s1"))
    }
}
