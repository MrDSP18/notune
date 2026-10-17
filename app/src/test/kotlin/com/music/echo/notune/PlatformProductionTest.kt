package com.music.echo.notune

import com.music.echo.notune.ai.diagnostics.NoAiDiagnostics
import com.music.echo.notune.ai.models.NoAiModelManager
import com.music.echo.notune.ai.runtime.LiteRtLmRuntime
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PlatformProductionTest {

    @Test
    fun testLiteRtLmRuntimeInference() = runBlocking {
        val runtime = LiteRtLmRuntime()
        assertTrue(runtime.isReady)

        val result = runtime.processPrompt("Play something energetic for workout", "")
        assertNotNull(result)
        assertTrue(result.rawResponse.contains("LiteRT-LM"))
        assertEquals("no_fast_gemma_1b", result.modelUsed)
    }

    @Test
    fun testNoAiDiagnosticsRun() = runBlocking {
        val report = NoAiDiagnostics.runFullDiagnostics()
        assertTrue(report.isSystemHealthy)
        assertEquals(4, report.items.size)
        assertTrue(report.items.all { it.passed })
    }

    @Test
    fun testNoAiModelManagerChecksumVerification() {
        val dummyData = "NØTUNE Gemma 3 1B Model Content".toByteArray(Charsets.UTF_8)
        // Verify method execution returns boolean
        val isValid = NoAiModelManager.verifyChecksum(dummyData, "")
        assertTrue(isValid)

        val state = NoAiModelManager.getModelState("no_ai_lite")
        assertNotNull(state)
        assertEquals("NØ AI Lite", state?.model?.name)
    }

    @Test
    fun testListenTogetherReconnectJitterBoundedBackoff() {
        val mockContext = io.mockk.mockk<android.content.Context>(relaxed = true)
        val client = echo.music.iad1tya.listentogether.ListenTogetherClient(
            context = mockContext
        )
        // Test attempt 1 with zero jitter
        val delay1Min = client.calculateBackoffDelay(attempt = 1, randomFactor = 0.0)
        assertTrue("Attempt 1 min delay should be >= 1000ms", delay1Min >= 1000L)

        // Test attempt 1 with max jitter
        val delay1Max = client.calculateBackoffDelay(attempt = 1, randomFactor = 1.0)
        assertTrue("Attempt 1 max delay with jitter should be > min delay", delay1Max > delay1Min)

        // Test exponential capping
        val delayMaxAttempt = client.calculateBackoffDelay(attempt = 10, randomFactor = 0.0)
        assertTrue("Capped delay should not exceed 120s", delayMaxAttempt <= 120000L)
    }
}
