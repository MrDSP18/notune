package com.music.echo.notune

import com.music.echo.notune.intelligence.musicbrain.*
import com.music.echo.notune.intelligence.personalization.PreferenceDecay
import com.music.echo.notune.intelligence.personalization.TasteProfileStore
import com.music.echo.notune.intelligence.queue.*
import com.music.echo.notune.intelligence.session.SessionSeed
import com.music.echo.notune.intelligence.session.SessionSeedManager
import com.music.echo.notune.intelligence.session.SessionSeedSourceType
import echo.music.iad1tya.notune.provider.UnifiedTrack
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AdaptiveFlowEngineTest {

    private lateinit var transitionScorer: TransitionScorer
    private lateinit var transitionEngine: TransitionEngine
    private lateinit var repetitionController: RepetitionController
    private lateinit var musicBrain: MusicBrain
    private lateinit var tasteProfileStore: TasteProfileStore
    private lateinit var sessionSeedManager: SessionSeedManager
    private lateinit var adaptiveQueueEngine: AdaptiveQueueEngine

    @Before
    fun setUp() {
        transitionScorer = TransitionScorer()
        transitionEngine = TransitionEngine(transitionScorer)
        repetitionController = RepetitionController()
        musicBrain = MusicBrain(
            similarityEngine = SimilarityEngine(),
            moodEngine = MoodEngine(),
            energyEngine = EnergyEngine(),
            languageEngine = LanguageEngine()
        )
        tasteProfileStore = TasteProfileStore(PreferenceDecay())
        sessionSeedManager = SessionSeedManager()
        adaptiveQueueEngine = AdaptiveQueueEngine(
            musicBrain = musicBrain,
            transitionScorer = transitionScorer,
            repetitionController = repetitionController,
            tasteProfileStore = tasteProfileStore,
            transitionEngine = transitionEngine,
            sessionSeedManager = sessionSeedManager
        )
    }

    @Test
    fun testSessionSeedCreation() {
        val track = UnifiedTrack(
            id = "seed_1",
            title = "Hukum",
            artist = "Anirudh Ravichander",
            album = "Jailer",
            providerName = "YouTube",
            providerTrackId = "yt_hukum"
        )
        val seed = sessionSeedManager.createTrackSeed(track, SessionSeedSourceType.TRACK)
        assertEquals("seed_1", seed.seedId)
        assertEquals("Hukum", seed.title)
        assertEquals("Anirudh Ravichander", seed.artist)
        assertEquals(SessionSeedSourceType.TRACK, seed.sourceType)
    }

    @Test
    fun testTransitionEngineDistanceAndFlow() {
        val trackA = TrackEmbedding(trackId = "a", title = "Hukum", artistName = "Anirudh", energy = 0.85f, tempoBpm = 128f, genre = "Tamil")
        val trackB = TrackEmbedding(trackId = "b", title = "Vathi Coming", artistName = "Anirudh", energy = 0.82f, tempoBpm = 126f, genre = "Tamil")

        val result = transitionEngine.evaluateTransition(trackA, trackB)
        assertTrue("Similar tracks should produce smooth transition", result.distance < 0.35f)
        assertTrue("Energy should be evaluated as smooth", result.isSmoothEnergy)
    }

    @Test
    fun testQueueTiersAndImmutability() {
        val currentTrack = QueueTrack(id = "current", title = "Current Playing", artistName = "Artist 1", embedding = TrackEmbedding(trackId = "current", title = "Current Playing", artistName = "Artist 1"))
        val upcoming = (1..15).map { index ->
            QueueTrack(
                id = "track_$index",
                title = "Upcoming Track $index",
                artistName = "Artist $index",
                embedding = TrackEmbedding(trackId = "track_$index", title = "Upcoming Track $index", artistName = "Artist $index")
            )
        }

        adaptiveQueueEngine.setQueue(currentTrack, upcoming)

        val tiers = adaptiveQueueEngine.queueTiers.value
        assertEquals("Current playing track should be in NOW tier", "current", tiers.now?.id)
        assertEquals("NEXT_3 tier should contain 3 tracks", 3, tiers.next3.size)
        assertEquals("BALANCED_5 tier should contain 5 tracks", 5, tiers.balanced5.size)
        assertEquals("DISCOVERY_5 tier should contain 5 tracks", 5, tiers.discovery5.size)
        assertTrue("Reserve pool should contain remaining tracks", tiers.reservePool.isNotEmpty())
    }

    @Test
    fun testWeightedSkipFeedbackAdjustment() {
        val skippedTrack = QueueTrack(
            id = "skip_1",
            title = "High Energy Song",
            artistName = "Artist X",
            embedding = TrackEmbedding(trackId = "skip_1", title = "High Energy Song", artistName = "Artist X", energy = 0.95f, tempoBpm = 160f)
        )

        val initialEnergy = sessionSeedManager.sessionProfile.value.currentEnergyTarget

        // Apply early skip feedback
        adaptiveQueueEngine.onFeedback(skippedTrack, FeedbackSignalType.EARLY_SKIP)

        val adjustedEnergy = sessionSeedManager.sessionProfile.value.currentEnergyTarget
        assertNotEquals("Energy target should adjust after early skip", initialEnergy, adjustedEnergy)
    }
}
