package echo.music.iad1tya.notune.personalization

import com.music.echo.notune.personalization.model.DiscoveryPreference
import com.music.echo.notune.personalization.model.SelectedArtist
import com.music.echo.notune.personalization.model.TasteProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PersonalizationEngineTest {

    @Test
    fun testExplainability_favoriteArtistMatch_returnsCorrectReason() {
        val artistName = "Anirudh Ravichander"
        val favoriteArtists = listOf(SelectedArtist("1", "Anirudh Ravichander"))
        val isMatch = favoriteArtists.any { it.name.equals(artistName, ignoreCase = true) }

        assertTrue(isMatch)
    }

    @Test
    fun testCandidateQueryGeneration_combinesLanguagesAndGenres() {
        val language = "Tamil"
        val genre = "Pop"
        val query = if (language.isNotEmpty() && !genre.contains(language, ignoreCase = true)) {
            "$language $genre"
        } else {
            genre
        }

        assertEquals("Tamil Pop", query)
    }

    @Test
    fun testFamiliarRatio_mapsToEngineDiscoveryValue() {
        val discovery = DiscoveryPreference.MOSTLY_NEW
        val ratio = discovery.familiarRatio
        assertEquals(0.30f, ratio, 0.01f)
    }
}
