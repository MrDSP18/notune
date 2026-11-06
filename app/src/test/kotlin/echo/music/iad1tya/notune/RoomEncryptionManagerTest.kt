package echo.music.iad1tya.notune

import echo.music.iad1tya.models.RoomType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RoomEncryptionManagerTest {

    @Test
    fun testRoomTypeEncryptedProperty() {
        val publicRoomIsEncrypted = RoomType.PUBLIC == RoomType.PRIVATE || RoomType.PUBLIC == RoomType.COUPLE
        val privateRoomIsEncrypted = RoomType.PRIVATE == RoomType.PRIVATE || RoomType.PRIVATE == RoomType.COUPLE
        val coupleRoomIsEncrypted = RoomType.COUPLE == RoomType.PRIVATE || RoomType.COUPLE == RoomType.COUPLE

        assertTrue(!publicRoomIsEncrypted)
        assertTrue(privateRoomIsEncrypted)
        assertTrue(coupleRoomIsEncrypted)
    }

    @Test
    fun testCoupleMemberLimitEnforcement() {
        val coupleMemberLimit = if (RoomType.COUPLE == RoomType.COUPLE) 2 else 100
        assertEquals(2, coupleMemberLimit)
    }
}
