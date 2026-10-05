package com.music.echo.notune

import com.music.echo.notune.ai.core.NoAiEngine
import com.music.echo.notune.ai.core.NoAiModelRegistry
import com.music.echo.notune.ai.core.RuleBasedRuntime
import com.music.echo.notune.ai.music.MusicBrain
import com.music.echo.notune.ai.music.TrackFeatures
import com.music.echo.notune.ai.tools.CommandRouter
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NoAiArchitectureTest {

    @Test
    fun testModelRegistryDefaults() {
        val models = NoAiModelRegistry.Models
        assertEquals(3, models.size)
        assertEquals("no_ai_lite", models.first().id)
        assertTrue(models.first().isInstalled)
    }

    @Test
    fun testCommandRouterToolDispatch() = runBlocking {
        val result = CommandRouter.dispatch("playback", mapOf("action" to "play"))
        assertTrue(result.success)
        assertEquals("playback_play", result.actionName)

        val flowResult = CommandRouter.dispatch("flow", mapOf("vibe" to "Energy"))
        assertTrue(flowResult.success)
        assertEquals("start_flow", flowResult.actionName)

        val searchResult = CommandRouter.dispatch("search", mapOf("query" to "A.R. Rahman"))
        assertTrue(searchResult.success)
        assertEquals("A.R. Rahman", searchResult.data["query"])
    }

    @Test
    fun testRuleBasedRuntimeNaturalLanguageParsing() = runBlocking {
        val runtime = RuleBasedRuntime()
        
        val energyResult = runtime.processPrompt("Play something energetic for workout", "")
        assertTrue(energyResult.rawResponse.contains("NØ AI Lite"))
        assertEquals("no_ai_lite", energyResult.modelUsed)

        val chillResult = runtime.processPrompt("Give me chill relaxing music", "")
        assertTrue(chillResult.rawResponse.contains("NØ FLOW"))

        val lyricsResult = runtime.processPrompt("Show lyrics for this track", "")
        assertTrue(lyricsResult.rawResponse.contains("Lyrics action"))
    }

    @Test
    fun testMusicBrainDeterministicScoring() {
        val current = TrackFeatures("1", "Munbe Vaa", "A.R. Rahman", "Melody", "Tamil", 0.6f, 0.7f, 90f, 2006)
        val candidate = TrackFeatures("2", "Nira", "Sid Sriram", "Melody", "Tamil", 0.65f, 0.72f, 92f, 2020)

        val scoreResult = MusicBrain.scoreCandidate(candidate, current, userTasteLanguage = "Tamil")
        assertNotNull(scoreResult)
        assertTrue(scoreResult.matchPercentage > 70)
        assertEquals(1.0f, scoreResult.tasteScore, 0.01f)
    }
}
