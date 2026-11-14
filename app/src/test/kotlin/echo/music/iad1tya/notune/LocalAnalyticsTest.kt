package echo.music.iad1tya.notune

import com.music.echo.notune.AnalyticsManager
import com.music.echo.notune.privacy.PrivateSessionManager
import echo.music.iad1tya.db.MusicDatabase
import echo.music.iad1tya.models.AppEvent
import echo.music.iad1tya.models.MediaMetadata
import echo.music.iad1tya.repository.EventRepository
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LocalAnalyticsTest {

    private val eventRepository = mockk<EventRepository>()
    private val database = mockk<MusicDatabase>(relaxed = true)
    private val privateSessionManager = mockk<PrivateSessionManager>()
    private val events = MutableSharedFlow<AppEvent>()
    private val testScope = TestScope()
    private lateinit var analyticsManager: AnalyticsManager

    @Before
    fun setUp() {
        every { eventRepository.events } returns events
        every { privateSessionManager.shouldRecordHistoryAndPersonalization() } returns true
        analyticsManager = AnalyticsManager(eventRepository, database, privateSessionManager, testScope)
    }

    @Test
    fun `test local song completion updates database`() = testScope.runTest {
        try {
            val localId = "content://media/123"
            val metadata = MediaMetadata(
                id = localId,
                title = "Local Song",
                artists = emptyList(),
                duration = 180,
                thumbnailUrl = null
            )
        
            val event = AppEvent.PlaybackCompleted(metadata, 180000L)
        
            events.emit(event)
        
            // Wait for coroutine in AnalyticsManager to process
            testScope.testScheduler.advanceUntilIdle()
        
            // Verify database query block was called
            val dbBlock = slot<MusicDatabase.() -> Unit>()
            verify { database.query(capture(dbBlock)) }
        
            // Run the captured block on the mock
            dbBlock.captured.invoke(database)
        
            verify { database.incrementTotalPlayTime(localId, 180000L) }
            verify { database.insert(any<echo.music.iad1tya.db.entities.Event>()) }
        } finally {
            analyticsManager.release()
        }
    }

    @Test
    fun `test private session suppresses database updates`() = testScope.runTest {
        try {
            every { privateSessionManager.shouldRecordHistoryAndPersonalization() } returns false
            clearMocks(database, answers = false, recordedCalls = true)

            val localId = "content://media/private_123"
            val metadata = MediaMetadata(
                id = localId,
                title = "Private Song",
                artists = emptyList(),
                duration = 200,
                thumbnailUrl = null
            )

            val event = AppEvent.PlaybackCompleted(metadata, 200000L)
            events.emit(event)

            testScope.testScheduler.advanceUntilIdle()

            // Verify database query was never invoked
            verify(exactly = 0) { database.query(any()) }
            verify(exactly = 0) { database.incrementTotalPlayTime(any(), any()) }
        } finally {
            analyticsManager.release()
        }
    }
}
