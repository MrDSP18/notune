package echo.music.iad1tya.notune

import echo.music.iad1tya.notune.ai.ToolCall
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class AiToolManagerTest {

    @Test
    fun testToolCallCreation_validArguments() {
        val toolCall = ToolCall(
            id = "call_12345",
            functionName = "search_songs",
            arguments = mapOf("query" to "Anirudh Ravichander")
        )

        assertEquals("call_12345", toolCall.id)
        assertEquals("search_songs", toolCall.functionName)
        assertEquals("Anirudh Ravichander", toolCall.arguments["query"])
    }

    @Test
    fun testToolCallCreation_playSong() {
        val toolCall = ToolCall(
            id = "call_67890",
            functionName = "play_song",
            arguments = mapOf("song_id" to "v1234567")
        )

        assertEquals("play_song", toolCall.functionName)
        assertEquals("v1234567", toolCall.arguments["song_id"])
    }
}
