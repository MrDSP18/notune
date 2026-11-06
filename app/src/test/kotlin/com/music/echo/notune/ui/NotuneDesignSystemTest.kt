package com.music.echo.notune.ui

import com.music.echo.notune.ui.theme.NotuneColors
import com.music.echo.notune.ui.theme.NotuneTypography
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class NotuneDesignSystemTest {

    @Test
    fun `test color palette definition`() {
        assertNotNull(NotuneColors.OledBlack)
        assertNotNull(NotuneColors.PureWhite)
        assertNotNull(NotuneColors.NothingRed)
        assertEquals(androidx.compose.ui.graphics.Color(0xFF000000), NotuneColors.OledBlack)
    }

    @Test
    fun `test typography styles`() {
        assertNotNull(NotuneTypography.DotMatrixHeader)
        assertNotNull(NotuneTypography.DotMatrixLarge)
        assertNotNull(NotuneTypography.DotMatrixMedium)
        assertNotNull(NotuneTypography.TechnicalHeader)
    }
}
