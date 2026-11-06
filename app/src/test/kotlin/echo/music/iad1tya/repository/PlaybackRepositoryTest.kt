
package echo.music.iad1tya.repository

import com.music.echo.repository.PlaybackRepositoryImpl
import echo.music.iad1tya.models.MediaMetadata
import echo.music.iad1tya.models.PlaybackState
import echo.music.iad1tya.models.TechnicalTelemetry
import echo.music.iad1tya.playback.PlayerConnection
import echo.music.iad1tya.playback.PlayerConnectionManager
import io.mockk.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import androidx.media3.common.Format
import androidx.media3.common.MimeTypes
import androidx.media3.exoplayer.ExoPlayer

@OptIn(ExperimentalCoroutinesApi::class)
class PlaybackRepositoryTest {

    private val testScope = TestScope()
    private val connectionManager = mockk<PlayerConnectionManager>()
    private val playerConnection = mockk<PlayerConnection>()

    /** Wire up the mock PlayerConnection with all StateFlows required by PlaybackRepositoryImpl. */
    private fun setupBaseConnectionMocks(
        audioSessionId: Int? = null,
        bufferedPosition: Long = 0L,
        playerVolume: Float = 1f,
        playerQueue: List<MediaMetadata> = emptyList(),
        currentMediaItemIndex: Int = -1,
    ) {
        every { playerConnection.mediaMetadata } returns MutableStateFlow(null)
        every { playerConnection.isPlaying } returns MutableStateFlow(false)
        every { playerConnection.position } returns MutableStateFlow(0L)
        every { playerConnection.duration } returns MutableStateFlow(0L)
        every { playerConnection.shuffleModeEnabled } returns MutableStateFlow(false)
        every { playerConnection.repeatMode } returns MutableStateFlow(0)
        every { playerConnection.technicalTelemetry } returns MutableStateFlow(TechnicalTelemetry())
        every { playerConnection.aiDjCommentary } returns MutableStateFlow(null)
        every { playerConnection.audioFormat } returns MutableStateFlow(null)

        // Thread-safe snapshot flows — no direct player access required by the repository.
        every { playerConnection.audioSessionId } returns MutableStateFlow(audioSessionId)
        every { playerConnection.bufferedPosition } returns MutableStateFlow(bufferedPosition)
        every { playerConnection.playerVolume } returns MutableStateFlow(playerVolume)
        every { playerConnection.playerQueue } returns MutableStateFlow(playerQueue)
        every { playerConnection.currentMediaItemIndex } returns MutableStateFlow(currentMediaItemIndex)
    }

    @Test
    fun `test technical telemetry mapping from audio format`() = testScope.runTest {
        val playerConnectionFlow = MutableStateFlow<PlayerConnection?>(playerConnection)
        every { connectionManager.playerConnection } returns playerConnectionFlow

        setupBaseConnectionMocks(audioSessionId = 123)

        val audioFormatFlow = MutableStateFlow<Format?>(null)
        every { playerConnection.audioFormat } returns audioFormatFlow

        val repository = PlaybackRepositoryImpl(connectionManager, backgroundScope)

        // Wait for flow to emit initial state
        testScope.testScheduler.advanceUntilIdle()

        // Update format
        val format = Format.Builder()
            .setSampleMimeType(MimeTypes.AUDIO_OPUS)
            .setAverageBitrate(128000)
            .setSampleRate(48000)
            .setChannelCount(2)
            .build()

        audioFormatFlow.value = format

        // Check mapping
        val state = repository.playbackState.first { it.telemetry.codec != null }
        assertEquals("OPUS", state.telemetry.codec)
        assertEquals(128000, state.telemetry.bitrate)
        assertEquals(48000, state.telemetry.sampleRate)
        assertEquals(2, state.telemetry.channelCount)
        assertEquals(123, state.telemetry.audioSessionId)
    }

    @Test
    fun `test AI DJ commentary mapping`() = testScope.runTest {
        val playerConnectionFlow = MutableStateFlow<PlayerConnection?>(playerConnection)
        every { connectionManager.playerConnection } returns playerConnectionFlow

        setupBaseConnectionMocks(audioSessionId = 456)

        val aiCommentaryFlow = MutableStateFlow<String?>(null)
        every { playerConnection.aiDjCommentary } returns aiCommentaryFlow

        val repository = PlaybackRepositoryImpl(connectionManager, backgroundScope)

        testScope.testScheduler.advanceUntilIdle()

        aiCommentaryFlow.value = "This is a futuristic vibe."

        val state = repository.playbackState.first { it.aiDjCommentary != null }
        assertEquals("This is a futuristic vibe.", state.aiDjCommentary)
    }
}
