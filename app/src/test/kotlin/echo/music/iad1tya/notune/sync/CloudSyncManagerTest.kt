package echo.music.iad1tya.notune.sync

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.test.core.app.ApplicationProvider
import com.music.echo.notune.privacy.security.SecureStorageManager
import com.music.echo.notune.sync.CloudSyncManager
import com.music.echo.notune.sync.selectSyncBatch
import echo.music.iad1tya.constants.CloudPersonalizationKey
import echo.music.iad1tya.constants.PrivateSessionEnabledKey
import echo.music.iad1tya.repository.EventRepository
import echo.music.iad1tya.utils.dataStore
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File

@RunWith(RobolectricTestRunner::class)
class CloudSyncManagerTest {

    companion object {
        @BeforeClass
        @JvmStatic
        fun setupConscryptWorkdir() {
            val workdir = File("build/tmp/conscrypt").apply { mkdirs() }
            System.setProperty("org.conscrypt.native.workdir", workdir.absolutePath)
        }
    }

    private lateinit var context: Context
    private val secureStorageManager = mockk<SecureStorageManager>(relaxed = true)
    private val eventRepository = mockk<EventRepository>(relaxed = true)
    private val eventsFlow = kotlinx.coroutines.flow.MutableSharedFlow<echo.music.iad1tya.models.AppEvent>()

    @Before
    fun setUp() {
        runBlocking {
            context = ApplicationProvider.getApplicationContext<Context>()
            io.mockk.every { eventRepository.events } returns eventsFlow
            // Reset preferences to default state
            context.dataStore.edit {
                it[PrivateSessionEnabledKey] = false
                it[CloudPersonalizationKey] = true
            }
            val outbox = File(context.filesDir, "notune_sync_outbox.json")
            if (outbox.exists()) {
                outbox.delete()
            }
        }
    }

    @Test
    fun syncBatchNeverExceedsWorkerLimit() {
        val events = (0..100).toList()
        val batch = selectSyncBatch(events)

        assertEquals(99, batch.size)
        assertEquals(events.take(99), batch)
    }

    @Test
    fun testSyncEventPayloadStructure() {
        val payload = CloudSyncManager.SyncEventPayload(
            eventId = "evt_123",
            eventType = "TASTE_UPDATE",
            payloadJson = "{\"genre\":\"rock\",\"score\":0.85}"
        )

        assertEquals("evt_123", payload.eventId)
        assertEquals("TASTE_UPDATE", payload.eventType)
        assertTrue(payload.payloadJson.contains("rock"))
        assertTrue(payload.timestamp > 0)
    }

    @Test
    fun testNormalEnqueueingAppendsToQueueAndPersists() {
        val manager = CloudSyncManager(context, secureStorageManager, eventRepository)
        assertTrue(manager.isSyncPermitted())

        val enqueued = manager.enqueueSyncEvent(
            "SONG_FAVORITE_TOGGLED",
            "{\"songId\":\"song_1\",\"action\":\"LIKE\"}"
        )
        assertTrue(enqueued)
        assertEquals(1, manager.pendingQueueCount.value)

        val outboxFile = File(context.filesDir, "notune_sync_outbox.json")
        assertTrue(outboxFile.exists())
        val content = outboxFile.readText()
        assertTrue(content.contains("song_1"))
        assertTrue(content.contains("SONG_FAVORITE_TOGGLED"))
    }

    @Test
    fun testPrivateSessionSuppressesEventEnqueueing() = runBlocking {
        context.dataStore.edit {
            it[PrivateSessionEnabledKey] = true
        }

        val manager = CloudSyncManager(context, secureStorageManager, eventRepository)
        assertFalse(manager.isSyncPermitted())

        val enqueued = manager.enqueueSyncEvent(
            "PLAYBACK_HISTORY_LOGGED",
            "{\"songId\":\"private_song\",\"playTimeMs\":120000}"
        )
        assertFalse("Event must not be enqueued when private session is active", enqueued)
        assertEquals(0, manager.pendingQueueCount.value)

        val outboxFile = File(context.filesDir, "notune_sync_outbox.json")
        assertFalse("Outbox file must not contain private session events", outboxFile.exists())
    }

    @Test
    fun testCloudPersonalizationDisabledSuppressesEventEnqueueing() = runBlocking {
        context.dataStore.edit {
            it[CloudPersonalizationKey] = false
        }

        val manager = CloudSyncManager(context, secureStorageManager, eventRepository)
        assertFalse(manager.isSyncPermitted())

        val enqueued = manager.enqueueSyncEvent(
            "TASTE_UPDATE",
            "{\"genre\":\"jazz\"}"
        )
        assertFalse("Event must not be enqueued when cloud personalization is disabled", enqueued)
        assertEquals(0, manager.pendingQueueCount.value)
    }

    @Test
    fun testClearPendingOutboxEmptiesQueueAndDeletesFile() {
        val manager = CloudSyncManager(context, secureStorageManager, eventRepository)
        manager.enqueueSyncEvent("TEST_EVT", "{\"action\":\"test\"}")
        assertEquals(1, manager.pendingQueueCount.value)

        val outboxFile = File(context.filesDir, "notune_sync_outbox.json")
        assertTrue(outboxFile.exists())

        val cleared = manager.clearPendingOutbox()
        assertTrue(cleared)
        assertEquals(0, manager.pendingQueueCount.value)
        assertFalse(outboxFile.exists())
    }

    @Test
    fun testOutboxReloadsFromDiskOnInit() {
        val manager1 = CloudSyncManager(context, secureStorageManager, eventRepository)
        manager1.enqueueSyncEvent("EVENT_1", "{\"id\":1}")
        manager1.enqueueSyncEvent("EVENT_2", "{\"id\":2}")
        assertEquals(2, manager1.pendingQueueCount.value)

        // Instantiate new manager - should reload from persistent file
        val manager2 = CloudSyncManager(context, secureStorageManager, eventRepository)
        assertEquals(2, manager2.pendingQueueCount.value)
    }
}
