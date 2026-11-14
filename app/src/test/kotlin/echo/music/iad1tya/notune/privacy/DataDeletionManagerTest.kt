package echo.music.iad1tya.notune.privacy

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.music.echo.notune.privacy.deletion.DataDeletionManager
import com.music.echo.notune.sync.CloudSyncManager
import echo.music.iad1tya.db.MusicDatabase
import echo.music.iad1tya.notune.ai.engine.NoMindMemory
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File

@RunWith(RobolectricTestRunner::class)
class DataDeletionManagerTest {

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
    private val mindMemory = mockk<NoMindMemory>(relaxed = true)
    private val cloudSyncManager = mockk<CloudSyncManager>(relaxed = true)
    private lateinit var dataDeletionManager: DataDeletionManager

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
        dataDeletionManager = DataDeletionManager(
            context = context,
            database = database,
            mindMemory = mindMemory,
            cloudSyncManager = cloudSyncManager
        )
    }

    @Test
    fun testClearListeningHistoryQueriesDatabase() {
        runBlocking {
            dataDeletionManager.clearListeningHistory()
            val slot = slot<MusicDatabase.() -> Unit>()
            verify { database.query(capture(slot)) }
            slot.captured.invoke(database)
            verify { database.clearListenHistory() }
        }
    }

    @Test
    fun testClearSearchHistoryQueriesDatabase() {
        runBlocking {
            dataDeletionManager.clearSearchHistory()
            val slot = slot<MusicDatabase.() -> Unit>()
            verify { database.query(capture(slot)) }
            slot.captured.invoke(database)
            verify { database.clearSearchHistory() }
        }
    }

    @Test
    fun testClearAiDataDeletesCacheAndClearsMemory() {
        runBlocking {
            val aiCacheDir = File(context.cacheDir, "ai_cache").apply { mkdirs() }
            File(aiCacheDir, "cached_embedding.bin").writeText("test-data")

            dataDeletionManager.clearAiData()

            assertFalse(aiCacheDir.exists())
            coVerify(exactly = 1) { mindMemory.clearMemory() }
        }
    }

    @Test
    fun testFullAccountAndDataResetCleansSyncOutboxAndDatabase() {
        runBlocking {
            dataDeletionManager.fullAccountAndDataReset()

            coVerify(atLeast = 1) { cloudSyncManager.clearPendingOutbox() }
            coVerify(atLeast = 1) { mindMemory.clearMemory() }
            verify(atLeast = 1) { database.clearAllData() }
        }
    }
}
