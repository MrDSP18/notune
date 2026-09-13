package echo.music.iad1tya.notune.personalization

import com.music.echo.notune.personalization.model.DiscoveryPreference
import com.music.echo.notune.personalization.model.SelectedArtist
import com.music.echo.notune.personalization.model.TasteProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TasteProfileRepositoryTest {

    @Test
    fun testTasteProfile_defaultValues_areCorrect() {
        val defaultProfile = TasteProfile()
        assertEquals("en", defaultProfile.appLanguage)
        assertEquals(setOf("English"), defaultProfile.musicLanguages)
        assertTrue(defaultProfile.favoriteArtists.isEmpty())
        assertTrue(defaultProfile.favoriteGenres.isEmpty())
        assertEquals(DiscoveryPreference.BALANCED, defaultProfile.discoveryPreference)
        assertFalse(defaultProfile.isOnboardingCompleted)
        assertFalse(defaultProfile.isOnboardingSkipped)
    }

    @Test
    fun testTasteProfile_updateValues_copiesProperly() {
        val artist = SelectedArtist("1", "A.R. Rahman")
        val initial = TasteProfile()
        val updated = initial.copy(
            appLanguage = "ta",
            musicLanguages = setOf("Tamil", "English"),
            favoriteArtists = listOf(artist),
            favoriteGenres = setOf("Tamil Pop", "Soundtrack"),
            isOnboardingCompleted = true
        )

        assertEquals("ta", updated.appLanguage)
        assertEquals(2, updated.musicLanguages.size)
        assertEquals("A.R. Rahman", updated.favoriteArtists.first().name)
        assertTrue(updated.isOnboardingCompleted)
    }

    @Test
    fun testDiscoveryPreference_ratios_areValid() {
        assertEquals(0.80f, DiscoveryPreference.MOSTLY_FAMILIAR.familiarRatio)
        assertEquals(0.50f, DiscoveryPreference.BALANCED.familiarRatio)
        assertEquals(0.30f, DiscoveryPreference.MOSTLY_NEW.familiarRatio)
    }
}
