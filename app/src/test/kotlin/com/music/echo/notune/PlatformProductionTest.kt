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
}
