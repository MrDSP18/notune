package com.music.echo.notune

import com.music.echo.notune.personalization.repository.NotuneCustomizationConfig
import com.music.echo.notune.personalization.repository.NotuneGeometryPreset
import com.music.echo.notune.personalization.repository.NotuneThemeConfig
import com.music.echo.notune.personalization.repository.NotuneThemePreset
import org.junit.Assert.*
import org.junit.Test

class NotuneCustomizationTest {

    @Test
    fun testDefaultCustomizationConfig() {
        val config = NotuneCustomizationConfig()
        assertEquals(NotuneThemePreset.NOTUNE_RED, config.theme.preset)
        assertEquals("#FF0031", config.theme.primaryColorHex)
        assertEquals(NotuneGeometryPreset.SOFT, config.geometry.preset)
        assertTrue(config.player.showArtwork)
        assertTrue(config.components.showRecentlyPlayed)
    }

    @Test
    fun testThemePresetCustomization() {
        val oledTheme = NotuneThemeConfig(
            preset = NotuneThemePreset.OLED_DARK,
            primaryColorHex = "#FF0031",
            backgroundColorHex = "#000000",
            surfaceColorHex = "#050505"
        )
        val config = NotuneCustomizationConfig(theme = oledTheme)
        assertEquals(NotuneThemePreset.OLED_DARK, config.theme.preset)
        assertEquals("#000000", config.theme.backgroundColorHex)
    }
}
