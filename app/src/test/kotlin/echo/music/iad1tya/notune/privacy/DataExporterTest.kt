package echo.music.iad1tya.notune.privacy

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.test.core.app.ApplicationProvider
import com.music.echo.notune.privacy.export.DataExporter
import echo.music.iad1tya.constants.FlowEnabledKey
import echo.music.iad1tya.constants.PrivateSessionEnabledKey
import echo.music.iad1tya.db.MusicDatabase
import echo.music.iad1tya.notune.MusicDnaRepository
import echo.music.iad1tya.utils.dataStore
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File
import java.util.zip.ZipFile

@RunWith(RobolectricTestRunner::class)
class DataExporterTest {

    companion object {
        @BeforeClass
        @JvmStatic
        fun setupConscryptWorkdir() {
            val workdir = File("build/tmp/conscrypt").apply { mkdirs() }
            System.setProperty("org.conscrypt.native.workdir", workdir.absolutePath)
        }
    }

    private lateinit var context: Context
    private val database = mockk<MusicDatabase>(relaxed = true)
    private val musicDnaRepository = mockk<MusicDnaRepository>(relaxed = true)

    @Before
    fun setUp() {
        runBlocking {
            context = ApplicationProvider.getApplicationContext<Context>()
            context.dataStore.edit {
                it[PrivateSessionEnabledKey] = false
                it[FlowEnabledKey] = true
            }
            every { database.likedSongs(any(), any()) } returns flowOf(emptyList())
            every { database.playlists(any(), any()) } returns flowOf(emptyList())
            every { database.topSongs(any()) } returns flowOf(emptyList())
            coEvery { musicDnaRepository.getEnergyProfile() } returns "High Energy"
            coEvery { musicDnaRepository.getDiscoveryScore() } returns 0.88f
            coEvery { musicDnaRepository.getTopGenres(any()) } returns listOf("Synthwave", "Ambient")
        }
    }

    @Test
    fun testGenerateDataExportCreatesValidZipWithAllFiles() {
        runBlocking {
            val exporter = DataExporter(context, database, musicDnaRepository)
            val exportFile = exporter.generateDataExport()

            assertTrue(exportFile.exists())
            assertTrue(exportFile.length() > 0)

            val zip = ZipFile(exportFile)
            val entries = zip.entries().asSequence().map { it.name }.toSet()

            assertTrue(entries.contains("profile.json"))
            assertTrue(entries.contains("favorites.json"))
            assertTrue(entries.contains("playlists.json"))
            assertTrue(entries.contains("history.json"))
            assertTrue(entries.contains("preferences.json"))
            assertTrue(entries.contains("music-dna.json"))
            assertTrue(entries.contains("flow.json"))
            assertTrue(entries.contains("sync.json"))

            // Verify Preferences JSON contains real datastore values
            val prefsEntry = zip.getEntry("preferences.json")
            val prefsText = zip.getInputStream(prefsEntry).bufferedReader().use { it.readText() }
            val prefsJson = JSONObject(prefsText)
            assertTrue(prefsJson.has("flowEnabled"))
            assertEquals(true, prefsJson.getBoolean("flowEnabled"))

            // Verify Music DNA contains repository-computed values
            val dnaEntry = zip.getEntry("music-dna.json")
            val dnaText = zip.getInputStream(dnaEntry).bufferedReader().use { it.readText() }
            val dnaJson = JSONObject(dnaText)
            assertEquals("High Energy", dnaJson.getString("energyProfile"))
            assertEquals(88, dnaJson.getInt("discoveryScore"))
            val genres = dnaJson.getJSONArray("topGenres")
            assertEquals(2, genres.length())
            assertEquals("Synthwave", genres.getString(0))

            zip.close()
            exportFile.delete()
        }
    }
}
