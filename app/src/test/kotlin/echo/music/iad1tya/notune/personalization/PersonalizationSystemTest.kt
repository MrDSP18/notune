package echo.music.iad1tya.notune.personalization

import com.music.echo.notune.theme.*
import echo.music.iad1tya.constants.*
import org.junit.Assert.*
import org.junit.Test

class PersonalizationSystemTest {

    @Test
    fun `NoTuneThemeEngine contains 20 distinct theme presets`() {
        val themes = NoTuneThemeEngine.ALL_THEMES
        assertEquals(20, themes.size)

        val pure = NoTuneThemeEngine.getThemeById("notune_pure")
        assertEquals("NØTUNE Pure", pure.name)

        val midnight = NoTuneThemeEngine.getThemeById("midnight")
        assertTrue(midnight.isOledOptimized)

        val tokyo = NoTuneThemeEngine.getThemeById("tokyo_neon")
        assertEquals("Tokyo Cyberpunk", tokyo.name)
    }

    @Test
    fun `NoTuneThemeEngine resolves light dark and system appearance modes`() {
        val theme = NoTuneThemeEngine.MIDNIGHT

        val darkScheme = NoTuneThemeEngine.resolveColorScheme(theme, AppearanceMode.DARK, isSystemDark = false)
        assertEquals(theme.darkColorScheme, darkScheme)

        val lightScheme = NoTuneThemeEngine.resolveColorScheme(theme, AppearanceMode.LIGHT, isSystemDark = true)
        assertEquals(theme.lightColorScheme, lightScheme)

        val systemSchemeDark = NoTuneThemeEngine.resolveColorScheme(theme, AppearanceMode.SYSTEM, isSystemDark = true)
        assertEquals(theme.darkColorScheme, systemSchemeDark)
    }

    @Test
    fun `NoTuneTypographySystem builds typography for all 15 font styles`() {
        FontFamilyStyle.entries.forEach { style ->
            val typography = NoTuneTypographySystem.buildTypography(fontStyle = style, scaleFactor = 1.0f)
            assertNotNull(typography)
            assertNotNull(typography.headlineMedium)
        }
    }

    @Test
    fun `SmartContextWidgetEngine prioritizes actions depending on UiContext`() {
        val focusActions = SmartContextWidgetEngine.getPrioritizedActions(UiContext.FOCUS)
        assertTrue(focusActions.any { it.id == "calm_mode" })

        val workoutActions = SmartContextWidgetEngine.getPrioritizedActions(UiContext.WORKOUT)
        assertTrue(workoutActions.any { it.id == "high_energy" })

        val partyActions = SmartContextWidgetEngine.getPrioritizedActions(UiContext.PARTY)
        assertTrue(partyActions.any { it.id == "room_queue" })
    }

    @Test
    fun `LogoVariant supports all 12 creative logo variants`() {
        assertEquals(12, LogoVariant.entries.size)
        assertEquals(LogoVariant.WORDMARK, LogoVariant.fromName("wordmark"))
        assertEquals(LogoVariant.GLYPH, LogoVariant.fromName("glyph"))
        assertEquals(LogoVariant.CYBER_PULSE, LogoVariant.fromName("cyber_pulse"))
        assertEquals(LogoVariant.HARMONIC_WAVE, LogoVariant.fromName("harmonic_wave"))
        assertEquals(LogoVariant.VINYL_GROOVES, LogoVariant.fromName("vinyl_grooves"))
        assertEquals(LogoVariant.INFINITE_LOOP, LogoVariant.fromName("infinite_loop"))
        assertEquals(LogoVariant.ACOUSTIC_MESH, LogoVariant.fromName("acoustic_mesh"))

        assertEquals(FontFamilyStyle.SANS, FontFamilyStyle.fromName("sans"))
        assertEquals(FontFamilyStyle.MONO, FontFamilyStyle.fromName("mono"))
        assertEquals(UiContext.WORKOUT, UiContext.fromName("workout"))
        assertEquals(CornerRadiusStyle.ROUNDED, CornerRadiusStyle.fromName("rounded"))
    }
}
