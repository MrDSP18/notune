package echo.music.iad1tya.notune.flow

import echo.music.iad1tya.models.MediaMetadata
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FlowCandidateScorerTest {

    private lateinit var scorer: FlowCandidateScorer

    @Before
    fun setUp() {
        scorer = FlowCandidateScorer()
    }

    @Test
    fun testScoringRanksFavoriteAndArtistAffinityHigher() {
        val currentTrack = MediaMetadata(
            id = "song1",
            title = "Current Track",
            artists = listOf(MediaMetadata.Artist(id = "artist1", name = "Artist 1")),
            duration = 180
        )

        val candidate1 = FlowCandidate(
            mediaMetadata = MediaMetadata(
                id = "song2",
                title = "Candidate 2",
                artists = listOf(MediaMetadata.Artist(id = "artist1", name = "Artist 1")),
                duration = 180
            ),
            candidateSource = "Related Track"
        )

        val candidate2 = FlowCandidate(
            mediaMetadata = MediaMetadata(
                id = "song3",
                title = "Candidate 3",
                artists = listOf(MediaMetadata.Artist(id = "artist2", name = "Artist 2")),
                duration = 180
            ),
            candidateSource = "Random"
        )

        val scored = scorer.scoreCandidates(
            candidates = listOf(candidate2, candidate1),
            currentTrack = currentTrack,
            mode = FlowMode.AUTO_FLOW,
            contextMode = FlowContextMode.NORMAL,
            discoveryRatio = 0.3f,
            favoriteTrackIds = setOf("song2")
        )

        assertEquals(2, scored.size)
        assertEquals("song2", scored.first().candidate.mediaMetadata.id)
        assertTrue(scored.first().totalScore > scored.last().totalScore)
    }
}
