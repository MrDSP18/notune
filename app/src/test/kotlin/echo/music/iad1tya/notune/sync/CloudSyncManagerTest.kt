package echo.music.iad1tya.notune.sync

import com.music.echo.notune.sync.CloudSyncManager
import org.junit.Assert.*
import org.junit.Test

class CloudSyncManagerTest {

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
}
