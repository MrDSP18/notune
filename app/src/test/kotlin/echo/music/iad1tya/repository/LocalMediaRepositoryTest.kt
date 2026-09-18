package echo.music.iad1tya.repository

import com.music.echo.repository.LocalMediaRepositoryImpl
import echo.music.iad1tya.db.MusicDatabase
import echo.music.iad1tya.db.entities.Song
import echo.music.iad1tya.db.entities.SongEntity
import echo.music.iad1tya.localmedia.LocalSongScanner
import echo.music.iad1tya.models.LocalSong
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class LocalMediaRepositoryTest {

    private val database = mockk<MusicDatabase>()
    private val scanner = mockk<LocalSongScanner>()
    private lateinit var repository: LocalMediaRepository

    @Before
    fun setUp() {
        repository = LocalMediaRepositoryImpl(database, scanner)
    }

    @Test
    fun `test getSongs maps database entities to local models`() = runTest {
        val songEntity = SongEntity(
            id = "test_id",
            title = "Test Song",
            duration = 180,
            albumName = "Test Album",
            isLocal = true
        )
        val song = Song(
            song = songEntity,
            artists = emptyList(),
            album = null,
            format = null
        )
        
        every { database.localSongs() } returns flowOf(listOf(song))
        
        val result = repository.getSongs().first()
        
        assertEquals(1, result.size)
        val localSong = result[0]
        assertEquals("test_id", localSong.id)
        assertEquals("Test Song", localSong.title)
        assertEquals("Test Album", localSong.albumTitle)
        assertEquals(180, localSong.durationSeconds)
    }

    @Test
    fun `test startScan triggers scanner`() = runTest {
        coEvery { scanner.scanDevice() } returns mockk()
        
        repository.startScan()
        
        coVerify { scanner.scanDevice() }
    }
}
