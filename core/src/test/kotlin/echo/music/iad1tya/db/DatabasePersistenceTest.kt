package echo.music.iad1tya.db

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import echo.music.iad1tya.db.entities.SongEntity
import echo.music.iad1tya.db.entities.ArtistEntity
import echo.music.iad1tya.db.entities.SongArtistMap
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import android.content.Context

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [32])
class DatabasePersistenceTest {

    private lateinit var db: InternalDatabase
    private lateinit var dao: DatabaseDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, InternalDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.dao
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `test song insertion and retrieval`() = runBlocking {
        val song = SongEntity(
            id = "test_song_1",
            title = "Test Song",
            duration = 180,
            isLocal = true
        )
        dao.insert(song)

        val retrieved = dao.getSongById("test_song_1")
        assertNotNull(retrieved)
        assertEquals("Test Song", retrieved?.song?.title)
    }

    @Test
    fun `test song-artist relationship persistence`() = runBlocking {
        val song = SongEntity(id = "s1", title = "T1", isLocal = true)
        val artist = ArtistEntity(id = "a1", name = "A1", isLocal = true)
        
        dao.insert(song)
        dao.insert(artist)
        dao.insert(SongArtistMap(songId = "s1", artistId = "a1", position = 0))

        val localSongs = dao.localSongs().first()
        assertEquals(1, localSongs.size)
        assertEquals(1, localSongs[0].artists.size)
        assertEquals("A1", localSongs[0].artists[0].name)
    }

    @Test
    fun `test duplicate song insertion with IGNORE`() = runBlocking {
        val song = SongEntity(id = "s1", title = "T1", isLocal = true)
        dao.insert(song)
        
        val song2 = SongEntity(id = "s1", title = "T2", isLocal = true)
        dao.insert(song2) // Should be ignored because of primary key conflict

        val retrieved = dao.getSongById("s1")
        assertEquals("T1", retrieved?.song?.title)
    }

    @Test
    fun `test database persistence across restarts`() = runBlocking {
        val songId = "persistent_song"
        dao.insert(SongEntity(id = songId, title = "Persistent", isLocal = true))
        
        // We can't easily "restart" an in-memory DB in the same way as a file DB,
        // but we can simulate it by closing and reopening a file-based DB if we had one.
        // For in-memory, we'll verify it works within the session.
        // To truly test persistence, we'd need a temporary file.
        
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dbFile = context.getDatabasePath("test_restart.db")
        dbFile.delete()
        
        val fileDb = Room.databaseBuilder(context, InternalDatabase::class.java, dbFile.absolutePath)
            .allowMainThreadQueries()
            .build()
        
        try {
            fileDb.dao.insert(SongEntity(id = songId, title = "Persistent", isLocal = true))
            fileDb.close()
            
            val reopenedDb = Room.databaseBuilder(context, InternalDatabase::class.java, dbFile.absolutePath)
                .allowMainThreadQueries()
                .build()
            
            val retrieved = reopenedDb.dao.getSongById(songId)
            assertNotNull(retrieved)
            assertEquals("Persistent", retrieved?.song?.title)
            reopenedDb.close()
        } finally {
            dbFile.delete()
        }
    }
}
