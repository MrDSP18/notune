package echo.music.iad1tya.notune.flow

import echo.music.iad1tya.models.MediaMetadata
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FlowQueueOptimizerTest {

    private lateinit var optimizer: FlowQueueOptimizer

    @Before
    fun setUp() {
        optimizer = FlowQueueOptimizer()
    }

    @Test
    fun testShouldRefillQueueWhenBelowMinimum() {
        assertTrue(optimizer.shouldRefillQueue(upcomingItemsCount = 3, config = FlowConfig(minQueueSize = 5)))
        assertFalse(optimizer.shouldRefillQueue(upcomingItemsCount = 8, config = FlowConfig(minQueueSize = 5)))
    }

    @Test
    fun testBuildOptimizedQueuePreservesManualAndLockedItems() {
        val manualItem = FlowQueueItem(
            mediaMetadata = MediaMetadata(id = "manual1", title = "Manual Song", artists = emptyList(), duration = 180),
            source = ItemSource.MANUAL
        )

        val flowCandidate = FlowScore(
            candidate = FlowCandidate(
                mediaMetadata = MediaMetadata(id = "flow1", title = "Flow Rec 1", artists = emptyList(), duration = 180),
                candidateSource = "Related Track"
            ),
            totalScore = 50f,
            primaryReason = FlowReason(FlowReasonType.GENRE_MATCH, "Match")
        )

        val result = optimizer.buildOptimizedQueue(
            existingItems = listOf(manualItem),
            rankedCandidates = listOf(flowCandidate),
            generationId = 1L,
            config = FlowConfig(targetQueueSize = 5)
        )

        assertEquals(2, result.size)
        assertEquals("manual1", result[0].mediaMetadata.id)
        assertEquals(ItemSource.MANUAL, result[0].source)
        assertEquals("flow1", result[1].mediaMetadata.id)
        assertEquals(ItemSource.FLOW, result[1].source)
    }
}
