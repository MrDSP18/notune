package echo.music.iad1tya.notune

import echo.music.iad1tya.listentogether.ChatMessagePayload
import echo.music.iad1tya.models.Room
import echo.music.iad1tya.models.RoomMember
import echo.music.iad1tya.models.RoomRole
import echo.music.iad1tya.models.RoomType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RoomTypeAndChatTest {

    @Test
    fun testRoomTypeEnumerationIncludesCoupleFriendsAndTeam() {
        val types = RoomType.entries
        assertTrue(types.contains(RoomType.COUPLE))
        assertTrue(types.contains(RoomType.FRIENDS))
        assertTrue(types.contains(RoomType.TEAM))
        assertTrue(types.contains(RoomType.PUBLIC))
    }

    @Test
    fun testCoupleRoomCapacityAndPrivacy() {
        val coupleRoom = Room(
            roomId = "CP9988",
            name = "Late Night Sync",
            type = RoomType.COUPLE,
            hostId = "user_1",
            members = listOf(
                RoomMember("user_1", "DSP", RoomRole.HOST),
                RoomMember("user_2", "Partner", RoomRole.MEMBER)
            )
        )
        assertEquals(RoomType.COUPLE, coupleRoom.type)
        assertEquals(2, coupleRoom.members.size)
    }

    @Test
    fun testTeamRoomCapacityAndStructure() {
        val teamRoom = Room(
            roomId = "TM1234",
            name = "Engineering Collab Room",
            type = RoomType.TEAM,
            hostId = "host_lead",
            members = listOf(
                RoomMember("host_lead", "TeamLead", RoomRole.HOST),
                RoomMember("dev_1", "Dev1", RoomRole.MEMBER),
                RoomMember("dev_2", "Dev2", RoomRole.MEMBER)
            )
        )
        assertEquals(RoomType.TEAM, teamRoom.type)
        assertTrue(teamRoom.members.size >= 3)
    }

    @Test
    fun testChatMessagePayloadSupportsReactionsAndConversations() {
        val regularChat = ChatMessagePayload(
            userId = "u1",
            username = "DSP",
            message = "This track's bass punch is unreal!",
            timestamp = 1700000000L
        )
        assertEquals("DSP", regularChat.username)
        assertTrue(regularChat.message.contains("unreal"))

        val reactionChat = ChatMessagePayload(
            userId = "u2",
            username = "Partner",
            message = "REACTION:🔥",
            timestamp = 1700000005L
        )
        assertTrue(reactionChat.message.startsWith("REACTION:"))
        assertEquals("🔥", reactionChat.message.removePrefix("REACTION:"))
    }
}
