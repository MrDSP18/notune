package com.music.echo.notune.gesture

import echo.music.iad1tya.notune.ai.tools.AiToolRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.math.cos
import kotlin.math.sin

class GestureEngineTest {

    private lateinit var toolRegistry: AiToolRegistry
    private lateinit var gestureEngine: GestureEngine

    @Before
    fun setUp() {
        toolRegistry = AiToolRegistry()
        gestureEngine = GestureEngine(toolRegistry)
        gestureEngine.updateSettings(GestureSettings(gestureCooldownMs = 0L))
    }

    @Test
    fun `test context resolution for swipes`() {
        gestureEngine.setContext(GestureContext.PLAYER)
        val actionPlayerLeft = gestureEngine.processGesture(GestureEvent.Swipe(GestureEvent.SwipeDirection.LEFT))
        assertEquals(GestureAction.NextTrack, actionPlayerLeft)

        gestureEngine.setContext(GestureContext.MINI_PLAYER)
        val actionMiniPlayerLeft = gestureEngine.processGesture(GestureEvent.Swipe(GestureEvent.SwipeDirection.LEFT))
        assertEquals(GestureAction.PreviousTrack, actionMiniPlayerLeft)

        gestureEngine.setContext(GestureContext.QUEUE)
        val actionQueueRight = gestureEngine.processGesture(GestureEvent.Swipe(GestureEvent.SwipeDirection.RIGHT))
        assertEquals(GestureAction.PlayNext, actionQueueRight)
    }

    @Test
    fun `test drawn shape classification for circle`() {
        // Generate circular stroke points
        val points = mutableListOf<GesturePoint>()
        val centerX = 200f
        val centerY = 200f
        val radius = 100f
        val numPoints = 20

        for (i in 0..numPoints) {
            val angle = 2.0 * Math.PI * i / numPoints
            val x = (centerX + radius * cos(angle)).toFloat()
            val y = (centerY + radius * sin(angle)).toFloat()
            points.add(GesturePoint(x, y))
        }

        val shape = GestureClassifier.classifyStrokePath(points)
        assertNotNull(shape)
        assertEquals(GestureEvent.ShapeType.CIRCLE, shape?.shape)

        val action = gestureEngine.processGesture(shape!!)
        assertEquals(GestureAction.OpenAi, action)
    }

    @Test
    fun `test drawn shape classification for V shape`() {
        val points = listOf(
            GesturePoint(100f, 100f),
            GesturePoint(150f, 250f),
            GesturePoint(200f, 400f),
            GesturePoint(250f, 250f),
            GesturePoint(300f, 100f)
        )

        val shape = GestureClassifier.classifyStrokePath(points)
        assertNotNull(shape)
        assertEquals(GestureEvent.ShapeType.V_SHAPE, shape?.shape)

        val action = gestureEngine.processGesture(shape!!)
        assertEquals(GestureAction.PlayNext, action)
    }

    @Test
    fun `test multi-touch and eq gestures`() {
        gestureEngine.setContext(GestureContext.PLAYER)

        val pinchInAction = gestureEngine.processGesture(GestureEvent.Pinch(scaleFactor = 0.8f))
        assertEquals(GestureAction.CompactPlayer, pinchInAction)

        val rotateAction = gestureEngine.processGesture(GestureEvent.TwoFingerRotate(degrees = 45f))
        assertEquals(GestureAction.ChangeSpeed(0.25f), rotateAction)

        val eqBassAction = gestureEngine.processGesture(GestureEvent.EqPinch(delta = 1.0f))
        assertEquals(GestureAction.AdjustBass(1.5f), eqBassAction)
    }

    @Test
    fun `test shake gesture in party vs player context`() {
        gestureEngine.setContext(GestureContext.PLAYER)
        val playerShake = gestureEngine.processGesture(GestureEvent.ShakeDevice)
        assertEquals(GestureAction.ShuffleQueue, playerShake)

        gestureEngine.setContext(GestureContext.PARTY)
        val partyShake = gestureEngine.processGesture(GestureEvent.ShakeDevice)
        assertEquals(GestureAction.PartyBoost, partyShake)
    }

    @Test
    fun `test macro execution`() {
        val macro = GestureMacroEngine.getMacro("macro_fav_and_next")
        assertNotNull(macro)
        assertEquals(2, macro?.actions?.size)

        val macroEvent = GestureEvent.TriggerMacro("macro_fav_and_next")
        val processed = gestureEngine.processGesture(macroEvent)
        assertEquals(GestureAction.ExecuteMacro("macro_fav_and_next"), processed)
    }

    @Test
    fun `test gesture system disabled toggle`() {
        gestureEngine.updateSettings(GestureSettings(isGestureSystemEnabled = false, gestureCooldownMs = 0L))
        val action = gestureEngine.processGesture(GestureEvent.SingleTap)
        assertEquals(GestureAction.None, action)
    }
}
