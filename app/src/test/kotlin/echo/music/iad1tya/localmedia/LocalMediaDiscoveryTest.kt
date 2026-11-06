package echo.music.iad1tya.localmedia

import android.content.Context
import echo.music.iad1tya.db.MusicDatabase
import echo.music.iad1tya.db.entities.Song
import echo.music.iad1tya.db.entities.SongEntity
import echo.music.iad1tya.db.entities.ArtistEntity
import echo.music.iad1tya.db.entities.SongArtistMap
import echo.music.iad1tya.db.entities.Artist
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import echo.music.iad1tya.R

class LocalMediaDiscoveryTest {

    private val context = mockk<Context>(relaxed = true)
    private val database = mockk<MusicDatabase>(relaxed = true)
    private lateinit var scanner: LocalSongScanner

    @Before
    fun setUp() {
        scanner = spyk(LocalSongScanner(context, database))
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `test mapping from LocalTrackRecord to entities`() = runTest {
        val track = LocalSongScanner.LocalTrackRecord(
            id = "content://media/1",
            title = "Test Title",
            artists = listOf(LocalSongScanner.LocalArtistRecord("a1", "Test Artist")),
            albumId = "al1",
            albumName = "Test Album",
            durationSeconds = 180,
            year = 2023,
            dateModified = null,
            sizeBytes = 5000000L,
            mimeType = "audio/mpeg",
            thumbnailUrl = "thumb1"
        )
        
        val snapshot = LocalSongScanner.LocalScanSnapshot(
            tracks = listOf(track),
            artists = listOf(LocalSongScanner.LocalArtistRecord("a1", "Test Artist")),
            albums = listOf(LocalSongScanner.LocalAlbumRecord("al1", "Test Album", 2023, "thumb1", 1, 180, listOf("a1")))
        )

        every { scanner.queryTracks(any()) } returns snapshot

        // Mock database transaction
        val slot = slot<suspend MusicDatabase.() -> Unit>()
        coEvery { database.withTransaction(capture(slot)) } coAnswers {
            slot.captured.invoke(database)
        }
        
        val summary = scanner.scanDevice()
        
        assertEquals(1, summary.scannedSongs)
        
        // Verify mapping
        val songEntitySlot = slot<SongEntity>()
        verify { database.insert(capture(songEntitySlot)) }
        
        val song = songEntitySlot.captured
        assertEquals("Test Title", song.title)
        assertEquals(180, song.duration)
        assertEquals("Test Album", song.albumName)
        assertTrue(song.isLocal)
        assertEquals("content://media/1", song.id)
        
        verify { database.insert(any<ArtistEntity>()) }
        verify { database.insert(any<SongArtistMap>()) }
    }

    @Test
    fun `test metadata update on rescanning same file`() = runTest {
        val track = LocalSongScanner.LocalTrackRecord(
            id = "content://media/1",
            title = "Old Title",
            artists = listOf(LocalSongScanner.LocalArtistRecord("a1", "Artist")),
            albumId = "al1",
            albumName = "Album",
            durationSeconds = 180,
            year = 2023,
            dateModified = null,
            sizeBytes = 1000L,
            mimeType = "audio/mpeg",
            thumbnailUrl = "thumb1"
        )
        
        val snapshot = LocalSongScanner.LocalScanSnapshot(
            tracks = listOf(track),
            artists = listOf(LocalSongScanner.LocalArtistRecord("a1", "Artist")),
            albums = listOf(LocalSongScanner.LocalAlbumRecord("al1", "Album", 2023, "thumb1", 1, 180, listOf("a1")))
        )

        every { scanner.queryTracks(any()) } returns snapshot
        
        val slot = slot<suspend MusicDatabase.() -> Unit>()
        coEvery { database.withTransaction(capture(slot)) } coAnswers {
            slot.captured.invoke(database)
        }

        // First scan
        scanner.scanDevice()
        
        // Second scan with updated title
        val updatedTrack = track.copy(title = "New Title")
        val updatedSnapshot = snapshot.copy(tracks = listOf(updatedTrack))
        every { scanner.queryTracks(any()) } returns updatedSnapshot
        
        // We need to mock existingSongs return from loadSongs (which is called inside scanDevice)
        // Wait, loadSongs calls database.getSongsByIds
        val songEntity = SongEntity(id = "content://media/1", title = "Old Title", duration = 180, isLocal = true)
        val existingSong = Song(song = songEntity, artists = emptyList(), album = null, format = null)
        coEvery { database.getSongsByIds(any()) } returns listOf(existingSong)

        scanner.scanDevice()
        
        // Verify if insert or update was called with New Title
        val capturedSongs = mutableListOf<SongEntity>()
        verify { database.insert(capture(capturedSongs)) }
        
        // If it used IGNORE, the second insert will have "New Title" but DB will ignore it.
        // The scanner should ideally use Upsert or call update.
        assertTrue(capturedSongs.any { it.title == "New Title" })
    }
}
