package echo.music.iad1tya.notune.flow

import androidx.media3.common.MediaItem
import echo.music.iad1tya.models.MediaMetadata
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AttributeBasedQueueRecommenderTest {

    private lateinit var cooldownManager: AutoQueueCooldownManager
    private lateinit var recommender: AttributeBasedQueueRecommender

    @Before
    fun setUp() {
        cooldownManager = AutoQueueCooldownManager(null)
        cooldownManager.clearAll()
        recommender = AttributeBasedQueueRecommender(cooldownManager)
    }

    private fun createMediaItem(id: String, title: String, artist: String, album: String = ""): MediaItem {
        return MediaItem.Builder()
            .setMediaId(id)
            .setMediaMetadata(
                androidx.media3.common.MediaMetadata.Builder()
                    .setTitle(title)
                    .setArtist(artist)
                    .setAlbumTitle(album)
                    .build()
            )
            .build()
    }

    @Test
    fun testAttributeExtractionIdentifiesDirectorsActorsAndAuthors() {
        val song = MediaMetadata(
            id = "s1",
            title = "Aalaporaan Thamizhan (Tamil) (From Mersal)",
            artists = listOf(
                MediaMetadata.Artist("a1", "A.R. Rahman"),
                MediaMetadata.Artist("a2", "Kailash Kher")
            ),
            duration = 340,
            album = MediaMetadata.Album("al1", "Mersal")
        )

        val attrs = recommender.extractAttributes(song)
        assertTrue(attrs.musicDirectors.contains("a.r. rahman") || attrs.musicDirectors.contains("ar rahman") || attrs.musicDirectors.contains("rahman"))
        assertTrue(attrs.genres.contains("kollywood") || attrs.genres.contains("tamil") || attrs.genres.contains("thamizh"))
    }

    @Test
    fun testRecommenderScoresMatchingMusicDirectorHigher() {
        val currentTrack = MediaMetadata(
            id = "current",
            title = "Naa Ready",
            artists = listOf(MediaMetadata.Artist("a1", "Anirudh Ravichander"), MediaMetadata.Artist("a2", "Thalapathy Vijay")),
            duration = 240
        )
        val attrs = recommender.extractAttributes(currentTrack)

        val matchingDirectorCandidate = createMediaItem("c1", "Badass", "Anirudh Ravichander (Leo)")
        val unrelatedCandidate = createMediaItem("c2", "Random Track", "Unknown Indie Artist")

        val matchScore = recommender.scoreCandidate(attrs, matchingDirectorCandidate)
        val unrelatedScore = recommender.scoreCandidate(attrs, unrelatedCandidate)

        assertTrue(matchScore > unrelatedScore)
    }

    @Test
    fun testFilterAndRankQueueEnforcesDeduplicationAnd24HourCooldown() {
        val now = 1_000_000_000L
        val currentTrack = MediaMetadata(
            id = "current",
            title = "Chaleya",
            artists = listOf(MediaMetadata.Artist("a1", "Anirudh Ravichander")),
            duration = 200
        )

        val candidateAlreadyInQueue = createMediaItem("in_queue_id", "Zinda Banda", "Anirudh")
        val candidateCoolingDown = createMediaItem("cooldown_id", "Hukum", "Anirudh")
        val validCandidate1 = createMediaItem("valid_1", "Kaavaalaa", "Anirudh")
        val validCandidate2 = createMediaItem("valid_2", "Arabic Kuthu", "Anirudh")

        // Put candidateCoolingDown on 24-hour cooldown
        cooldownManager.recordAutoAdded("cooldown_id", timestampMs = now - 3600_000L) // 1 hour ago (< 24h)

        val activeQueueIds = setOf("current", "in_queue_id")

        val ranked = recommender.filterAndRankQueue(
            currentTrack = currentTrack,
            candidates = listOf(candidateAlreadyInQueue, candidateCoolingDown, validCandidate1, validCandidate2),
            activeQueueIds = activeQueueIds,
            currentTimeMs = now
        )

        val rankedIds = ranked.map { it.mediaId }

        assertFalse("Candidate already in queue must be rejected", rankedIds.contains("in_queue_id"))
        assertFalse("Candidate on 24-hour cooldown must be rejected", rankedIds.contains("cooldown_id"))
        assertTrue("Valid candidates must be accepted", rankedIds.contains("valid_1"))
        assertTrue("Valid candidates must be accepted", rankedIds.contains("valid_2"))
    }
}
