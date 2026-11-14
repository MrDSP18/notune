package echo.music.iad1tya.playback

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ABRepeatControllerTest {

    private lateinit var controller: ABRepeatController

    @Before
    fun setUp() {
        controller = ABRepeatController()
    }

    @Test
    fun testInitialState() {
        val state = controller.state.value
        assertNull(state.pointA)
        assertNull(state.pointB)
        assertFalse(state.isEnabled)
        assertFalse(state.isLoopReady)
    }

    @Test
    fun testSettingPointsAndLooping() {
        controller.setPointA(1000L)
        assertEquals(1000L, controller.state.value.pointA)
        assertNull(controller.state.value.pointB)
        assertFalse(controller.state.value.isLoopReady)

        controller.setPointB(5000L)
        assertEquals(5000L, controller.state.value.pointB)
        assertTrue(controller.state.value.isEnabled)
        assertTrue(controller.state.value.isLoopReady)

        var seekedTo: Long? = null
        val seekAction: (Long) -> Unit = { seekedTo = it }

        // Position before Point B should not loop
        val loopedEarly = controller.checkAndLoop(4999L, seekAction)
        assertFalse(loopedEarly)
        assertNull(seekedTo)

        // Position at or past Point B should loop to Point A
        val looped = controller.checkAndLoop(5000L, seekAction)
        assertTrue(looped)
        assertEquals(1000L, seekedTo)
    }

    @Test
    fun testInvalidPointBReset() {
        controller.setPointA(3000L)
        controller.setPointB(1000L) // Point B <= Point A should be rejected
        assertNull(controller.state.value.pointB)
        assertFalse(controller.state.value.isLoopReady)
    }

    @Test
    fun testToggleAndClear() {
        controller.setPointA(1000L)
        controller.setPointB(2000L)
        assertTrue(controller.state.value.isEnabled)

        controller.toggle()
        assertFalse(controller.state.value.isEnabled)

        controller.clear()
        assertNull(controller.state.value.pointA)
        assertNull(controller.state.value.pointB)
        assertFalse(controller.state.value.isEnabled)
    }
}
