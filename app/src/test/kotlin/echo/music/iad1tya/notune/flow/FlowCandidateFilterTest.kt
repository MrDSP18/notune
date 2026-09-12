package echo.music.iad1tya.notune.flow

import echo.music.iad1tya.models.MediaMetadata
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FlowCandidateFilterTest {

    private lateinit var filter: FlowCandidateFilter

    @Before
    fun setUp() {
        filter = FlowCandidateFilter()
    }

    @Test
    fun testHardFiltersRemovesCurrentTrackAndQueueItems() {
        val currentTrack = MediaMetadata(id = "song1", title = "Song 1", artists = emptyList(), duration = 180)
        val candidate1 = FlowCandidate(MediaMetadata(id = "song1", title = "Song 1", artists = emptyList(), duration = 180), "source")
        val candidate2 = FlowCandidate(MediaMetadata(id = "song2", title = "Song 2", artists = emptyList(), duration = 180), "source")
        val candidate3 = FlowCandidate(MediaMetadata(id = "song3", title = "Song 3", artists = emptyList(), duration = 180), "source")

        val filtered = filter.filterCandidates(
            candidates = listOf(candidate1, candidate2, candidate3),
            currentTrack = currentTrack,
            activeQueueTrackIds = listOf("song2")
        )

        assertEquals(1, filtered.size)
        assertEquals("song3", filtered.first().mediaMetadata.id)
    }

    @Test
    fun testSoftFilterRemovesRecentlySkippedTracks() {
        val candidate1 = FlowCandidate(MediaMetadata(id = "song1", title = "Song 1", artists = emptyList(), duration = 180), "source")
        val candidate2 = FlowCandidate(MediaMetadata(id = "song2", title = "Song 2", artists = emptyList(), duration = 180), "source")

        val filtered = filter.filterCandidates(
            candidates = listOf(candidate1, candidate2),
            currentTrack = null,
            activeQueueTrackIds = emptyList(),
            recentlySkippedIds = listOf("song1")
        )

        assertEquals(1, filtered.size)
        assertEquals("song2", filtered.first().mediaMetadata.id)
    }
}
