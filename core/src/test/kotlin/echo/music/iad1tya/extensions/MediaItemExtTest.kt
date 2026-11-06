package echo.music.iad1tya.extensions

import echo.music.iad1tya.db.entities.ArtistEntity
import echo.music.iad1tya.db.entities.Song
import echo.music.iad1tya.db.entities.SongEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class MediaItemExtTest {

    @Test
    fun testLocalSongEntityStructure() {
        val localId = "content://media/external/audio/media/1"
        val songEntity = SongEntity(
            id = localId,
            title = "Test Song",
            duration = 180,
            isLocal = true
        )
        val artists = listOf(ArtistEntity(id = "a1", name = "Artist 1"))
        val song = Song(song = songEntity, artists = artists, album = null)

        assertEquals(localId, song.song.id)
        assertEquals("Test Song", song.song.title)
        assertEquals(true, song.song.isLocal)
    }
}
