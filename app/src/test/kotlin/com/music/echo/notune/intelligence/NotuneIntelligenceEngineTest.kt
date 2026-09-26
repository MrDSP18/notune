package com.music.echo.notune.intelligence

import com.music.echo.notune.intelligence.context.ContextEngine
import com.music.echo.notune.intelligence.explanation.RecommendationExplanation
import com.music.echo.notune.intelligence.explanation.TasteExplanation
import com.music.echo.notune.intelligence.feedback.FeedbackProcessor
import com.music.echo.notune.intelligence.feedback.RewardCalculator
import com.music.echo.notune.intelligence.feedback.UserEvent
import com.music.echo.notune.intelligence.musicbrain.EnergyEngine
import com.music.echo.notune.intelligence.musicbrain.LanguageEngine
import com.music.echo.notune.intelligence.musicbrain.MoodEngine
import com.music.echo.notune.intelligence.musicbrain.MusicBrain
import com.music.echo.notune.intelligence.musicbrain.SimilarityEngine
import com.music.echo.notune.intelligence.musicbrain.TrackEmbedding
import com.music.echo.notune.intelligence.personalization.PreferenceDecay
import com.music.echo.notune.intelligence.personalization.PreferenceLearner
import com.music.echo.notune.intelligence.personalization.TasteProfileStore
import com.music.echo.notune.intelligence.personalization.TeachNotuneEngine
import com.music.echo.notune.intelligence.queue.AdaptiveQueueEngine
import com.music.echo.notune.intelligence.queue.QueueTrack
import com.music.echo.notune.intelligence.queue.RepetitionController
import com.music.echo.notune.intelligence.queue.TransitionScorer
import com.music.echo.notune.intelligence.recommendation.ExplorationEngine
import com.music.echo.notune.intelligence.recommendation.RecommendationCandidate
import com.music.echo.notune.intelligence.recommendation.RecommendationEngine
import com.music.echo.notune.intelligence.search.PersonalizedSearchEngine
import com.music.echo.notune.intelligence.search.SearchCandidate
import com.music.echo.notune.intelligence.search.SearchIntentParser
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

        val similarityEngine = SimilarityEngine()
        val moodEngine = MoodEngine()
        val energyEngine = EnergyEngine()
        val languageEngine = LanguageEngine()
        val musicBrain = MusicBrain(similarityEngine, moodEngine, energyEngine, languageEngine)

        val intentParser = SearchIntentParser()
        val searchEngine = PersonalizedSearchEngine(intentParser, musicBrain, tasteProfileStore)

        val contextEngine = ContextEngine()
        val transitionScorer = TransitionScorer()
        val repetitionController = RepetitionController()
        val adaptiveQueueEngine = AdaptiveQueueEngine(musicBrain, transitionScorer, repetitionController, tasteProfileStore)

        val rewardCalculator = RewardCalculator()
        val feedbackProcessor = FeedbackProcessor(rewardCalculator, preferenceLearner, tasteProfileStore, teachNotuneEngine)

        val explorationEngine = ExplorationEngine()
        val recommendationEngine = RecommendationEngine(explorationEngine, musicBrain, tasteProfileStore)

        val recommendationExplanation = RecommendationExplanation()
        val tasteExplanation = TasteExplanation()

        intelligenceEngine = NotuneIntelligenceEngine(
            tasteProfileStore = tasteProfileStore,
            searchEngine = searchEngine,
            contextEngine = contextEngine,
            musicBrain = musicBrain,
            adaptiveQueueEngine = adaptiveQueueEngine,
            feedbackProcessor = feedbackProcessor,
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
    fun `test adaptive queue reordering on skips`() {
        val track1 = QueueTrack("t1", "Song 1", "Artist A", TrackEmbedding("t1", "Song 1", "Artist A", energy = 0.7f))
        val track2 = QueueTrack("t2", "Song 2", "Artist B", TrackEmbedding("t2", "Song 2", "Artist B", energy = 0.65f))
        val track3 = QueueTrack("t3", "Song 3", "Artist C", TrackEmbedding("t3", "Song 3", "Artist C", energy = 0.2f))

        intelligenceEngine.updateQueue(track1, listOf(track2, track3))

        // Record skip event
        intelligenceEngine.recordFeedback(UserEvent.Skip("t1", "Song 1", "Artist A", playedDurationSec = 5.0f))

        val state = intelligenceEngine.adaptiveQueueEngine.queueState.value
        assertEquals(1, intelligenceEngine.getCurrentDna().sessionTaste.consecutiveSkips)
    }

    @Test
    fun `test teach rule compilation and control center summary`() {
        val rule = intelligenceEngine.teachRule("Don't play sad songs when I'm working")
        assertNotNull(rule)

        val summary = intelligenceEngine.getControlCenterSummary()
        assertTrue(summary.activeRulesCount >= 1)
    }
}
