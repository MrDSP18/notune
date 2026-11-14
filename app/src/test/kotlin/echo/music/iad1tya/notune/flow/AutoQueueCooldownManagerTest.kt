package echo.music.iad1tya.notune.flow

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AutoQueueCooldownManagerTest {

    private lateinit var cooldownManager: AutoQueueCooldownManager

    @Before
    fun setUp() {
        cooldownManager = AutoQueueCooldownManager(null)
        cooldownManager.clearAll()
    }

    @Test
    fun testTrackIsEligibleInitially() {
        assertTrue(cooldownManager.isEligibleForAutoAdd("track_1"))
        assertEquals(0L, cooldownManager.getRemainingCooldownMs("track_1"))
    }

    @Test
    fun testTrackEnters24HourCooldownUponAutoAdd() {
        val startTime = 1_000_000_000L
        cooldownManager.recordAutoAdded("track_1", timestampMs = startTime)

        // Immediately after: not eligible
        assertFalse(cooldownManager.isEligibleForAutoAdd("track_1", currentTimeMs = startTime + 1000L))
        assertEquals(
            AutoQueueCooldownManager.COOLDOWN_DURATION_MS - 1000L,
            cooldownManager.getRemainingCooldownMs("track_1", currentTimeMs = startTime + 1000L)
        )

        // 12 hours later: still cooling down
        val twelveHours = 12 * 60 * 60 * 1000L
        assertFalse(cooldownManager.isEligibleForAutoAdd("track_1", currentTimeMs = startTime + twelveHours))

        // 23 hours and 59 minutes later: still cooling down
        val almost24Hours = (24 * 60 * 60 * 1000L) - 60_000L
        assertFalse(cooldownManager.isEligibleForAutoAdd("track_1", currentTimeMs = startTime + almost24Hours))

        // Exactly 24 hours later: eligible again!
        val exactly24Hours = 24 * 60 * 60 * 1000L
        assertTrue(cooldownManager.isEligibleForAutoAdd("track_1", currentTimeMs = startTime + exactly24Hours))
        assertEquals(0L, cooldownManager.getRemainingCooldownMs("track_1", currentTimeMs = startTime + exactly24Hours))

        // 25 hours later: eligible!
        assertTrue(cooldownManager.isEligibleForAutoAdd("track_1", currentTimeMs = startTime + exactly24Hours + 3600_000L))
    }

    @Test
    fun testBulkRecordingAndPruning() {
        val baseTime = 1_000_000_000L
        val tracks = listOf("song_a", "song_b", "song_c")
        cooldownManager.recordAutoAdded(tracks, timestampMs = baseTime)

        for (id in tracks) {
            assertFalse(cooldownManager.isEligibleForAutoAdd(id, currentTimeMs = baseTime + 1000L))
        }

        // Before 24h prune does not remove
        cooldownManager.pruneExpired(currentTimeMs = baseTime + 10_000L)
        assertEquals(3, cooldownManager.getActiveCooldownTrackIds(currentTimeMs = baseTime + 10_000L).size)

        // After 24h prune clears expired entries
        val after24h = baseTime + (25 * 60 * 60 * 1000L)
        cooldownManager.pruneExpired(currentTimeMs = after24h)
        assertTrue(cooldownManager.getActiveCooldownTrackIds(currentTimeMs = after24h).isEmpty())
    }
}
