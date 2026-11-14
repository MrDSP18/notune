package echo.music.iad1tya.notune.ai.engine

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.test.core.app.ApplicationProvider
import com.music.echo.notune.privacy.PrivateSessionManager
import echo.music.iad1tya.constants.AiPersonalizationKey
import echo.music.iad1tya.notune.ai.AiEngine
import echo.music.iad1tya.notune.ai.AiResponse
import echo.music.iad1tya.notune.ai.safety.ToolGuard
import echo.music.iad1tya.notune.ai.tools.AiToolManager
import echo.music.iad1tya.utils.dataStore
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File

@RunWith(RobolectricTestRunner::class)
class NoCoreOrchestratorPrivacyTest {

    companion object {
        @BeforeClass
        @JvmStatic
        fun setupConscryptWorkdir() {
            val workdir = File("build/tmp/conscrypt").apply { mkdirs() }
            System.setProperty("org.conscrypt.native.workdir", workdir.absolutePath)
        }
    }

    private lateinit var context: Context
    private val aiEngine = mockk<AiEngine>()
    private val toolManager = mockk<AiToolManager>(relaxed = true)
    private val mindMemory = mockk<NoMindMemory>(relaxed = true)
    private val dnaEngine = mockk<NoDnaEngine>()
    private val guardSafety = NoGuardSafety(ToolGuard())
    private val privateSessionManager = mockk<PrivateSessionManager>()
    private val isPrivateSessionFlow = MutableStateFlow(false)

    private lateinit var orchestrator: NoCoreOrchestrator

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
        every { privateSessionManager.isPrivateSession } returns isPrivateSessionFlow
        every { mindMemory.memoryFacts } returns flowOf(listOf("Loves 80s synth", "Dislikes country"))
        coEvery { dnaEngine.getDnaSummary() } returns "Synthwave 70%, Ambient 30%"
        coEvery {
            aiEngine.runNeuralMatrixLoop(
                prompt = any(),
                systemInstruction = any(),
                history = any(),
                toolExecutor = any()
            )
        } returns Result.success(AiResponse(text = "Playing music now", modelId = "mock-model"))

        orchestrator = NoCoreOrchestrator(
            context = context,
            aiEngine = aiEngine,
            toolManager = toolManager,
            mindMemory = mindMemory,
            dnaEngine = dnaEngine,
            guardSafety = guardSafety,
            privateSessionManager = privateSessionManager
        )
    }

    @Test
    fun testPrivateSessionSuppressesDnaMemoriesAndLearning() {
        runBlocking {
            isPrivateSessionFlow.value = true
            context.dataStore.edit { it[AiPersonalizationKey] = true }

            val capturedInstruction = slot<String>()
            coEvery {
                aiEngine.runNeuralMatrixLoop(
                    prompt = any(),
                    systemInstruction = capture(capturedInstruction),
                    history = any(),
                    toolExecutor = any()
                )
            } returns Result.success(AiResponse(text = "Understood", modelId = "mock-model"))

            val prompt = "I like listening to retrowave synth tracks"
            orchestrator.processUserRequest(prompt)

            // Verify system instruction lacks private DNA & memory facts
            assertTrue(capturedInstruction.captured.contains("Default / Unpersonalized (Privacy Mode Active)"))
            assertFalse(capturedInstruction.captured.contains("Synthwave 70%"))
            assertFalse(capturedInstruction.captured.contains("Loves 80s synth"))

            // Verify mindMemory does not learn facts
            coVerify(exactly = 0) { mindMemory.rememberFact(any()) }
        }
    }

    @Test
    fun testPersonalizationDisabledSuppressesDnaAndLearning() {
        runBlocking {
            isPrivateSessionFlow.value = false
            context.dataStore.edit { it[AiPersonalizationKey] = false }

            val capturedInstruction = slot<String>()
            coEvery {
                aiEngine.runNeuralMatrixLoop(
                    prompt = any(),
                    systemInstruction = capture(capturedInstruction),
                    history = any(),
                    toolExecutor = any()
                )
            } returns Result.success(AiResponse(text = "Understood", modelId = "mock-model"))

            val prompt = "I like dark synth music"
            orchestrator.processUserRequest(prompt)

            assertTrue(capturedInstruction.captured.contains("Default / Unpersonalized (Privacy Mode Active)"))
            assertFalse(capturedInstruction.captured.contains("Synthwave 70%"))
            coVerify(exactly = 0) { mindMemory.rememberFact(any()) }
        }
    }

    @Test
    fun testNormalModeInjectsDnaAndLearnsPreferences() {
        runBlocking {
            isPrivateSessionFlow.value = false
            context.dataStore.edit { it[AiPersonalizationKey] = true }

            val capturedInstruction = slot<String>()
            coEvery {
                aiEngine.runNeuralMatrixLoop(
                    prompt = any(),
                    systemInstruction = capture(capturedInstruction),
                    history = any(),
                    toolExecutor = any()
                )
            } returns Result.success(AiResponse(text = "Understood", modelId = "mock-model"))

            val prompt = "I love progressive house music"
            orchestrator.processUserRequest(prompt)

            assertTrue(capturedInstruction.captured.contains("Synthwave 70%"))
            assertTrue(capturedInstruction.captured.contains("Loves 80s synth"))
            coVerify(exactly = 1) { mindMemory.rememberFact(prompt) }
        }
    }
}
