package com.music.echo.notune.intelligence

import com.music.echo.notune.intelligence.context.ContextEngine
import com.music.echo.notune.intelligence.explanation.RecommendationExplanation
import com.music.echo.notune.intelligence.explanation.TasteExplanation
import com.music.echo.notune.intelligence.feedback.FeedbackProcessor
import com.music.echo.notune.intelligence.feedback.RewardCalculator
import com.music.echo.notune.intelligence.feedback.SkipReasonAnalyzer
import com.music.echo.notune.intelligence.feedback.UserEvent
import com.music.echo.notune.intelligence.musicbrain.EnergyEngine
import com.music.echo.notune.intelligence.musicbrain.LanguageEngine
import com.music.echo.notune.intelligence.musicbrain.MoodEngine
import com.music.echo.notune.intelligence.musicbrain.MusicBrain
import com.music.echo.notune.intelligence.musicbrain.MusicStateEngine
import com.music.echo.notune.intelligence.musicbrain.SimilarityEngine
import com.music.echo.notune.intelligence.musicbrain.TrackEmbedding
import com.music.echo.notune.intelligence.personalization.InstantOverrideAction
import com.music.echo.notune.intelligence.personalization.PreferenceDecay
import com.music.echo.notune.intelligence.personalization.PreferenceLearner
import com.music.echo.notune.intelligence.personalization.SessionOverrideEngine
import com.music.echo.notune.intelligence.personalization.TasteProfileStore
import com.music.echo.notune.intelligence.personalization.TeachNotuneEngine
import com.music.echo.notune.intelligence.queue.AdaptiveQueueEngine
import com.music.echo.notune.intelligence.queue.NotuneFlowMode
import com.music.echo.notune.intelligence.queue.QueueTrack
import com.music.echo.notune.intelligence.queue.RepetitionController
import com.music.echo.notune.intelligence.queue.TransitionScorer
import com.music.echo.notune.intelligence.recommendation.ExplorationEngine
import com.music.echo.notune.intelligence.recommendation.RecommendationEngine
import com.music.echo.notune.intelligence.search.PersonalizedSearchEngine
import com.music.echo.notune.intelligence.search.SearchCandidate
import com.music.echo.notune.intelligence.search.SearchIntentParser
import com.music.echo.notune.intelligence.search.SearchQualityGate
import com.music.echo.notune.intelligence.session.PersonalMusicSession
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RealAppIntegrationTest {

    private lateinit var intelligenceEngine: NotuneIntelligenceEngine
    private lateinit var personalMusicSession: PersonalMusicSession

    @Before
    fun setUp() {
        val preferenceDecay = PreferenceDecay()
        val tasteProfileStore = TasteProfileStore(preferenceDecay)
        val preferenceLearner = PreferenceLearner(tasteProfileStore)
        val teachNotuneEngine = TeachNotuneEngine()
        val sessionOverrideEngine = SessionOverrideEngine(tasteProfileStore)

        val similarityEngine = SimilarityEngine()
        val moodEngine = MoodEngine()
        val energyEngine = EnergyEngine()
        val languageEngine = LanguageEngine()
        val musicBrain = MusicBrain(similarityEngine, moodEngine, energyEngine, languageEngine)
        val musicStateEngine = MusicStateEngine()

        val intentParser = SearchIntentParser()
        val searchQualityGate = SearchQualityGate()
        val searchEngine = PersonalizedSearchEngine(intentParser, musicBrain, tasteProfileStore)

        val contextEngine = ContextEngine()
        val transitionScorer = TransitionScorer()
        val repetitionController = RepetitionController()
        val adaptiveQueueEngine = AdaptiveQueueEngine(musicBrain, transitionScorer, repetitionController, tasteProfileStore)

        val rewardCalculator = RewardCalculator()
        val feedbackProcessor = FeedbackProcessor(rewardCalculator, preferenceLearner, tasteProfileStore, teachNotuneEngine)
        val skipReasonAnalyzer = SkipReasonAnalyzer()

        val explorationEngine = ExplorationEngine()
        val recommendationEngine = RecommendationEngine(explorationEngine, musicBrain, tasteProfileStore)

        val recommendationExplanation = RecommendationExplanation()
        val tasteExplanation = TasteExplanation()
        personalMusicSession = PersonalMusicSession()

        val dnaInitializer = com.music.echo.notune.intelligence.personalization.MusicDnaInitializer(null, null, null)

        intelligenceEngine = NotuneIntelligenceEngine(
            tasteProfileStore = tasteProfileStore,
            musicDnaInitializer = dnaInitializer,
            searchEngine = searchEngine,
            searchQualityGate = searchQualityGate,
            contextEngine = contextEngine,
            musicStateEngine = musicStateEngine,
            musicBrain = musicBrain,
            adaptiveQueueEngine = adaptiveQueueEngine,
            feedbackProcessor = feedbackProcessor,
            skipReasonAnalyzer = skipReasonAnalyzer,
            sessionOverrideEngine = sessionOverrideEngine,
            teachNotuneEngine = teachNotuneEngine,
            recommendationEngine = recommendationEngine,
            recommendationExplanation = recommendationExplanation,
            tasteExplanation = tasteExplanation
        )
    }

    @Test
    fun `test real music session track transitions and live taste snapshot`() {
        val track1 = QueueTrack("rt1", "Vennilave", "Hariharan", TrackEmbedding("rt1", "Vennilave", "Hariharan", energy = 0.35f, genre = "Tamil Melody", language = "Tamil"))
        personalMusicSession.onTrackStarted(track1)

        val snapshot = personalMusicSession.liveTasteSnapshot.value
        assertEquals("Hariharan", snapshot.primaryArtist)
        assertEquals("Tamil Melody", snapshot.primaryGenre)
        assertEquals(0.35f, snapshot.currentEnergyTarget, 0.05f)
    }

    @Test
    fun `test quality gate rejects unplayable candidates`() {
        val unplayableCandidate = SearchCandidate(
            id = "unplay_1",
            title = "Broken Song",
            artistName = "Broken Artist",
            embedding = TrackEmbedding("unplay_1", "Broken Song", "Broken Artist"),
            isAvailableOfflineOrStream = false
        )

        val decision = intelligenceEngine.evaluateQualityGate(unplayableCandidate)
        assertTrue(!decision.isPassed)
        assertEquals(com.music.echo.notune.intelligence.search.GateResultStatus.REJECT_UNPLAYABLE, decision.status)
    }

    @Test
    fun `test user locked track preservation during skip reorder`() {
        val currentTrack = QueueTrack("ct1", "Now Playing", "Artist A", TrackEmbedding("ct1", "Now Playing", "Artist A"))
        val userLockedTrack = QueueTrack("lock1", "User Selected Song", "Artist B", TrackEmbedding("lock1", "User Selected Song", "Artist B"), isLockedByUser = true)
        val dynamicTrack = QueueTrack("dyn1", "Auto Generated", "Artist C", TrackEmbedding("dyn1", "Auto Generated", "Artist C"))

        intelligenceEngine.updateQueue(currentTrack, listOf(dynamicTrack, userLockedTrack))

        val state = intelligenceEngine.adaptiveQueueEngine.queueState.value
        assertEquals("lock1", state.upcomingQueue.first().id)
        assertTrue(state.upcomingQueue.first().isLockedByUser)
    }

    @Test
    fun `test instant session override actions`() {
        intelligenceEngine.applyInstantOverride(InstantOverrideAction.SURPRISE_ME)
        val dna = intelligenceEngine.getCurrentDna()
        assertEquals(0.80f, dna.discoveryProfile.explorationRate, 0.05f)
    }
}
