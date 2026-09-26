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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NotuneIntelligenceEngineTest {

    private lateinit var intelligenceEngine: NotuneIntelligenceEngine

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

        intelligenceEngine = NotuneIntelligenceEngine(
            tasteProfileStore = tasteProfileStore,
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
    fun `test personalized search with natural language intent`() {
        val candidates = listOf(
            SearchCandidate(
                id = "s1",
                title = "Munbe Vaa",
                artistName = "A.R. Rahman",
                embedding = TrackEmbedding("s1", "Munbe Vaa", "A.R. Rahman", energy = 0.4f, moodValence = 0.6f, language = "Tamil", genre = "Tamil Pop")
            ),
            SearchCandidate(
                id = "s2",
                title = "Heavy Rock",
                artistName = "Band X",
                embedding = TrackEmbedding("s2", "Heavy Rock", "Band X", energy = 0.95f, moodValence = 0.8f, language = "English", genre = "Rock")
            )
        )

        val results = intelligenceEngine.search("peaceful Tamil songs for studying", candidates)
        assertTrue(results.isNotEmpty())
        assertEquals("Munbe Vaa", results.first().candidate.title)
    }

    @Test
    fun `test user locked tracks preserved in queue`() {
        val track1 = QueueTrack("t1", "User Lock", "Artist A", TrackEmbedding("t1", "User Lock", "Artist A"), isLockedByUser = true)
        val track2 = QueueTrack("t2", "Auto Rec", "Artist B", TrackEmbedding("t2", "Auto Rec", "Artist B"))

        intelligenceEngine.updateQueue(null, listOf(track2, track1))

        val state = intelligenceEngine.adaptiveQueueEngine.queueState.value
        assertEquals("t1", state.upcomingQueue.first().id)
        assertTrue(state.upcomingQueue.first().isLockedByUser)
    }

    @Test
    fun `test instant session override action`() {
        intelligenceEngine.applyInstantOverride(InstantOverrideAction.SURPRISE_ME)
        val dna = intelligenceEngine.getCurrentDna()
        assertEquals(0.80f, dna.discoveryProfile.explorationRate, 0.05f)
    }

    @Test
    fun `test set flow mode`() {
        intelligenceEngine.setFlowMode(NotuneFlowMode.DISCOVERY)
        val state = intelligenceEngine.adaptiveQueueEngine.queueState.value
        assertEquals(NotuneFlowMode.DISCOVERY, state.flowMode)
    }
}
