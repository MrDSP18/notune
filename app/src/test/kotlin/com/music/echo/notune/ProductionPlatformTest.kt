package com.music.echo.notune

import com.music.echo.notune.governor.GovernorLevel
import com.music.echo.notune.governor.PlatformGovernor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductionPlatformTest {

    @Test
    fun testPlatformGovernorLevels() {
        PlatformGovernor.setLevel(GovernorLevel.GREEN)
        val greenPolicy = PlatformGovernor.getPolicy()
        assertEquals(GovernorLevel.GREEN, greenPolicy.level)
        assertTrue(greenPolicy.lyricsPrefetchEnabled)
        assertTrue(greenPolicy.analyticsEnabled)
        assertEquals(5000L, greenPolicy.presenceIntervalMs)

        PlatformGovernor.setLevel(GovernorLevel.ORANGE)
        val orangePolicy = PlatformGovernor.getPolicy()
        assertEquals(GovernorLevel.ORANGE, orangePolicy.level)
        assertFalse(orangePolicy.lyricsPrefetchEnabled)
        assertFalse(orangePolicy.analyticsEnabled)
        assertEquals(30000L, orangePolicy.presenceIntervalMs)
    }

    @Test
    fun testHeaderParsing() {
        assertEquals(GovernorLevel.GREEN, PlatformGovernor.parseHeader("GREEN"))
        assertEquals(GovernorLevel.YELLOW, PlatformGovernor.parseHeader("yellow"))
        assertEquals(GovernorLevel.ORANGE, PlatformGovernor.parseHeader("ORANGE"))
        assertEquals(GovernorLevel.RED, PlatformGovernor.parseHeader("RED"))
        assertEquals(GovernorLevel.GREEN, PlatformGovernor.parseHeader(null))
        assertEquals(GovernorLevel.GREEN, PlatformGovernor.parseHeader("INVALID"))
    }
}
