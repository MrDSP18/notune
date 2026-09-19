
package echo.music.iad1tya.notune.ai

import android.content.Context
import com.music.echo.notune.identity.NotuneAccountRepository
import echo.music.iad1tya.db.MusicDatabase
import echo.music.iad1tya.notune.ai.ToolCall
import echo.music.iad1tya.notune.ai.tools.AiToolManager
import echo.music.iad1tya.playback.PlayerConnection
import echo.music.iad1tya.playback.PlayerConnectionManager
import echo.music.iad1tya.repository.LocalMediaRepository
import echo.music.iad1tya.repository.MessagingRepository
import echo.music.iad1tya.repository.RoomRepository
import echo.music.iad1tya.repository.SocialRepository
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import timber.log.Timber

class AiToolExecutionTest {

    private val context = mockk<Context>(relaxed = true)
    private val database = mockk<MusicDatabase>()
    private val aiSuiteManager = mockk<echo.music.iad1tya.notune.ai.suite.AiSuiteManager>()
    private val roomRepository = mockk<RoomRepository>()
    private val socialRepository = mockk<SocialRepository>()
    private val localMediaRepository = mockk<LocalMediaRepository>()
    private val messagingRepository = mockk<MessagingRepository>()
    private val playerConnectionManager = mockk<PlayerConnectionManager>()
    private val accountRepository = mockk<NotuneAccountRepository>()
    private val lyricsHelper = mockk<echo.music.iad1tya.lyrics.LyricsHelper>(relaxed = true)
    private val playerConnection = mockk<PlayerConnection>()

    private lateinit var toolManager: AiToolManager

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        toolManager = AiToolManager(
            context, database, aiSuiteManager, roomRepository, socialRepository,
            localMediaRepository, messagingRepository, playerConnectionManager, accountRepository
        )
        toolManager.playerConnection = playerConnection
    }


    @Test
    fun `test pause_music tool execution`() = runTest {
        coEvery { playerConnection.pause() } just Runs
        
        val call = ToolCall(functionName = "pause_music", arguments = emptyMap())
        val result = toolManager.executeTool(call)
        
        assertTrue("Music paused" in result)
        coVerify { playerConnection.pause() }
    }

    @Test
    fun `test resume_music tool execution`() = runTest {
        coEvery { playerConnection.play() } just Runs
        
        val call = ToolCall(functionName = "resume_music", arguments = emptyMap())
        val result = toolManager.executeTool(call)
        
        assertTrue("Music resumed" in result)
        coVerify { playerConnection.play() }
    }

    @Test
    fun `test tool failure reporting (NO_FABRICATION)`() = runTest {
        // Disconnect player
        toolManager.playerConnection = null
        every { playerConnectionManager.playerConnection.value } returns null
        
        val call = ToolCall(functionName = "pause_music", arguments = emptyMap())
        val result = toolManager.executeTool(call)
        
        assertTrue("Error: Player not connected" in result)
    }
}
