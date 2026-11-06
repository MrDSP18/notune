package echo.music.iad1tya.models

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime

class LocalMediaModelsTest {

    @Test
    fun `test local song data preservation`() {
        val now = LocalDateTime.now()
        val song = LocalSong(
            id = "test_id",
            title = "Test Song",
            artists = listOf("Artist 1", "Artist 2"),
            albumTitle = "Test Album",
            durationSeconds = 180,
            thumbnailUrl = "content://media/external/audio/albumart/1",
            mimeType = "audio/mpeg",
            sizeBytes = 5000000L,
            dateModified = now
        )

        assertEquals("test_id", song.id)
        assertEquals("Test Song", song.title)
        assertEquals(listOf("Artist 1", "Artist 2"), song.artists)
        assertEquals("Test Album", song.albumTitle)
        assertEquals(180, song.durationSeconds)
        assertEquals("content://media/external/audio/albumart/1", song.thumbnailUrl)
        assertEquals("audio/mpeg", song.mimeType)
        assertEquals(5000000L, song.sizeBytes)
        assertEquals(now, song.dateModified)
    }

    @Test
    fun `test local album data preservation`() {
        val album = LocalAlbum(
            id = "album_id",
            title = "Test Album",
            artists = listOf("Artist 1"),
            songCount = 10,
            durationSeconds = 3600,
            thumbnailUrl = "content://media/external/audio/albumart/1"
        )

        assertEquals("album_id", album.id)
        assertEquals("Test Album", album.title)
        assertEquals(listOf("Artist 1"), album.artists)
        assertEquals(10, album.songCount)
        assertEquals(3600, album.durationSeconds)
        assertEquals("content://media/external/audio/albumart/1", album.thumbnailUrl)
    }

    @Test
    fun `test local artist data preservation`() {
        val artist = LocalArtist(
            id = "artist_id",
            name = "Test Artist",
            songCount = 5,
            thumbnailUrl = "https://example.com/artist.jpg"
        )

        assertEquals("artist_id", artist.id)
        assertEquals("Test Artist", artist.name)
        assertEquals(5, artist.songCount)
        assertEquals("https://example.com/artist.jpg", artist.thumbnailUrl)
    }
}
