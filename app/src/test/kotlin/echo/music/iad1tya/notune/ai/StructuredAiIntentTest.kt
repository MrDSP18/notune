package echo.music.iad1tya.notune.ai

import com.music.echo.notune.ai.IntentActionType
import com.music.echo.notune.ai.StructuredAiIntent
import org.junit.Assert.*
import org.junit.Test

class StructuredAiIntentTest {

    @Test
    fun `StructuredAiIntent parses playback and mood intents correctly`() {
        val pauseIntent = StructuredAiIntent.parse("Pause music")
        assertEquals(IntentActionType.PAUSE, pauseIntent.actionType)

        val nextIntent = StructuredAiIntent.parse("Skip song")
        assertEquals(IntentActionType.NEXT, nextIntent.actionType)

        val calmIntent = StructuredAiIntent.parse("Make it calm and relaxing")
        assertEquals(IntentActionType.SET_MOOD, calmIntent.actionType)
        assertEquals("Calm", calmIntent.targetMood)

        val tamilIntent = StructuredAiIntent.parse("Play 90s Tamil hits")
        assertEquals(IntentActionType.FILTER_LANGUAGE, tamilIntent.actionType)
        assertEquals("Tamil", tamilIntent.targetLanguage)
    }
}
